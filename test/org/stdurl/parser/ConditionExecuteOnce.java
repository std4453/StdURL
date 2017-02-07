package org.stdurl.parser;

/**
 *
 */
public class ConditionExecuteOnce implements ITerminateCondition {
	public static final ConditionExecuteOnce instance = new ConditionExecuteOnce();

	@Override
	public boolean shouldTerminate(InjectedParserStateMachine machine) {
		return true; // thus the state machine executes only once
	}
}
