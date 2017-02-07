package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;
import static org.stdurl.parser.ParserStateTestHelper.testFailure;

/**
 *
 */
public class SchemeStartStateTest {
	@Test
	public void test() {
		ITerminateCondition cond = ConditionExecuteOnce.instance;
		MachineContext context = new MachineContext(ParserStates.SCHEME_START_STATE, 0);
		MachineURLParts parts = new MachineURLParts();

		testCompare(cond, new MachineParameters("abc"),
				false, parts, parts, context,
				new MachineContext(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, new MachineParameters("ABC"),
				false, parts, parts, context,
				new MachineContext(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, new MachineParameters("123"),
				false, parts, parts, context,
				new MachineContext(ParserStates.NO_SCHEME_STATE, -1));
		testCompare(cond, new MachineParameters("\u65b0"),
				false, parts, parts, context,
				new MachineContext(ParserStates.NO_SCHEME_STATE, -1));
		testCompare(cond, new MachineParameters("abc", ParserStates.FRAGMENT_STATE),
				false, parts, parts, context,
				new MachineContext(ParserStates.SCHEME_STATE, 0, "a"));
		testCompare(cond, new MachineParameters("ABC", ParserStates.FRAGMENT_STATE),
				false, parts, parts, context,
				new MachineContext(ParserStates.SCHEME_STATE, 0, "a"));
		testFailure(cond, new MachineParameters("123", ParserStates.FRAGMENT_STATE),
				parts, context, true);
		testFailure(cond, new MachineParameters("\u65b0", ParserStates.FRAGMENT_STATE),
				parts, context, true);
	}
}
