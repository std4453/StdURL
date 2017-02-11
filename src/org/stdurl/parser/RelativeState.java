package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#relative-state">#relative-state</a>
 */
public class RelativeState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		machine.setScheme(machine.base.getSchemeInternal());

		switch (machine.c) {
			case 0:
				machine.setUsername(machine.base.getUsernameInternal());
				machine.setPassword(machine.base.getPasswordInternal());
				machine.setHost(machine.base.getHostInternal());
				machine.setPort(machine.base.getPortInternal());
				machine.path.addAll(machine.base.getPathInternal());
				machine.setQuery(machine.base.getQueryInternal());
				break;
			case '/':
				machine.setState(ParserStates.RELATIVE_SLASH_STATE);
				break;
			case '?':
				machine.setUsername(machine.base.getUsernameInternal());
				machine.setPassword(machine.base.getPasswordInternal());
				machine.setHost(machine.base.getHostInternal());
				machine.setPort(machine.base.getPortInternal());
				machine.path.addAll(machine.base.getPathInternal());
				machine.setQuery("");
				machine.setState(ParserStates.QUERY_STATE);
				break;
			case '#':
				machine.setUsername(machine.base.getUsernameInternal());
				machine.setPassword(machine.base.getPasswordInternal());
				machine.setHost(machine.base.getHostInternal());
				machine.setPort(machine.base.getPortInternal());
				machine.path.addAll(machine.base.getPathInternal());
				machine.setQuery(machine.base.getQueryInternal());
				machine.setFragment("");
				machine.setState(ParserStates.FRAGMENT_STATE);
				break;
			default:
				if (SchemeHelper.isSpecialScheme(machine.scheme) && machine.c == '\\') {
					machine.reportValidationError("Backslash should be slash.");
					machine.setState(ParserStates.RELATIVE_SLASH_STATE);
				} else {
					machine.setUsername(machine.base.getUsernameInternal());
					machine.setPassword(machine.base.getPasswordInternal());
					machine.setHost(machine.base.getHostInternal());
					machine.setPort(machine.base.getPortInternal());
					machine.path.addAll(machine.base.getPathInternal());
					if (machine.path.size() >= 1)
						machine.path.remove(machine.path.size() - 1);
					machine.setState(ParserStates.PATH_STATE);
					machine.setPointer(machine.pointer - 1);
				}
		}
	}
}
