package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class FileStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineURLParts parts = u();
		MachineContext context = c(ParserStates.FILE_STATE, 0);
		MachineURLParts partsFile = u("file");

		testCompare(cond, p("/"), false, parts, partsFile, context,
				c(ParserStates.FILE_SLASH_STATE, 0));
		testCompare(cond, p("\\"), true, parts, partsFile, context,
				c(ParserStates.FILE_SLASH_STATE, 0));
		testCompare(cond, p("abc"), false, parts, partsFile, context,
				c(ParserStates.PATH_STATE, -1));
		testCompare(cond, p("abc", "http://example.com"), false, parts, partsFile,
				context, c(ParserStates.PATH_STATE, -1));

		String base = "file://example.com/1.txt?query#fragment";
		testCompare(cond, p("", base), false, parts,
				u("file", "", "", "example.com", -1,
						false, "1.txt", "query", null), context, context);
		testCompare(cond, p("?", base), false, parts,
				u("file", "", "", "example.com", -1,
						false, "1.txt", "", null),
				context, c(ParserStates.QUERY_STATE, 0));
		testCompare(cond, p("#", base), false, parts,
				u("file", "", "", "example.com", -1,
						false, "1.txt", "query", ""),
				context, c(ParserStates.FRAGMENT_STATE, 0));

		String[] testCases = new String[]{"C:/", "d|\\", "e:?", "F|#"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase, base), true, parts, partsFile, context,
					c(ParserStates.PATH_STATE, -1));

		testCases = new String[]{"ab/", "C:", "d|", "e:a", "F|b"};
		for (String testCase : testCases)
			testCompare(cond, p(testCase, base), false, parts,
					u("file", "", "", "example.com", -1,
							false, null, null, null),
					context, c(ParserStates.PATH_STATE, -1));
	}
}
