package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.ParserStateTestHelper.testTerminatedAndCompare;
import static org.stdurl.parser.TestShortenHelper.b;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class HostnameStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.HOSTNAME_STATE, 0);
		MachineURLParts parts1 = u("file");
		MachineURLParts parts2 = u("http");
		MachineURLParts parts3 = u("test");

		testCompare(cond, p("", ParserStates.SCHEME_START_STATE), false, parts1, parts1,
				context, c(ParserStates.FILE_HOST_STATE, -1));
		testFailure(cond, p(":"), parts1, context, true);
		testFailure(cond, p(":"), parts1, c(context, "["), true);
		testCompare(cond, p(":"), false, parts1,
				u(parts1, "example.com", -1), c(context, "example.com"),
				c(ParserStates.PORT_STATE, 0));
		testTerminatedAndCompare(cond, p(":", ParserStates.HOSTNAME_STATE), false,
				parts2, u(parts2, "example.com", -1), c(context, "example.com"),
				c(ParserStates.PORT_STATE, 0));

		String[] testCases = new String[]{"", "/", "?", "#", "\\"};
		for (String testCase : testCases) {
			MachineParameters params = p(testCase);

			testFailure(cond, params, parts2, context, true);
			if (!"\\".equals(testCase)) {
				MachineURLParts partsWithCredentials = c(parts3, "username", "password");
				testTerminatedAndCompare(cond,
						p(testCase, ParserStates.SCHEME_START_STATE), true,
						partsWithCredentials, partsWithCredentials, context,
						c(ParserStates.HOSTNAME_STATE, -1));
				MachineURLParts partsWithPort = u(parts3, "example.com", 8080);
				testTerminatedAndCompare(cond,
						p(testCase, ParserStates.SCHEME_START_STATE),
						true, partsWithPort, partsWithPort, context,
						c(ParserStates.HOSTNAME_STATE, -1));
			}
			testFailure(cond, params, parts2, c(context, "["), true);
			testCompare(cond, params, false, parts2, u(parts2, "example.com", -1),
					c(context, "example.com"), c(ParserStates.PATH_START_STATE, -1));
			testTerminatedAndCompare(cond, p(testCase, ParserStates.SCHEME_START_STATE),
					false, parts2, u(parts2, "example.com", -1),
					c(context, "example.com"), c(ParserStates.PATH_START_STATE, -1));
		}

		testCompare(cond, p("["), false, parts1, parts1, context, b(c(context, "[")));
		testCompare(cond, p("]"), false, parts1, parts1, b(context), c(context, "]"));
		testCompare(cond, p("a"), false, parts1, parts1, context, c(context, "a"));
	}
}
