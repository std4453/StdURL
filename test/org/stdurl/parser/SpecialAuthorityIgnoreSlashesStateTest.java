package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class SpecialAuthorityIgnoreSlashesStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context =
				c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, 0);
		MachineContext context2 = c(ParserStates.AUTHORITY_STATE, -1);
		MachineURLParts parts = u();

		testCompare(cond, p("/"), true, parts, parts, context, context);
		testCompare(cond, p("\\"), true, parts, parts, context, context);

		String[] testCases = new String[]{"a", "+", "=", "0", "A"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase), false, parts, parts, context, context2);
	}
}
