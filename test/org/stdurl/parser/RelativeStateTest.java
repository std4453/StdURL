package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class RelativeStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.RELATIVE_STATE, 0);
		MachineURLParts parts = u();
		String base = "http://username:password@example.com:8080/a/b/c?query#frag";

		// EOF code point
		testCompare(cond, p("", base), false, parts,
				u("http", "username", "password", "example.com", 8080, false,
						"a/b/c", "query", null), context, context);

		// /
		testCompare(cond, p("/", base), false, parts, u("http"),
				context, c(ParserStates.RELATIVE_SLASH_STATE, 0));

		// ?
		testCompare(cond, p("?", base), false, parts,
				u("http", "username", "password", "example.com", 8080, false,
						"a/b/c", "", null), context, c(ParserStates.QUERY_STATE, 0));

		// #
		testCompare(cond, p("#", base), false, parts,
				u("http", "username", "password", "example.com", 8080, false,
						"a/b/c", "query", ""), context,
				c(ParserStates.FRAGMENT_STATE, 0));

		// otherwise
		testCompare(cond, p("\\", base), true, parts, u("http"),
				context, c(ParserStates.RELATIVE_SLASH_STATE, 0));
		testCompare(cond, p("abc", base), false, parts,
				u("http", "username", "password", "example.com", 8080, false, "a/b",
						null, null), context, c(ParserStates.PATH_STATE, -1));
		testCompare(cond, p("abc",
				"http://username:password@example.com:8080/?query#frag"), false,
				parts, u("http", "username", "password", "example.com", 8080,
						false, null, null, null),
				context, c(ParserStates.PATH_STATE, -1));
	}
}
