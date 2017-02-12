package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#port-state">#port-state</a>
 */
public class PortState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (ASCIIHelper.isASCIIDigit(c)) // 1
			machine.buffer.appendCodePoint(c);
		else if (c == 0 || "/?#".indexOf(c) != -1 ||
				(SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\') ||
				ParserStates.hasState(machine.stateOverride)) { // 2
			if (machine.buffer.length() != 0) { // 2.1
				int port = Integer.parseInt(machine.buffer.toString()); // 2.1.1
				if (port > 0xFFFF) { // 2.1.2
					machine.reportValidationError("Port should be smaller than 65536.");
					machine.returnDirectly(URL.failure);
					return;
				}
				machine.setPort(port == SchemeHelper.getDefaultPort(machine.scheme) ?
						-1 : port); // 2.1.3
				machine.buffer.setLength(0); // 2.1.4
			}
			if (ParserStates.hasState(machine.stateOverride)) { // 2.2
				machine.terminate();
				return;
			}
			// 2.3
			machine.setState(ParserStates.PATH_START_STATE);
			machine.setPointer(machine.pointer - 1);
		} else { // 3
			machine.reportValidationError(
					new StringBuilder("Character '").
							appendCodePoint(c).append("' unexpected.").toString());
			machine.returnDirectly(URL.failure);
		}
	}
}
