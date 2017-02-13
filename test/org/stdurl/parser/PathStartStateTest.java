package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.helpers.StringHelper;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class PathStartStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.PATH_START_STATE, 0);
		MachineURLParts parts1 = u("test");
		MachineURLParts parts2 = u("http");

		testCompare(cond, p("\\"), true, parts2, parts2, context,
				c(ParserStates.PATH_STATE, 0));
		testCompare(cond, p("/"), false, parts2, parts2, context,
				c(ParserStates.PATH_STATE, 0));
		for (int i = 0; i < 128; ++i) {
			if ("/\\".indexOf(i) != -1) continue;
			String str = StringHelper.toString(i);
			testCompare(cond, p(str), false, parts2, parts2, context,
					c(ParserStates.PATH_STATE, -1));
		}

		testCompare(cond, p("?"), false, parts1,
				a(u("test"), "", null),
				context, c(ParserStates.QUERY_STATE, 0));
		testCompare(cond, p("#"), false, parts1,
				a(u("test"), null, ""),
				context, c(ParserStates.FRAGMENT_STATE, 0));
		testCompare(cond, p(""), false, parts1, parts1, context, context);
		testCompare(cond, p("/"), false, parts1, parts1, context,
				c(ParserStates.PATH_STATE, 0));
		for (int i = 1; i < 128; ++i) {
			if ("?#/".indexOf(i) != -1) continue;
			testCompare(cond, p(StringHelper.toString(i)), false, parts1, parts1,
					context, c(ParserStates.PATH_STATE, -1));
		}
	}
}
