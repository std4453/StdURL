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
public class FileSlashStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineURLParts parts = u();
		MachineContext context = c(ParserStates.FILE_SLASH_STATE, 0);

		testCompare(cond, p("/"), false, parts, parts, context,
				c(ParserStates.FILE_HOST_STATE, 0));
		testCompare(cond, p("\\"), true, parts, parts, context,
				c(ParserStates.FILE_HOST_STATE, 0));

		MachineContext endContext = c(ParserStates.PATH_STATE, -1);
		testCompare(cond, p(""), false, parts, parts, context, endContext);
		testCompare(cond, p("", "http://example.com"),
				false, parts, parts, context, endContext);
		testCompare(cond, p("", "file://example.com/1.txt"),
				false, parts, parts, context, endContext);
		testCompare(cond, p("", "file://D:/1.txt"),
				false, parts, a(parts, "D:"), context, endContext);
	}
}
