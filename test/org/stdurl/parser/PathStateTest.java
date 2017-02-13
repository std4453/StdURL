package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.percent.DefaultEncodeSet;
import org.stdurl.percent.PercentEncoder;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class PathStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.PATH_STATE, 0);
		MachineURLParts parts1 = u("test");
		MachineURLParts parts2 = u("http");

		for (int i = 1; i < 128; ++i) {
			if ("/\\%".indexOf(i) != -1) continue;
			boolean veExpected = !CodePointHelper.isURLCodePoint(i);
			String orig = StringHelper.toString(i);
			String result = PercentEncoder.utf8Encode(i, DefaultEncodeSet.instance);
			MachineParameters params = p(orig);
			if ("?#".indexOf(i) != -1) params = p(orig, ParserStates.PATH_STATE);
			testCompare(cond, params, veExpected, parts1, parts1, context,
					c(context, result));
		}
		String[] testCases = new String[]{"%", "%%", "%a", "%a%"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase), true, parts1, parts1, context,
					c(context, "%"));
		testCompare(cond, p("%aa"), false, parts1, parts1, context, c(context, "%"));

		testCompare(cond, p(""), false,
				parts1, a(parts1, "abc"), c(context, "abc"), context);
		testCompare(cond, p("?"), false,
				parts1, a(a(parts1, "abc"), "", null),
				c(context, "abc"), c(ParserStates.QUERY_STATE, 0));
		testCompare(cond, p("#"), false,
				parts1, a(a(parts1, "abc"), null, ""),
				c(context, "abc"), c(ParserStates.FRAGMENT_STATE, 0));

		testCases = new String[]{"/", "\\"};
		for (String testCase : testCases) {
			MachineURLParts parts = "\\".equals(testCase) ? parts2 : parts1;
			MachineURLParts startParts = a(parts, "a/b/c");
			boolean veExpected = "\\".equals(testCase);
			MachineParameters params = p(testCase);
			testCompare(cond, params, veExpected, startParts, a(parts, "a/b"),
					c(context, ".."), context);
			testCompare(cond, params, veExpected, startParts, a(parts, "a/b/c"),
					c(context, "."), context);
			testCompare(cond, params, veExpected, startParts, a(parts, "a/b/c/d"),
					c(context, "d"), context);
		}
		MachineURLParts startParts = a(parts1, "a/b/c");
		MachineParameters params = p("");
		testCompare(cond, params, false, startParts, a(parts1, "a/b/"),
				c(context, ".."), context);
		testCompare(cond, params, false, startParts, a(parts1, "a/b/c/"),
				c(context, "."), context);
		testCompare(cond, params, false, startParts, a(parts1, "a/b/c/d"),
				c(context, "d"), context);

		testCompare(cond, params, true, u(u("file"), "example.com", -1),
				a(u("file"), "C:"), c(context, "C|"), context);
		testCompare(cond, params, true, u(u("file"), "example.com", -1),
				a(u("file"), "d:"), c(context, "d:"), context);
		testCompare(cond, params, false, u("file"),
				a(u("file"), "e:"), c(context, "e|"), context);
		testCompare(cond, params, false, u("file"),
				a(u("file"), "F:"), c(context, "F:"), context);
	}
}
