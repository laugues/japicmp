package japicmp.cmp;

import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiMethod;
import japicmp.util.CtAnnotation;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import javax.ws.rs.Path;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static japicmp.util.Helper.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AnnotationsTest {

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD})
	public @interface Include {

	}

	@Test
	public void testNoAnnotationsClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {

			private CtAnnotation ctAnnotation = new CtAnnotation().setName(Include.class.getName());

			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(ctAnnotation).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").withAnnotation(ctAnnotation).addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		assertThat(jApiClass.getAnnotations().size(), is(0));
	}

	@Test
	public void testNoAnnotationsMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtAnnotation ctAnnotationMethod = new CtAnnotation().setName(Include.class.getName());
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel").withAnnotation(ctAnnotationMethod).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtAnnotation ctAnnotationMethod = new CtAnnotation().setName(Include.class.getName());
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel").withAnnotation(ctAnnotationMethod).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "excel");
		assertThat(jApiMethod.getAnnotations().size(), is(0));
	}

	@Test
	public void testJavaxAnnotationMethod() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {

			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {

				Map<String, String> pathAnnotationPropertiesClass1 = new HashMap<>();
				pathAnnotationPropertiesClass1.put("value", "/sheldon");
				CtAnnotation ctAnnotationPatchClass1 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationPropertiesClass1);
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.rest.api.Sheldon").withAnnotation(ctAnnotationPatchClass1).addToClassPool(classPool);

				Map<String, String> pathAnnotationPropertiesCl1Method1 = new HashMap<>();
				pathAnnotationPropertiesCl1Method1.put("value", "/{id}");
				CtAnnotation ctAnnotationPatchClass1Method1 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationPropertiesCl1Method1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("getSheldon").withAnnotation(ctAnnotationPatchClass1Method1).addToClass(ctClass1);

				Map<String, String> pathAnnotationProperties2 = new HashMap<>();
				pathAnnotationProperties2.put("value", "/leonard");
				CtAnnotation ctAnnotationPathClass2 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationProperties2);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.rest.api.Leonard").withAnnotation(ctAnnotationPathClass2).addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {

				Map<String, String> pathAnnotationProperties1 = new HashMap<>();
				pathAnnotationProperties1.put("value", "/sheldonUpdatedPath");
				CtAnnotation ctAnnotation1 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationProperties1);
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.rest.api.Sheldon").withAnnotation(ctAnnotation1).addToClassPool(classPool);

				Map<String, String> pathAnnotationPropertiesCl1Method1 = new HashMap<>();
				pathAnnotationPropertiesCl1Method1.put("value", "/{id}");
				CtAnnotation ctAnnotationPatchClass1Method1 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationPropertiesCl1Method1);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("getSheldon").withAnnotation(ctAnnotationPatchClass1Method1).addToClass(ctClass1);

				Map<String, String> pathAnnotationProperties2 = new HashMap<>();
				pathAnnotationProperties2.put("value", "/leonard");
				CtAnnotation ctAnnotation2 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationProperties2);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.rest.api.Leonard").withAnnotation(ctAnnotation2).addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});

		assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.rest.api.Sheldon");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "getSheldon");
		assertThat(jApiMethod.getAnnotations().size(), is(0));
	}

	@Test
	public void testNoAnnotationsField() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setNoAnnotations(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtFieldBuilder.create().name("age").type(classPool.getCtClass(String.class.getName())).withAnnotation(Include.class.getName()).addToClass(ctClass1);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
		assertThat(jApiClasses.size(), is(2));
		JApiClass jApiClass = getJApiClass(jApiClasses, "big.bang.theory.Sheldon");
		JApiField jApiField = getJApiField(jApiClass.getFields(), "age");
		assertThat(jApiField.getAnnotations().size(), is(0));
	}
}
