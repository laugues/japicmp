package japicmp.cmp;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.util.CtAnnotation;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassesComparatorTest {

	public static final String CLASS_NAMTE_TEST = "japicmp.Test";

	@Test
	public void testMethodAdded() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(), new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithoutMethod(classPool));
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithMethod(classPool));
			}
		});
		assertThat(jApiClasses.size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().get(0).getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClasses.get(0).getMethods().get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	@Test
	public void testAnnotationMethodUpdated() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setActivateRestCompatibility(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithRestPathAndRestMethod(classPool, "/persons", "/byId", GET.class.getName()));
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithRestPathAndRestMethod(classPool, "/boys", "/byId", PUT.class.getName()));
			}
		});
		assertThat(jApiClasses.size(), is(1));

		//Methods
		assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClasses.get(0).getMethods().get(0).isBinaryCompatible(), is(false));

		//Annotations
		assertThat(jApiClasses.get(0).getAnnotations().size(), is(1));
		assertThat(jApiClasses.get(0).getAnnotations().get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClasses.get(0).getAnnotations().get(0).isBinaryCompatible(), is(false));


		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(false));
		assertThat(jApiClasses.get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(false));
	}


	@Test
	public void testAnnotationPathUpdatedRestDisable() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setActivateRestCompatibility(false);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithRestPathAndRestMethod(classPool, "/persons", "/byId", GET.class.getName()));
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithRestPathAndRestMethod(classPool, "/persons", "/byIds", GET.class.getName()));
			}
		});
		assertThat(jApiClasses.size(), is(1));

		//Methods
		assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().get(0).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClasses.get(0).getMethods().get(0).isBinaryCompatible(), is(true));

		//Annotations
		assertThat(jApiClasses.get(0).getAnnotations().size(), is(1));
		assertThat(jApiClasses.get(0).getAnnotations().get(0).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClasses.get(0).getAnnotations().get(0).isBinaryCompatible(), is(true));


		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}


	private CtClass createClassWithoutMethod(ClassPool classPool) {
		return new CtClassBuilder().name(CLASS_NAMTE_TEST).addToClassPool(classPool);
	}

	private CtClass createClassWithRestPathAndRestMethod(ClassPool classPool, String classPathName, String methodPathName, String httpMethodName) throws NotFoundException, CannotCompileException {

		Map<String, String> pathAnnotationPropertiesClass1 = new HashMap<>();
		pathAnnotationPropertiesClass1.put("value", classPathName);
		CtAnnotation ctAnnotationPatchClass1 = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationPropertiesClass1);
		CtClass ctClass1 = CtClassBuilder.create().name(CLASS_NAMTE_TEST).withAnnotation(ctAnnotationPatchClass1).addToClassPool(classPool);

		Map<String, String> pathAnnotationPropertiesClass1Method1 = new HashMap<>();
		pathAnnotationPropertiesClass1Method1.put("value", methodPathName);

		CtAnnotation methodPathNameAnnotation = new CtAnnotation().setName(Path.class.getName()).setProperties(pathAnnotationPropertiesClass1Method1);
		CtAnnotation httpMethodAnnotation = new CtAnnotation().setName(httpMethodName);

		CtMethodBuilder.create().publicAccess()
			.returnType(CtClass.voidType)
			.name("getSheldon")
			.withAnnotation(methodPathNameAnnotation)
			.withAnnotation(httpMethodAnnotation)
			.addToClass(ctClass1);

		return ctClass1;
	}

	private CtClass createClassWithMethod(ClassPool classPool) throws NotFoundException, CannotCompileException {
		CtClass ctClass = createClassWithoutMethod(classPool);
		CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
		return ctClass;
	}
}
