package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.ParserStateTestHelper.testTerminated;
import static org.stdurl.parser.ParserStateTestHelper.testTerminatedAndCompare;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class SchemeStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		int st = ParserStates.SCHEME_STATE;
		MachineContext context = c(st, 1, "a");
		MachineURLParts parts = u();
		int so = ParserStates.SCHEME_START_STATE;

		// 1. c is alphanumeric / +-.
		testCompare(cond, p("abc"), false, parts, parts, context, c(st, 1, "ab"));
		testCompare(cond, p("aBc"), false, parts, parts, context, c(st, 1, "ab"));
		testCompare(cond, p("a0c"), false, parts, parts, context, c(st, 1, "a0"));
		testCompare(cond, p("a9c"), false, parts, parts, context, c(st, 1, "a9"));

		// 2. c is :
		// 2.1. state override is given
		// 2.1.1. url scheme is special scheme and special is not
		testTerminated(cond, p("http://", so), u("http"), c(st, 4, "test"), false);

		// 2.1.2. url scheme is not special scheme and special is
		testTerminated(cond, p("test://", so), u("test"), c(st, 4, "http"), false);

		// 2.4. state override is given
		testTerminatedAndCompare(cond, p("http://", so), false,
				u("ws"), u("http"), c(st, 4, "http"), c(st, 4));
		testTerminatedAndCompare(cond, p("test://", so), false,
				u("wtf"), u("test"), c(st, 4, "test"), c(st, 4));

		// 2.5. url's scheme is file
		testCompare(cond, p("file://"), false, parts, u("file"),
				c(st, 4, "file"), c(ParserStates.FILE_STATE, 4));

		// 2.5.1. remaining does not start with //
		testCompare(cond, p("file:"), true, parts, u("file"),
				c(st, 4, "file"), c(ParserStates.FILE_STATE, 4));
		testCompare(cond, p("file:aa"), true, parts, u("file"),
				c(st, 4, "file"), c(ParserStates.FILE_STATE, 4));
		testCompare(cond, p("file:/"), true, parts, u("file"),
				c(st, 4, "file"), c(ParserStates.FILE_STATE, 4));
		testCompare(cond, p("file:/a"), true, parts, u("file"),
				c(st, 4, "file"), c(ParserStates.FILE_STATE, 4));

		// 2.6. url is special & base non-null & base.scheme = url.scheme
		testCompare(cond, p("http://", "http://www.example.com"), false,
				parts, u("http"), c(st, 4, "http"),
				c(ParserStates.SPECIAL_RELATIVE_OR_AUTHORITY_STATE, 4));

		// 2.7. otherwise is url is special
		// otherwise - base.scheme != url.scheme
		testCompare(cond, p("http://", "ws://www.example.com"), false,
				parts, u("http"), c(st, 4, "http"),
				c(ParserStates.SPECIAL_AUTHORITY_SLASHES_STATE, 4));
		// otherwise - base == null
		testCompare(cond, p("http://"), false, parts, u("http"),
				c(st, 4, "http"), c(ParserStates.SPECIAL_AUTHORITY_SLASHES_STATE, 4));

		// 2.8. otherwise if remaining starts with /
		testCompare(cond, p("test:/"), false, parts, u("test"),
				c(st, 4, "test"), c(ParserStates.PATH_OR_AUTHORITY_STATE, 5));

		// 2.9. otherwise
		testCompare(cond, p("blob:"), false, parts,
				a(u("blob", true), ""),
				c(st, 4, "blob"), c(ParserStates.CANNOT_BE_A_BASE_URL_PATH_STATE, 4));

		// 3. otherwise (c is not :) and state override is not given
		testCompare(cond, p("a/b"), false, parts, parts, context,
				c(ParserStates.NO_SCHEME_STATE, -1));

		// 4. otherwise
		testFailure(cond, p("a/b", so), parts, context, true);
	}
}
