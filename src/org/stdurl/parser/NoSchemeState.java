package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#no-scheme-state">#no-scheme-state</a>
 */
public class NoSchemeState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (machine.base == null || (machine.base.getCannotBeABaseURLInternal() && c != '#')) { // 1
			machine.reportValidationError("Must have a base URL or begin with '#'.");
			machine.returnDirectly(URL.failure);
		} else if (machine.base.getCannotBeABaseURLInternal() && c == '#') { // 2
			machine.setScheme(machine.base.getSchemeInternal());
			machine.path.addAll(machine.base.getPathInternal());
			machine.setQuery(machine.base.getQueryInternal());
			machine.setFragment("");
			machine.setCannotBeABaseURL(true);
			machine.setState(ParserStates.FRAGMENT_STATE);
		} else if (!SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
				machine.base.getSchemeInternal())) { // 3
			machine.setState(ParserStates.RELATIVE_STATE);
			machine.setPointer(machine.pointer - 1);
		} else { // 4
			machine.setState(ParserStates.FILE_STATE);
			machine.setPointer(machine.pointer - 1);
		}
	}
}
