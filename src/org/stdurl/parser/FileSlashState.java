package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.FileSchemeHelper;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#file-slash-state">#file-slash-state</a>
 */
public class FileSlashState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if ("\\/".indexOf(c) != -1) { // 1
			if (c == '\\')
				machine.reportValidationError("Backslash should be slash.");
			machine.setState(ParserStates.FILE_HOST_STATE);
		} else { // 2
			URL base = machine.base;
			if (base != null &&
					SchemeHelper.SCHEME_FILE.equalsIgnoreCase(base.getSchemeInternal()) &&
					base.getPathInternal().size() >= 1 &&
					FileSchemeHelper.isWindowsDriveLetter(base.getPathInternal().get(0)))
				machine.path.add(base.getPathInternal().get(0));
			machine.setState(ParserStates.PATH_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
