package org.stdurl.parser;

import org.junit.Test;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.TestShortenHelper.a;
import static org.stdurl.parser.TestShortenHelper.c;
import static org.stdurl.parser.TestShortenHelper.p;
import static org.stdurl.parser.TestShortenHelper.u;

/**
 *
 */
public class QueryStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = c(ParserStates.QUERY_STATE, 0);
		MachineURLParts parts = u();

		for (int i = 1; i < 128; ++i) {
			if ("%#".indexOf(i) != -1) continue;
			boolean veExpected = !CodePointHelper.isURLCodePoint(i);
			String str = StringHelper.toString(i);
			testCompare(cond, p(str), veExpected, parts, parts, context, c(context, str));
		}
		testCompare(cond, p("#", ParserStates.QUERY_STATE), true, parts, parts,
				context, c(context, "#"));
		MachineContext endContext = c(context, "%");
		testCompare(cond, p("%"), true, parts, parts, context, endContext);
		testCompare(cond, p("%a"), true, parts, parts, context, endContext);
		testCompare(cond, p("%a%"), true, parts, parts, context, endContext);
		testCompare(cond, p("%aa"), false, parts, parts, context, endContext);

		testCompare(cond, p(""), false,
				parts, a(parts, "abc%20", null),
				c(context, "abc "), context);
		testCompare(cond, p("", ParserStates.QUERY_STATE), false,
				parts, a(parts, "abc%20", null),
				c(context, "abc "), context);
		testCompare(cond, p("#"), false,
				parts, a(parts, "abc%20", ""),
				c(context, "abc "), c(ParserStates.FRAGMENT_STATE, 0));

		long lMask = 0x5000000DFFFFFFFFL, hMask = 0x8000000000000000L;
		for (int i = 1; i < 128; ++i) {
			boolean encoded = (i < 0x40 ? (lMask & (1L << i)) :
					(hMask & (1L << (i - 0x40)))) != 0;
			String orig = StringHelper.toString(i);
			String result = orig;
			if (encoded) result = String.format("%%%02x", i).toUpperCase();
			testCompare(cond, p(""), false, parts,
					a(parts, result, null), c(context, orig), context);
		}

		if (EncodingHelper.GB2312 != null) {
			MachineParameters params = c(p(""), EncodingHelper.GB2312);
			MachineContext startContext = c(context, "\u65b0");
			String gb2312 = "%D0%C2", utf8 = "%E6%96%B0";
			testCompare(cond, params, false,
					u("http"), a(u("http"), gb2312, null), startContext, context);
			testCompare(cond, params, false,
					u("https"), a(u("https"), gb2312, null), startContext, context);
			testCompare(cond, params, false,
					u("ws"), a(u("ws"), utf8, null), startContext, context);
			testCompare(cond, params, false,
					u("wss"), a(u("wss"), utf8, null), startContext, context);
			testCompare(cond, params, false,
					u("test"), a(u("test"), utf8, null), startContext, context);
		}
	}
}
