package japicmp.util;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiCompatibilityChange;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;
import org.reflections.Reflections;

import javax.ws.rs.HttpMethod;
import java.util.*;
import java.util.logging.Logger;

public class AnnotationHelper {
	private static final Logger LOGGER = Logger.getLogger(AnnotationHelper.class.getName());
	private static final String JAVAX_WS_PACKAGE = "javax.ws";
	public static final String START_COMPATIBILITY_CHANGE_NAME = "REST_HTTP_";

	private static List<String> REST_ANNOTATIONS = new ArrayList<>();
	private static List<AnnotationCompatibility> ANNOTATION_COMPATIBILITIES = new ArrayList<>();
	private static List<String> EXCLUDED_ANNOTATIONS = new ArrayList<>();

	static {
		//Add HttpMethod annotations to the list of excluded annotations to process
		EXCLUDED_ANNOTATIONS.add(HttpMethod.class.getName());
		REST_ANNOTATIONS = getAllRestAnnotations();
		buildRestAnnotationCompatibilities();
	}

	public interface AnnotationsAttributeCallback<T> {

		AnnotationsAttribute getAnnotationsAttribute(T t);
	}

	/**
	 * Does the provided {@link JApiAnnotation} is the REST annotation (wihtin the package javax.ws)
	 *
	 * @param jApiAnnotation the {@link JApiAnnotation} to process
	 * @return true if the provided {@link JApiAnnotation} is the REST annotation else false.
	 */
	public static boolean isRestAnnotation(JApiAnnotation jApiAnnotation) {
		//check old annotation
		Optional<Annotation> oldAnnotation = jApiAnnotation.getOldAnnotation();
		boolean result = checkIsRestAnnotation(oldAnnotation);

		//if old it's already rest annotation does not process the test for new annotation (keep the result to true)
		if (!result) {
			Optional<Annotation> newAnnotation = jApiAnnotation.getNewAnnotation();
			result = checkIsRestAnnotation(newAnnotation);
		}
		return result;
	}


	/**
	 * Build list of {@link AnnotationCompatibility} from list of {@link #REST_ANNOTATIONS}
	 */
	private static void buildRestAnnotationCompatibilities() {
		for (String annotation : REST_ANNOTATIONS) {


			String annotationSimpleName = StringHelper.splitAndGetLast(annotation, StringHelper.DOT_REGEXP);
			String name = StringHelper.convertCamelCaseToSnakeUpperCase(annotationSimpleName);
			Map<JApiChangeStatus, JApiCompatibilityChange> jApiCompatibilityChanges = new HashMap<>();
			String jApiCompatibilityChangeName = null;
			AnnotationCompatibility annotationCompatibility = new AnnotationCompatibility().setAnnotationName(annotation);

			for (JApiChangeStatus changeStatus : JApiChangeStatus.values()) {
				if (changeStatus.equals(JApiChangeStatus.UNCHANGED)) {
					continue;
				}
				try {
					String statusName = changeStatus.name();
					if (JApiChangeStatus.NEW.equals(changeStatus)) {
						statusName = "ADDED";
					}

					jApiCompatibilityChangeName = START_COMPATIBILITY_CHANGE_NAME + name + StringHelper.UNDERSCORE + statusName;
					jApiCompatibilityChanges.put(changeStatus, JApiCompatibilityChange.valueOf(jApiCompatibilityChangeName));
				} catch (IllegalArgumentException e) {
					LOGGER.warning("Unable to build jApiCompatibilityChanger from name [" + jApiCompatibilityChangeName + "]");
					continue;
				}
				annotationCompatibility.setjApiCompatibilityChanges(jApiCompatibilityChanges);
			}
			ANNOTATION_COMPATIBILITIES.add(annotationCompatibility);
		}
	}

	/**
	 * Get {@link JApiCompatibilityChange} from an {@link JApiAnnotation}
	 *
	 * @param jApiAnnotation the annotation to process
	 * @return the compatibility change
	 */
	public static JApiCompatibilityChange getJApiCompatibilityChange(JApiAnnotation jApiAnnotation) {
		JApiCompatibilityChange result = null;

		if (AnnotationHelper.isRestAnnotation(jApiAnnotation)) {

			//if japiannotation and change status are not null. And the status of annotation is not UNCHANGED
			if (jApiAnnotation != null && jApiAnnotation.getChangeStatus() != null && !JApiChangeStatus.UNCHANGED.equals(jApiAnnotation.getChangeStatus())) {

				for (AnnotationCompatibility annotationCompatibility : ANNOTATION_COMPATIBILITIES) {
					if (!annotationCompatibility.getAnnotationName().equals(jApiAnnotation.getFullyQualifiedName())) {
						continue;
					}

					if (annotationCompatibility.getjApiCompatibilityChanges() == null) {
						continue;
					}

					JApiCompatibilityChange jApiCompatibilityChange = annotationCompatibility.getjApiCompatibilityChanges().get(jApiAnnotation.getChangeStatus());
					if (jApiCompatibilityChange != null) {
						result = jApiCompatibilityChange;
					}
				}
			}

		}
		return result;
	}

	/**
	 * Get all REST annotations which are in package {@link #JAVAX_WS_PACKAGE}
	 *
	 * @return the list of REST annotation
	 */
	private static List<String> getAllRestAnnotations() {
		List<String> classNames = new ArrayList<>();

		Set<Class<? extends java.lang.annotation.Annotation>> subTypesOfAnnotations =
			new Reflections(JAVAX_WS_PACKAGE).getSubTypesOf(java.lang.annotation.Annotation.class);

		for (Class clazz : subTypesOfAnnotations) {
			//does not add the excluded annotations
			if (EXCLUDED_ANNOTATIONS.contains(clazz.getName())) {
				continue;
			}
			classNames.add(clazz.getName());
		}
		return classNames;
	}

	private static boolean checkIsRestAnnotation(Optional<Annotation> annotation) {
		return annotation.isPresent() && REST_ANNOTATIONS.contains(annotation.get().getTypeName());
	}

	public static <T> void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<T> oldClassOptional, Optional<T> newClassOptional,
													JarArchiveComparatorOptions options, AnnotationsAttributeCallback<T> annotationsAttributeCallback) {
		if (!options.isNoAnnotations()) {
			if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
				T oldClass = oldClassOptional.get();
				T newClass = newClassOptional.get();
				AnnotationsAttribute oldAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(oldClass);
				AnnotationsAttribute newAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(newClass);
				Map<String, Annotation> oldAnnotationMap;
				Map<String, Annotation> newAnnotationMap;
				if (oldAnnotationsAttribute != null) {
					oldAnnotationMap = buildAnnotationMap(oldAnnotationsAttribute.getAnnotations());
				} else {
					oldAnnotationMap = new HashMap<>();
				}
				if (newAnnotationsAttribute != null) {
					newAnnotationMap = buildAnnotationMap(newAnnotationsAttribute.getAnnotations());
				} else {
					newAnnotationMap = new HashMap<>();
				}
				for (Annotation annotation : oldAnnotationMap.values()) {
					Annotation foundAnnotation = newAnnotationMap.get(annotation.getTypeName());
					if (foundAnnotation != null) {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.of(foundAnnotation), JApiChangeStatus.UNCHANGED);
						annotations.add(jApiAnnotation);
					} else {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.<Annotation>absent(), JApiChangeStatus.REMOVED);
						annotations.add(jApiAnnotation);
					}
				}
				for (Annotation annotation : newAnnotationMap.values()) {
					Annotation foundAnnotation = oldAnnotationMap.get(annotation.getTypeName());
					if (foundAnnotation == null) {
						JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.<Annotation>absent(), Optional.of(annotation), JApiChangeStatus.NEW);
						annotations.add(jApiAnnotation);
					}
				}
			} else {
				if (oldClassOptional.isPresent()) {
					T oldClass = oldClassOptional.get();
					AnnotationsAttribute oldAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(oldClass);
					if (oldAnnotationsAttribute != null) {
						Map<String, Annotation> oldAnnotationMap = buildAnnotationMap(oldAnnotationsAttribute.getAnnotations());
						for (Annotation annotation : oldAnnotationMap.values()) {
							JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.of(annotation), Optional.<Annotation>absent(),
								JApiChangeStatus.REMOVED);
							annotations.add(jApiAnnotation);
						}
					}
				}
				if (newClassOptional.isPresent()) {
					T newClass = newClassOptional.get();
					AnnotationsAttribute newAnnotationsAttribute = annotationsAttributeCallback.getAnnotationsAttribute(newClass);
					if (newAnnotationsAttribute != null) {
						Map<String, Annotation> newAnnotationMap = buildAnnotationMap(newAnnotationsAttribute.getAnnotations());
						for (Annotation annotation : newAnnotationMap.values()) {
							JApiAnnotation jApiAnnotation = new JApiAnnotation(annotation.getTypeName(), Optional.<Annotation>absent(), Optional.of(annotation), JApiChangeStatus.NEW);
							annotations.add(jApiAnnotation);
						}
					}
				}
			}
		}
	}

	private static Map<String, Annotation> buildAnnotationMap(Annotation[] annotations) {
		Map<String, Annotation> map = new HashMap<>();
		for (Annotation annotation : annotations) {
			map.put(annotation.getTypeName(), annotation);
		}
		return map;
	}

	public static boolean hasAnnotation(ClassFile classFile, String annotationClassName) {
		List attributes = classFile.getAttributes();
		if (hasAnnotation(attributes, annotationClassName)) {
			return true;
		}
		return false;
	}

	public static boolean hasAnnotation(List attributes, String annotationClassName) {
		for (Object obj : attributes) {
			if (obj instanceof AnnotationsAttribute) {
				AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) obj;
				Annotation[] annotations = annotationsAttribute.getAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.getTypeName().equals(annotationClassName)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
