package japicmp.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lds on 14/06/2017.
 */
public class CtAnnotation {

	private String name;
	private Map<String, String> properties;

	public CtAnnotation() {
		properties = new HashMap<>();
	}

	public String getName() {
		return name;
	}

	public CtAnnotation setName(String name) {
		this.name = name;
		return this;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public CtAnnotation setProperties(Map<String, String> properties) {
		this.properties = properties;
		return this;
	}
}

