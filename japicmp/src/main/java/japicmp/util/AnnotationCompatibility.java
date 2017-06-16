package japicmp.util;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiCompatibilityChange;

import java.util.Map;

/**
 * Annotation Compatibility. Map a annotation name to {@link JApiCompatibilityChange} by {@link JApiChangeStatus}
 */
public class AnnotationCompatibility {

	private String annotationName;
	private Map<JApiChangeStatus, JApiCompatibilityChange> jApiCompatibilityChanges;


	public AnnotationCompatibility() {

	}

	public String getAnnotationName() {
		return annotationName;
	}

	public AnnotationCompatibility setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
		return this;
	}

	public Map<JApiChangeStatus, JApiCompatibilityChange> getjApiCompatibilityChanges() {
		return jApiCompatibilityChanges;
	}

	public AnnotationCompatibility setjApiCompatibilityChanges(Map<JApiChangeStatus, JApiCompatibilityChange> jApiCompatibilityChanges) {
		this.jApiCompatibilityChanges = jApiCompatibilityChanges;
		return this;
	}
}
