package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-or-authority-state">#path-or-authority-state</a>
 */
public class PathOrAuthorityState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		if (machine.c == '/')
			machine.setState(ParserStates.AUTHORITY_STATE);
		else {
			machine.setState(ParserStates.PATH_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
