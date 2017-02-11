package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class SpecialAuthoritySlashesStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.SPECIAL_AUTHORITY_SLASHES_STATE, 0);
		MachineURLParts parts = u();

		testCompare(cond, p("//"), false, parts, parts, context,
				c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, 1));

		String[] testCases = new String[]{"/", "/a", "a", "", "/\\"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase), true, parts, parts, context,
					c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, -1));
	}
}
