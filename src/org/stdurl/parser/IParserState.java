package org.stdurl.parser;

/**
 * Interface for a state used by the state machine.
 */
interface IParserState {
	void execute(ParserStateMachine machine) throws Throwable;
}
