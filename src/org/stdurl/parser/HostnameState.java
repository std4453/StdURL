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

		if (c == ':' && !machine.bracketsFlag) { //1
			if (machine.buffer.length() == 0) { // 1.1
				machine.reportSyntaxViolation("Empty host forbidden.");
				machine.returnDirectly(URL.failure);
				return;
			}

			// 1.2
			String input = machine.buffer.toString();
			boolean isSpecial = SchemeHelper.isSpecialScheme(machine.scheme);
			Host host = HostParser.parseURLHost(input, isSpecial, machine.listener);

			if (host == null) { // 1.3
				machine.returnDirectly(URL.failure);
				return;
			}

			// 1.4
			machine.setHost(host);
			machine.buffer.setLength(0);
			machine.setState(ParserStates.PORT_STATE);

			// 1.5
			if (machine.stateOverride == ParserStates.HOSTNAME_STATE)
				machine.terminate();
		} else if (c == 0 || "/?#".indexOf(c) != -1 ||
				(SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\')) { // 2
			machine.setPointer(machine.pointer - 1);
			boolean isSpecial = SchemeHelper.isSpecialScheme(machine.scheme);

			if (isSpecial && machine.buffer.length() == 0) { // 2.1
				machine.reportSyntaxViolation("Empty host forbidden.");
				machine.returnDirectly(URL.failure);
				return;
			}

			// 2.2
			String input = machine.buffer.toString();
			Host host = HostParser.parseURLHost(input, isSpecial, machine.listener);

			if (host == null) { // 2.3
				machine.returnDirectly(URL.failure);
				return;
			}

			// 2.4
			machine.setHost(host);
			machine.buffer.setLength(0);
			machine.setState(ParserStates.PATH_START_STATE);

			if (ParserStates.hasState(machine.stateOverride)) // 2.5
				machine.terminate();
		} else { // 3
			if (c == '[') machine.setBracketsFlag(true);
			else if (c == ']') machine.setBracketsFlag(false);
			machine.buffer.appendCodePoint(c);
		}
	}
}
