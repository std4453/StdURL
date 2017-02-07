package org.stdurl.parser;

/**
 * An interface that is used by {@link InjectedParserStateMachine} to inject into the
 * {@link ParserStateMachine#loop()} method and determine whether the state machine
 * should terminate.
 */
public interface ITerminateCondition {
	boolean shouldTerminate(InjectedParserStateMachine machine);
}
