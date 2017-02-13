package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-start-state">#path-start-state</a>
 */
public class PathStartState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;
		boolean soGiven = ParserStates.hasState(machine.stateOverride);

		if (SchemeHelper.isSpecialScheme(machine.scheme)) { // 1
			if (c == '\\') machine.reportValidationError("Backslash should be slash.");
			machine.setState(ParserStates.PATH_STATE);
			if ("\\/".indexOf(c) == -1) machine.setPointer(machine.pointer - 1);
		} else if (!soGiven && c == '?') { // 2
			machine.setQuery("");
			machine.setState(ParserStates.QUERY_STATE);
		} else if (!soGiven && c == '#') { // 3
			machine.setFragment("");
			machine.setState(ParserStates.FRAGMENT_STATE);
		} else if (c != 0) { // 4
			machine.setState(ParserStates.PATH_STATE);
			if (c != '/') machine.setPointer(machine.pointer - 1);
		}
	}
}
