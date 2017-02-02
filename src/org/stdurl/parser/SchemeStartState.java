package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#scheme-start-state">#scheme-start-state</a>
 */
public class SchemeStartState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (ASCIIHelper.isASCIIAlpha(c)) { // 1
			machine.buffer.appendCodePoint(ASCIIHelper.toLowerCase(c));
			machine.setState(ParserStates.SCHEME_STATE);
		} else if (!ParserStates.hasState(machine.stateOverride)) { // 2
			machine.setState(ParserStates.NO_SCHEME_STATE);
			machine.setPointer(machine.pointer - 1);
		} else {
			machine.reportSyntaxViolation("State override does nothing.");
			machine.setReturnValue(URL.failure);
		}
	}
}
