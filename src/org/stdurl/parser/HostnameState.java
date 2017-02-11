package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.SchemeHelper;
import org.stdurl.host.Host;
import org.stdurl.host.HostParser;

/**
 * @see <a href="https://url.spec.whatwg.org/#hostname-state">#hostname-state</a>
 */
public class HostnameState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (ParserStates.hasState(machine.stateOverride) &&
				SchemeHelper.SCHEME_FILE.equalsIgnoreCase(machine.scheme)) { // 1
			machine.setPointer(machine.pointer - 1);
			machine.setState(ParserStates.FILE_HOST_STATE);
		} else if (c == ':' && !machine.bracketsFlag) { // 2
			if (machine.buffer.length() == 0) { // 2.1
				machine.reportValidationError("Empty host forbidden.");
				machine.returnDirectly(URL.failure);
				return;
			}

			// 2.2
			String input = machine.buffer.toString();
			boolean isSpecial = SchemeHelper.isSpecialScheme(machine.scheme);
			Host host = HostParser.parseURLHost(input, isSpecial, machine.listener);

			if (host == null) { // 2.3
				machine.returnDirectly(URL.failure);
				return;
			}

			// 2.4
			machine.setHost(host);
			machine.buffer.setLength(0);
			machine.setState(ParserStates.PORT_STATE);

			// 2.5
			if (machine.stateOverride == ParserStates.HOSTNAME_STATE)
				machine.terminate();
		} else if (c == 0 || "/?#".indexOf(c) != -1 ||
				(SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\')) { // 3
			machine.setPointer(machine.pointer - 1);
			boolean isSpecial = SchemeHelper.isSpecialScheme(machine.scheme);

			if (isSpecial && machine.buffer.length() == 0) { // 3.1
				machine.reportValidationError("Empty host forbidden.");
				machine.returnDirectly(URL.failure);
				return;
			}

			if (ParserStates.hasState(
					machine.stateOverride) && machine.buffer.length() == 0 &&
					(!machine.password.isEmpty() || !machine.username.isEmpty() || machine.port != -1)) { // 3.2
				machine.reportValidationError(
						"Host and credential shouldn't be overwritten with state override given.");
				machine.terminate();
				return;
			}

			// 3.3
			String input = machine.buffer.toString();
			Host host = HostParser.parseURLHost(input, isSpecial, machine.listener);

			if (host == null) { // 3.4
				machine.returnDirectly(URL.failure);
				return;
			}

			// 3.5
			machine.setHost(host);
			machine.buffer.setLength(0);
			machine.setState(ParserStates.PATH_START_STATE);

			if (ParserStates.hasState(machine.stateOverride)) // 3.6
				machine.terminate();
		} else { // 4
			if (c == '[') machine.setBracketsFlag(true);
			else if (c == ']') machine.setBracketsFlag(false);
			machine.buffer.appendCodePoint(c);
		}
	}
}
