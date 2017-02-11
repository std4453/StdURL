package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.host.Host;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class NoSchemeStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.NO_SCHEME_STATE, 0);
		MachineURLParts parts = u();

		// 1. base == null | (base cannot be a base URL & c != #)
		testFailure(cond, p("a/b"), parts, context, true);
		testFailure(cond, p("a/b", "test:abc"), parts, context, true);

		// 2. otherwise if base cannot be a base URL & c == #
		testCompare(cond, p("#query", "test:example-path?query#frag"), false, parts,
				a(u("test", "", "", (Host) null, -1, true, null, "query", ""),
						"example-path"), context, c(ParserStates.FRAGMENT_STATE, 0));

		// 3. otherwise if base.scheme != "file"
		testCompare(cond, p("a/b", "http://www.example.com"), false,
				parts, parts, context, c(ParserStates.RELATIVE_STATE, -1));

		// 4. otherwise
		testCompare(cond, p("a/b", "file:///C:/1.txt"), false,
				parts, parts, context, c(ParserStates.FILE_STATE, -1));
	}
}
