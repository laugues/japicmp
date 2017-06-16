package japicmp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lds on 15/06/2017.
 */
public class StringHelper {

	public static final String CAMEL_CASE_PATTERN = "(?<=[a-z])[A-Z]";
	public static final String UNDERSCORE = "_";
	public static final String DOT_REGEXP = "\\.";

	private StringHelper() {

	}

	public static String convertCamelCaseToSnakeUpperCase(String value) {
		String result = "";
		if (value != null) {

			Matcher m = Pattern.compile(CAMEL_CASE_PATTERN).matcher(value);

			StringBuffer sb = new StringBuffer();
			while (m.find()) {
				m.appendReplacement(sb, UNDERSCORE + m.group().toLowerCase());
			}
			m.appendTail(sb);
			result = sb.toString().toUpperCase();
		}

		return result;
	}

	/**
	 * Split string and get last string of splitted strings
	 *
	 * @param value     the value to split
	 * @param separator the separator to process split
	 * @return the last character within the splitted list. If value is null the result is empty;
	 */
	public static String splitAndGetLast(String value, String separator) {
		String result = "";

		if (value != null) {
			String[] split = value.split(separator);
			if (split.length > 0) {
				result = split[split.length - 1];
			}
		}

		return result;
	}
}
