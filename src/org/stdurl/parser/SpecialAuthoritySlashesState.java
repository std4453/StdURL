package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-authority-slashes-state">#special-authority-slashes-state</a>
 */
public class SpecialAuthoritySlashesState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		if (machine.c == '/' && machine.getRemainingAt(0) == '/') {
			machine.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			machine.setPointer(machine.pointer + 1);
		} else {
			machine.reportValidationError("Slash should be followed by another slash.");
			machine.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
