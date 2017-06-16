package japicmp.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for String Helper
 */
public class StringHelperTest {
	@Test
	public void testConvertCamelCaseToSnakeUpperCaseWithUnderScore() throws Exception {

		String actual = StringHelper.convertCamelCaseToSnakeUpperCase("pathParam");
		assertEquals("PATH_PARAM", actual);
	}

	@Test
	public void testConvertCamelCaseToSnakeUpperCaseWithNoUnderScore() throws Exception {
		String actual = StringHelper.convertCamelCaseToSnakeUpperCase("pathparam");
		assertEquals("PATHPARAM", actual);
	}

	@Test
	public void testConvertCamelCaseToSnakeUpperCaseNullValue() throws Exception {
		String actual = StringHelper.convertCamelCaseToSnakeUpperCase(null);
		assertEquals("", actual);
	}

	@Test
	public void testConvertCamelCaseToSnakeUpperCaseEmptyValue() throws Exception {
		String actual = StringHelper.convertCamelCaseToSnakeUpperCase("");
		assertEquals("", actual);
	}

	@Test
	public void testSplitAndGetLastNotEmpty() {
		String actual = StringHelper.splitAndGetLast("javax.ws.rs.PathParam", StringHelper.DOT_REGEXP);
		assertEquals("PathParam", actual);
	}
	@Test
	public void testSplitAndGetLastWhitoutSeparator() {
		String actual = StringHelper.splitAndGetLast("Test", StringHelper.DOT_REGEXP);
		assertEquals("Test", actual);
	}
	@Test
	public void testSplitAndGetLastNullValue() {
		String actual = StringHelper.splitAndGetLast(null, StringHelper.DOT_REGEXP);
		assertEquals("", actual);
	}
	@Test
	public void testSplitAndGetLastEmptyValue() {
		String actual = StringHelper.splitAndGetLast("", StringHelper.DOT_REGEXP);
		assertEquals("", actual);
	}
}
