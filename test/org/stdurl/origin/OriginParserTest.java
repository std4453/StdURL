package org.stdurl.origin;

import org.junit.Test;
import org.stdurl.URL;
import org.stdurl.parser.BasicURLParser;
import org.stdurl.parser.ISyntaxViolationListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class OriginParserTest {
	/**
	 * Whether this test passes or not strongly depends on whether tests in
	 * {@link org.stdurl.URLParserTest} pass. Possibilities are big where this test
	 * here fails when those in {@link org.stdurl.URLParserTest} fail.
	 */
	@Test
	public void testParse() {
		// blob (test case from URL Standard)
		test("blob:https://whatwg.org/d0360e2f-caee-469f-9a2f-87d5b0456f6f",
				"https://whatwg.org:443");
		test("blob:http://www.example.com:8080/a/b/c", "http://www.example.com:8080");

		// ftp, gopher, http, https, ws, wss
		test("ftp://www.example.com/a/b/c", "ftp://www.example.com:21");
		test("ftp://www.example.com:123/a/b/c", "ftp://www.example.com:123");
		test("gopher://www.example.com/a/b/c", "gopher://www.example.com:70");
		test("gopher://www.example.com:123/a/b/c", "gopher://www.example.com:123");
		test("http://www.example.com/a/b/c", "http://www.example.com:80");
		test("http://www.example.com:123/a/b/c", "http://www.example.com:123");
		test("https://www.example.com/a/b/c", "https://www.example.com:443");
		test("https://www.example.com:123/a/b/c", "https://www.example.com:123");
		test("ws://www.example.com/a/b/c", "ws://www.example.com:80");
		test("ws://www.example.com:123/a/b/c", "ws://www.example.com:123");
		test("wss://www.example.com/a/b/c", "wss://www.example.com:443");
		test("wss://www.example.com:123/a/b/c", "wss://www.example.com:123");

		// file (currently returns only an opaque origin)
		test("file:///C:\\Users\\Administrator\\Desktop\\desktop.ini", null);

		// other schemes
		test("fakeScheme://fakeHost/fakePath/fakeFile", null);
	}

	private static void test(String url, String origin) {
		String msg = "Input: " + url;

		URL parsedURL = BasicURLParser.parse(url, (ISyntaxViolationListener) null);
		assertNotEquals(msg, null, parsedURL);
		assertFalse(msg, parsedURL.isFailure());

		Origin parsedOrigin = OriginParser.parse(parsedURL);
		assertNotEquals(msg, null, parsedOrigin);
		if (origin == null)
			assertTrue(msg, parsedOrigin.isOpaqueOrigin());
		else {
			assertFalse(msg, parsedOrigin.isOpaqueOrigin());
			assertEquals(parsedOrigin.toString(), origin);
		}
	}
}
