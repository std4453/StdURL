package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.helpers.StringHelper;
import org.stdurl.percent.PercentEncoder;
import org.stdurl.percent.UserinfoEncodeSet;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class AuthorityStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.AUTHORITY_STATE, 0);
		MachineURLParts parts1 = u("http");
		MachineURLParts parts2 = u("test");

		for (int i = 1; i < 128; ++i) {
			if ("/?#@\\".indexOf(i) != -1) continue;

			String str = StringHelper.toString(i);
			MachineParameters params = p(str);
			MachineContext endContext = c(ParserStates.AUTHORITY_STATE, 0, str);
			testCompare(cond, params, false, parts1, parts1, context, endContext);
		}

		String[] testCases = new String[]{"", "/", "?", "#", "\\"};
		MachineContext context1 = c(ParserStates.AUTHORITY_STATE, 0, "abc");
		for (String testCase : testCases) {
			MachineURLParts parts = testCase.equals("\\") ? parts1 : parts2;
			MachineParameters params = p(testCase);
			testFailure(cond, params, parts, a(context), true);
			testCompare(cond, params, false, parts, parts,
					context1, c(ParserStates.HOST_STATE, -4));
			testCompare(cond, params, false, parts, parts,
					a(context1), a(c(ParserStates.HOST_STATE, -4)));
		}

		// @
		testCompare(cond, p("@"), true, parts1, c(parts1, "abc", ""),
				c(ParserStates.AUTHORITY_STATE, 0, "abc"), a(context));
		testCompare(cond, p("@"), true, parts1, c(parts1, "%40abc", ""),
				a(c(ParserStates.AUTHORITY_STATE, 0, "abc")), a(context));
		testCompare(cond, p("@"), true, parts1, c(parts1, "username", "password"),
				c(ParserStates.AUTHORITY_STATE, 0, "username:password"), p(a(context)));
		String str = "\u65b0/;=@[\\]^|\"#<>?{}azAZ09";
		String encoded = PercentEncoder.utf8Encode(StringHelper.toCodePoints(str),
				UserinfoEncodeSet.instance);
		testCompare(cond, p("@"), true, parts1, c(parts1, encoded, ""),
				c(ParserStates.AUTHORITY_STATE, 0, str), a(context));
	}
}
