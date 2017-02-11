package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class RelativeSlashStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.RELATIVE_SLASH_STATE, 0);
		MachineURLParts parts = u("http");
		MachineURLParts parts2 = u("test");

		// 1. url is special and c is / or \
		testCompare(cond, p("\\"), true, parts, parts, context,
				c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, 0));
		testCompare(cond, p("/"), false, parts, parts, context,
				c(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE, 0));

		// 2. otherwise, is c is /
		testCompare(cond, p("/"), false, parts2, parts2, context,
				c(ParserStates.AUTHORITY_STATE, 0));

		// 3. otherwise
		String base = "http://username:password@example.com:8080/a/b/c?query#frag";
		testCompare(cond, p("abc", base), false, parts2,
				u("test", "username", "password", "example.com", 8080, false,
						null, null, null), context,
				c(ParserStates.PATH_STATE, -1));
		testCompare(cond, p("\\", base), false, parts2,
				u("test", "username", "password", "example.com", 8080, false,
						null, null, null), context,
				c(ParserStates.PATH_STATE, -1));
	}
}
