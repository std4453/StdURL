package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-start-state">#path-start-state</a>
 */
public class PathStartState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		// 1
		boolean flag = SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\';
		if (flag) machine.reportValidationError("Backslash should be slash.");

		// 2
		machine.setState(ParserStates.PATH_STATE);
		if (c != '/' && !flag) machine.setPointer(machine.pointer - 1);
	}
}
