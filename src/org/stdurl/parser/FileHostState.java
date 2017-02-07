package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.FileSchemeHelper;
import org.stdurl.host.Domain;
import org.stdurl.host.Host;
import org.stdurl.host.HostParser;

/**
 * @see <a href="https://url.spec.whatwg.org/#file-host-state">#file-host-state</a>
 */
public class FileHostState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (c == 0 || "/\\?#".indexOf(c) != -1) { // 1
			machine.setPointer(machine.pointer - 1);

			if (FileSchemeHelper.isWindowsDriveLetter(machine.buffer.toString())) { // 1.1
				machine.reportSyntaxViolation("File host shouldn't be windows drive.");
				machine.setState(ParserStates.PATH_STATE);
			} else if (machine.buffer.length() == 0) { // 1.2
				machine.setState(ParserStates.PATH_START_STATE);
			} else { // 1.3
				// 1.3.1
				String input = machine.buffer.toString();
				Host host = HostParser.parseHost(input, machine.listener);

				// 1.3.2
				if (host == null) {
					machine.returnDirectly(URL.failure);
					return;
				}

				if (!Domain.localhost.equals(host)) machine.setHost(host); // 1.3.3

				// 1.3.4
				machine.buffer.setLength(0);
				machine.setState(ParserStates.PATH_START_STATE);
			}
		} else { // 2
			machine.buffer.appendCodePoint(c);
		}
	}
}
