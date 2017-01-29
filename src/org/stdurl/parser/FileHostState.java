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
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (c == 0 || "/\\?#".indexOf(c) != -1) { // 1
			context.setPointer(context.pointer - 1);

			if (FileSchemeHelper.isWindowsDriveLetter(context.buffer.toString())) { // 1.1
				context.reportSyntaxViolation("File host shouldn't be windows drive.");
				context.setState(ParserStates.PATH_STATE);
			} else if (context.buffer.length() == 0) { // 1.2
				context.setState(ParserStates.PATH_START_STATE);
			} else { // 1.3
				// 1.3.1
				String input = context.buffer.toString();
				Host host = HostParser.parseHost(input, context.listener);

				// 1.3.2
				if (host == null) {
					context.setReturnValue(URL.failure);
					return;
				}

				if (!Domain.localhost.equals(host)) context.setHost(host); // 1.3.3

				// 1.3.4
				context.buffer.setLength(0);
				context.setState(ParserStates.PATH_START_STATE);
			}
		} else { // 2
			context.buffer.appendCodePoint(c);
		}
	}
}
