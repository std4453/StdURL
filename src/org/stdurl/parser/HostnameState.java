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
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (c == ':' && !context.bracketsFlag) { //1
			if (context.buffer.length() == 0) { // 1.1
				context.reportSyntaxViolation("Empty host forbidden.");
				context.setReturnValue(URL.failure);
				return;
			}

			// 1.2
			String input = context.buffer.toString();
			boolean isSpecial = SchemeHelper.isSpecialScheme(context.scheme);
			Host host = HostParser.parseURLHost(input, isSpecial, context.listener);

			if (host == null) { // 1.3
				context.setReturnValue(URL.failure);
				return;
			}

			// 1.4
			context.setHost(host);
			context.buffer.setLength(0);
			context.setState(ParserStates.PORT_STATE);

			// 1.5
			if (context.stateOverride == ParserStates.HOSTNAME_STATE)
				context.setTerminateRequested(true);
		} else if (c == 0 || "/?#".indexOf(c) != -1 ||
				(SchemeHelper.isSpecialScheme(context.scheme) && c == '\\')) { // 2
			context.setPointer(context.pointer - 1);
			boolean isSpecial = SchemeHelper.isSpecialScheme(context.scheme);

			if (isSpecial && context.buffer.length() == 0) { // 2.1
				context.reportSyntaxViolation("Empty host forbidden.");
				context.setReturnValue(URL.failure);
				return;
			}

			// 2.2
			String input = context.buffer.toString();
			Host host = HostParser.parseURLHost(input, isSpecial, context.listener);

			if (host == null) { // 2.3
				context.setReturnValue(URL.failure);
				return;
			}

			// 2.4
			context.setHost(host);
			context.buffer.setLength(0);
			context.setState(ParserStates.PATH_START_STATE);

			if (ParserStates.hasState(context.stateOverride)) // 2.5
				context.setTerminateRequested(true);
		} else { // 3
			if (c == '[') context.setBracketsFlag(true);
			else if (c == ']') context.setBracketsFlag(false);
			context.buffer.appendCodePoint(c);
		}
	}
}
