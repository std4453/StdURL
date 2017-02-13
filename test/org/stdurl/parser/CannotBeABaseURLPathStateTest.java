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
public class CannotBeABaseURLPathStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.CANNOT_BE_A_BASE_URL_PATH_STATE, 0);
		MachineURLParts parts = u();

		testCompare(cond, p("?"), false, parts, a(parts, "", null), context,
				c(ParserStates.QUERY_STATE, 0));
		testCompare(cond, p("#"), false, parts, a(parts, null, ""), context,
				c(ParserStates.FRAGMENT_STATE, 0));

		testCompare(cond, p("\\"), true,
				a(parts, ""), a(parts, "\\"), context, context);
		testCompare(cond, p("%"), true,
				a(parts, ""), a(parts, "%"), context, context);
		testCompare(cond, p("%a"), true,
				a(parts, ""), a(parts, "%"), context, context);
		testCompare(cond, p("%a%"), true,
				a(parts, ""), a(parts, "%"), context, context);
		testCompare(cond, p("%aa"), false,
				a(parts, ""), a(parts, "%"), context, context);
		testCompare(cond, p("\u65b0"), false,
				a(parts, ""), a(parts, "%E6%96%B0"), context, context);
		testCompare(cond, p(""), false, parts, parts, context, context);
	}
}
