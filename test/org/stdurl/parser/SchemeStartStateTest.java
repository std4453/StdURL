package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class SchemeStartStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.SCHEME_START_STATE, 0);
		MachineURLParts parts = u();
		int so = ParserStates.FRAGMENT_STATE;

		testCompare(cond, p("abc"), false, parts, parts, context,
				c(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, p("ABC"), false, parts, parts, context,
				c(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, p("123"), false, parts, parts, context,
				c(ParserStates.NO_SCHEME_STATE, -1));
		testCompare(cond, p("\u65b0"), false, parts, parts, context,
				c(ParserStates.NO_SCHEME_STATE, -1));
		testCompare(cond, p("abc", so), false, parts, parts, context,
				c(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, p("ABC", so), false, parts, parts, context,
				c(ParserStates.SCHEME_STATE, 0, "a"));
		testFailure(cond, p("123", so), parts, context, true);
		testFailure(cond, p("\u65b0", so), parts, context, true);
	}
}
