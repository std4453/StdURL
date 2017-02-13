package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class FragmentStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.FRAGMENT_STATE, 0);
		MachineURLParts parts = a(u(), null, "");

		// it is derived from the standard that an input code point of U+0000 does not
		// report a violation error, since we regard U+0000 the same as EOF code point
		// (like the c-style string) and we choose to do nothing here.
		testCompare(cond, p(""), false, parts, parts, context, context);
		testCompare(cond, p("\u0000"), false, parts, parts, context, context);

		testCompare(cond, p("\\"), true,
				parts, a(parts, null, "\\"), context, context);
		MachineURLParts endParts = a(parts, null, "%");
		String[] testCases = new String[]{"%", "%%", "%a", "%a%"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase), true, parts, endParts, context, context);
		testCompare(cond, p("%aa"), false, parts, endParts, context, context);
		testCompare(cond, p("\u65b0"), false,
				parts, a(parts, null, "%E6%96%B0"), context, context);
	}
}
