package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-authority-ignore-slashes-state">#special-authority-ignore-slashes-state</a>
 */
public class SpecialAuthorityIgnoreSlashesState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		if ("/\\".indexOf(machine.c) != -1)
			machine.reportSyntaxViolation("Slash or backslash unexpected.");
		else {
			machine.setState(ParserStates.AUTHORITY_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
