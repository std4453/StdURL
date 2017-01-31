package test;

import org.stdurl.URL;
import org.stdurl.parser.BasicURLParser;

/**
 *
 */
public class Example {
	public static void main(String[] args) {
		boolean passed =
				test("https:example.org", null, "https://example.org/") &
						test("https://////example.com///", null,
								"https://example.com///") &
						test("https://example.com/././foo", null,
								"https://example.com/foo") &
						test("hello:world", "https://example.com", "hello:world") &
						test("https:example.org", "https://example.com",
								"https://example.com/example.org") &
						test("\\example\\..\\demo/.\\", "https://example.com",
								"https://example.com/demo/") &
						test("example", "https://example.com/demo",
								"https://example.com/example") &
						test("example", null, null) &
						test("https://example.com:demo", null, null) &
						test("http://[www.example.com]/", null, null) &
						test("http://www.example.com/新年快乐", null,
								"http://www.example" +
										".com/%E6%96%B0%E5%B9%B4%E5%BF%AB%E4%B9%90") &
						test("http://www.example.com/新年 快乐", null,
								"http://www.example" +
										".com/%E6%96%B0%E5%B9%B4%20%E5%BF%AB%E4%B9%90") &
						test("http://www.example.com/%%%%", null,
								"http://www.example.com/%%%%") &
						test("http://www.example.com/%29%%29%", null,
								"http://www.example.com/%29%%29%");

		if (passed)
			System.out.println("All passed.");
	}

	private static boolean test(String url, String base, String result) {
		URL u = parse(url, base);

		boolean passed;
		if (result == null)
			passed = u == null || u.isFailure();
		else passed = result.equals(u.toString());

		if (!passed) {
			System.err.printf("\tTest not passed for url \"%s\" and base \"%s\":\n" +
							"\tExpected: \"%s\", Got: \"%s\"\n",
					url, base == null ? "NO BASE" : base,
					result == null ? "FAILURE" : result, u.toString());
		} else {
			System.out.printf("\tTest passed for url \"%s\" and base \"%s\":\n" +
							"\tResult is: \"%s\"\n",
					url, base == null ? "NO BASE" : base,
					result == null ? "FAILURE" : result);
		}

		return passed;
	}

	private static URL parse(String url, String base) {
		if (base == null) {
			return BasicURLParser.parse(url);
		} else {
			URL baseUrl = BasicURLParser.parse(base);
			if (baseUrl == null || baseUrl.isFailure()) return URL.failure;
			return BasicURLParser.parse(url, baseUrl);
		}
	}
}
