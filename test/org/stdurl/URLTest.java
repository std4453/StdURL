package org.stdurl;

import org.junit.Test;
import org.stdurl.parser.BasicURLParser;
import org.stdurl.parser.SyntaxViolationListener;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class URLTest {
	@Test
	public void testStandard() {
		this.test("https:example.org", null, "https://example.org/");
		this.test("https://////example.com///", null, "https://example.com///");
		this.test("https://example.com/././foo", null, "https://example.com/foo");
		this.test("hello:world", "https://example.com", "hello:world");
		this.test("https:example.org", "https://example.com",
				"https://example.com/example.org");
		this.test("\\example\\..\\demo/.\\", "https://example.com",
				"https://example.com/demo/");
		this.test("example", "https://example.com/demo", "https://example.com/example");
		this.test("example", null, null);
		this.test("https://example.com:demo", null, null);
		this.test("http://[www.example.com]/", null, null);
	}

	@Test
	public void testChinese() {
		this.test("http://www.example.com/\u65b0\u5e74\u5feb\u4e50", null,
				"http://www.example.com/%E6%96%B0%E5%B9%B4%E5%BF%AB%E4%B9%90");
		this.test("http://www.example.com/\u65b0\u5e74 \u5feb\u4e50", null,
				"http://www.example.com/%E6%96%B0%E5%B9%B4%20%E5%BF%AB%E4%B9%90");
		this.test("http://www.example.com/%%%%", null, "http://www.example.com/%%%%");
		this.test("http://www.example.com/%29%%29%", null,
				"http://www.example.com/%29%%29%");
	}

	private void test(String url, String base, String result) {
		URL u = this.parse(url, base);
		String uStr = "FAILURE";
		if (u != null && !u.isFailure()) uStr = u.toString();

		if (result == null)
			assertEquals("Result should be FAILURE.", uStr, "FAILURE");
		else {
			String msg = String.format("Input=\"%s\", base=\"%s\"", url,
					base == null ? "NO BASE" : base);
			assertEquals(msg, result, uStr);
		}
	}

	private URL parse(String url, String base) {
		if (base == null) {
			return BasicURLParser.parse(url, (SyntaxViolationListener) null);
		} else {
			URL baseUrl = BasicURLParser.parse(base, (SyntaxViolationListener) null);
			if (baseUrl == null || baseUrl.isFailure()) return URL.failure;
			return BasicURLParser.parse(url, baseUrl, (SyntaxViolationListener) null);
		}
	}
}
