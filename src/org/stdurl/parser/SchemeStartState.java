package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#scheme-start-state">#scheme-start-state</a>
 */
public class SchemeStartState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (ASCIIHelper.isASCIIAlpha(c)) { // 1
			context.buffer.appendCodePoint(ASCIIHelper.toLowerCase(c));
			context.setState(ParserStates.SCHEME_STATE);
		} else if (!ParserStates.hasState(context.stateOverride)) { // 2
			context.setState(ParserStates.NO_SCHEME_STATE);
			context.setPointer(context.pointer - 1);
		} else {
			context.reportSyntaxViolation("State override does nothing.");
			context.setReturnValue(URL.failure);
		}
	}
}
