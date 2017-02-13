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
public class FileHostStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineURLParts parts = u();
		MachineContext context = c(ParserStates.FILE_HOST_STATE, 0);

		for (int i = 1; i < 128; ++i) {
			if ("/\\?#".indexOf(i) != -1) continue;
			String str = StringHelper.toString(i);
			testCompare(cond, p(str), false, parts, parts, context, c(context, str));
		}

		String[] testCases = new String[]{"", "/", "\\", "?", "#"};
		for (String testCase : testCases) {
			MachineParameters params = p(testCase);
			testCompare(cond, params, true, parts, parts,
					c(context, "C:"), c(ParserStates.PATH_STATE, -1, "C:"));
			MachineURLParts partsCredential = c(parts, "username", "password");
			testTerminatedAndCompare(cond, p(testCase, ParserStates.FILE_HOST_STATE),
					true, partsCredential, partsCredential,
					context, c(ParserStates.FILE_HOST_STATE, -1));
			testTerminatedAndCompare(cond, p(testCase, ParserStates.FILE_HOST_STATE),
					false, parts, u(parts, "", -1),
					context, c(ParserStates.FILE_HOST_STATE, -1));
			testCompare(cond, params, false, parts, u(parts, "", -1),
					context, c(ParserStates.PATH_START_STATE, -1));

			testFailure(cond, params, parts, c(context, "["), true);
			MachineContext endContext = c(ParserStates.PATH_START_STATE, -1);
			testCompare(cond, params, false, parts,
					u(parts, "", -1), c(context, "localhost"), endContext);
			testCompare(cond, params, false, parts,
					u(parts, "example.com", -1), c(context, "example.com"), endContext);
			testTerminatedAndCompare(cond, p(testCase, ParserStates.FILE_HOST_STATE),
					false, parts, u(parts, "example.com", -1), c(context, "example.com"),
					c(ParserStates.FILE_HOST_STATE, -1, "example.com"));
		}
	}
}
