package japicmp.util;

import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChange;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test for annotation helper
 */
public class AnnotationHelperTest {


	@Test
	public void test() {
		assertEquals(JApiCompatibilityChange.valueOf("REST_HTTP_PATH_PARAM_MODIFIED"), JApiCompatibilityChange.REST_HTTP_PATH_PARAM_MODIFIED);
	}

	@Test
	public void isRestAnnotation() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setActivateRestCompatibility(true);
		List<JApiClass> jApiClasses = buildClasses(options);

		assertEquals(2, jApiClasses.size());
		JApiClass jApiClass = jApiClasses.get(1);
		List<JApiAnnotation> jApiAnnotations = jApiClass.getAnnotations();
		assertEquals(2, jApiAnnotations.size());

		JApiAnnotation jApiAnnotationNotRest = jApiAnnotations.get(0);
		assertNotNull(jApiAnnotationNotRest);
		boolean actual = AnnotationHelper.isRestAnnotation(jApiAnnotationNotRest);
		assertFalse("The result should be false", actual);

		JApiAnnotation jApiAnnotationRest = jApiAnnotations.get(1);
		assertNotNull(jApiAnnotationRest);
		boolean actualRest = AnnotationHelper.isRestAnnotation(jApiAnnotationRest);
		assertTrue("Annotation [" + jApiAnnotationRest.getFullyQualifiedName() + "] should be rest annotation", actualRest);
	}


	@Test
	public void testGetJApiCompatibilityChange() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setActivateRestCompatibility(true);
		List<JApiClass> jApiClasses = buildClasses(options);
		assertEquals(2, jApiClasses.size());
		List<JApiAnnotation> jApiAnnotations = jApiClasses.get(1).getAnnotations();

		assertEquals(2, jApiClasses.size());
		JApiAnnotation jApiAnnotation2 = jApiAnnotations.get(1);
		JApiCompatibilityChange actual1 = AnnotationHelper.getJApiCompatibilityChange(jApiAnnotation2);
		assertEquals(JApiCompatibilityChange.REST_HTTP_PATH_REMOVED, actual1);
	}

	protected List<JApiClass> buildClasses(JarArchiveComparatorOptions options) throws Exception {
		return japicmp.cmp.ClassesHelper.compareClasses(options, new japicmp.cmp.ClassesHelper.ClassesGenerator() {

			private CtAnnotation ctRestAnnotation = new CtAnnotation().setName(Path.class.getName());
			private CtAnnotation ctNotRestAnnotation = new CtAnnotation().setName(Inject.class.getName());

			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {

				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
					.withAnnotation(ctRestAnnotation)
					.withAnnotation(ctNotRestAnnotation)
					.addToClassPool(classPool);

				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
				CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
				return Arrays.asList(ctClass1, ctClass2);
			}
		});
	}

}
