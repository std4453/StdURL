package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class PathOrAuthorityStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineURLParts parts = u("test");
		MachineContext context = c(ParserStates.PATH_OR_AUTHORITY_STATE, 5);

		testCompare(cond, p("test:/abc"), false, parts, parts, context,
				c(ParserStates.AUTHORITY_STATE, 5));
		testCompare(cond, p("test:abc"), false, parts, parts, context,
				c(ParserStates.PATH_STATE, 4));
	}
}
