package org.stdurl.parser;

import org.junit.Test;

import static org.stdurl.parser.ParserStateTestHelper.testCompare;

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
	}
}
