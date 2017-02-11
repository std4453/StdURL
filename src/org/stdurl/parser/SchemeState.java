package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.SchemeHelper;

import java.util.Objects;

/**
 * @see <a href="https://url.spec.whatwg.org/#scheme-state">#scheme-state</a>
 */
public class SchemeState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;
		boolean stateOverrideGiven = ParserStates.hasState(machine.stateOverride);

		if (ASCIIHelper.isASCIIAlphanumeric(c) || "+-.".indexOf(c) != -1) { // 1
			machine.buffer.appendCodePoint(ASCIIHelper.toLowerCase(c));
		} else if (c == ':') { // 2
			if (stateOverrideGiven) { // 2.1
				String buf = machine.buffer.toString();
				if (SchemeHelper.isSpecialScheme(machine.scheme) ^
						SchemeHelper.isSpecialScheme(buf)) {
					machine.terminate();
					return;
				}
			}

			machine.setScheme(machine.buffer.toString()); // 2.2
			machine.buffer.setLength(0); // 2.3

			if (stateOverrideGiven) { // 2.4
				machine.terminate();
				return;
			}

			if ("file".equalsIgnoreCase(machine.scheme)) { // 2.5
				if (machine.getRemainingAt(0) != '/' ||
						machine.getRemainingAt(1) != '/')
					machine.reportValidationError(
							"Path of file scheme doesn't start with \"//\".");
				machine.setState(ParserStates.FILE_STATE);
			} else if (SchemeHelper.isSpecialScheme(machine.scheme)) {
				// 2.6 + 2.7
				machine.setState(machine.base != null &&
						Objects.equals(machine.base.getSchemeInternal(), machine.scheme) ?
						ParserStates.SPECIAL_RELATIVE_OR_AUTHORITY_STATE :
						ParserStates.SPECIAL_AUTHORITY_SLASHES_STATE);
			} else if (machine.getRemainingAt(0) == '/') { // 2.8
				machine.setState(ParserStates.PATH_OR_AUTHORITY_STATE);
				machine.setPointer(machine.pointer + 1);
			} else { // 2.9
				machine.setCannotBeABaseURL(true);
				machine.path.add("");
				machine.setState(ParserStates.CANNOT_BE_A_BASE_URL_PATH_STATE);
			}
		} else if (!stateOverrideGiven) { // 3
			machine.buffer.setLength(0);
			machine.setState(ParserStates.NO_SCHEME_STATE);
			machine.setPointer(-1);
		} else { //4
			machine.reportValidationError("State override does nothing.");
			machine.returnDirectly(URL.failure);
		}
	}
}
