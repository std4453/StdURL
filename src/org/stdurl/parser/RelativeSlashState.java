package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#relative-slash-state">#relative-slash-state</a>
 */
public class RelativeSlashState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		// 1
		if (c == '/' || (SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\')) {
			if (c == '\\')
				machine.reportSyntaxViolation("Backslash should be slash.");
			machine.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
		} else { // 2
			machine.setUsername(machine.base.getUsernameInternal());
			machine.setPassword(machine.base.getPasswordInternal());
			machine.setHost(machine.base.getHostInternal());
			machine.setPort(machine.base.getPortInternal());
			machine.setState(ParserStates.PATH_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
