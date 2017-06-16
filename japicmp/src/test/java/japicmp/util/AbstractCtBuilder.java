package japicmp.util;

import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lds on 15/06/2017.
 */
public class AbstractCtBuilder {
	protected List<CtAnnotation> annotations = new ArrayList<>();

	protected void buildAnnotations(ClassFile classFile, ConstPool constPool, List<Annotation> annotationsToSet) {
		for (CtAnnotation ctAnnotation : annotations) {
			Annotation annot = new Annotation(ctAnnotation.getName(), constPool);
			for (Map.Entry<String, String> entry : ctAnnotation.getProperties().entrySet()) {
				annot.addMemberValue(entry.getKey(), new StringMemberValue(entry.getValue(), classFile.getConstPool()));
			}
			annotationsToSet.add(annot);
		}
	}
}
