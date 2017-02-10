package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class SpecialRelativeOrAuthorityStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineURLParts parts = u();
		MachineContext context = c(ParserStates.SPECIAL_RELATIVE_OR_AUTHORITY_STATE, 5);

		// 1. c is / and remaining starts with /
		testCompare(cond, p("http://", "http://www.example.com"),
				false, parts, parts, context,
				c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, 6));

		// 2. otherwise
		testCompare(cond, p("http:/", "http://www.example.com"), true,
				parts, parts, context, c(ParserStates.RELATIVE_STATE, 4));
		testCompare(cond, p("http:", "http://www.example.com"), true,
				parts, parts, context, c(ParserStates.RELATIVE_STATE, 4));
	}
}
