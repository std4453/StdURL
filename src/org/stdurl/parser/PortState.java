package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#port-state">#port-state</a>
 */
public class PortState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (ASCIIHelper.isASCIIDigit(c)) // 1
			context.buffer.appendCodePoint(c);
		else if (c == 0 || "/?#".indexOf(c) != -1 ||
				(SchemeHelper.isSpecialScheme(context.scheme) && c == '\\') ||
				ParserStates.hasState(context.stateOverride)) { // 2
			if (context.buffer.length() != 0) { // 2.1
				int port = Integer.parseInt(context.buffer.toString()); // 2.1.1
				if (port > 0xFFFF) { // 2.1.2
					context.reportSyntaxViolation("Port should be smaller than 32768.");
					context.setReturnValue(URL.failure);
					return;
				}
				context.setPort(port == SchemeHelper.getDefaultPort(context.scheme) ?
						-1 : port); // 2.1.3
				context.buffer.setLength(0); // 2.1.4
			}
			if (ParserStates.hasState(context.stateOverride)) { // 2.2
				context.setTerminateRequested(true);
				return;
			}
			// 2.3
			context.setState(ParserStates.PATH_START_STATE);
			context.setPointer(context.pointer - 1);
		} else { // 3
			context.reportSyntaxViolation(
					new StringBuilder("Character '").
							appendCodePoint(c).append("' unexpected.").toString());
			context.setReturnValue(URL.failure);
		}
	}
}
