package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-relative-or-authority-state">#special-relative-or-authoritu-state</a>
 */
public class SpecialRelativeOrAuthorityState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;
		if (c == '/' && machine.getRemainingAt(0) == '/') {
			machine.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			machine.setPointer(machine.pointer + 1);
		} else {
			machine.reportValidationError("Should start with \"//\".");
			machine.setState(ParserStates.RELATIVE_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
