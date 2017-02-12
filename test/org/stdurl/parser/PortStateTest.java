package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.helpers.StringHelper;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.ParserStateTestHelper.testTerminatedAndCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class PortStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.PORT_STATE, 0);
		MachineURLParts parts1 = u("test");
		MachineURLParts parts2 = u("http");

		for (int i = 0; i < 10; ++i) {
			String str = "" + i;
			testCompare(cond, p(str), false, parts1, parts1, context, c(context, str));
		}

		for (int i = 1; i < 128; ++i) {
			if ("/?#\\0123456789".indexOf(i) != -1) continue;
			String str = StringHelper.toString(i);
			testFailure(cond, p(str), parts1, context, true);
		}

		String[] testCases = new String[]{"", "/", "?", "#", "\\"};
		for (String testCase : testCases) {
			MachineParameters params = p(testCase);
			testFailure(cond, params, parts1, c(context, "65536"), true);
			if (!"\\".equals(testCase))
				testCompare(cond, params, false, parts1, u(parts1, null, 80),
						c(context, "80"), c(ParserStates.PATH_START_STATE, -1));
			testCompare(cond, params, false, parts2, parts2,
					c(context, "80"), c(ParserStates.PATH_START_STATE, -1));
			testCompare(cond, params, false, parts2, u(parts2, null, 8080),
					c(context, "8080"), c(ParserStates.PATH_START_STATE, -1));
		}

		testFailure(cond, p(""), parts1, c(context, "65536"), true);
		testTerminatedAndCompare(cond, p("", ParserStates.PORT_STATE), false,
				parts1, u(parts1, null, 80), c(context, "80"), context);
		testTerminatedAndCompare(cond, p("", ParserStates.PORT_STATE), false,
				parts2, parts2, c(context, "80"), context);
		testTerminatedAndCompare(cond, p("", ParserStates.PORT_STATE), false,
				parts2, u(parts2, null, 8080), c(context, "8080"), context);
	}
}
