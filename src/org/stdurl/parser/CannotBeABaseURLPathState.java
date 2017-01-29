package org.stdurl.parser;

import org.stdurl.encoding.PercentEncoder;
import org.stdurl.encoding.SimpleEncodeSet;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#cannot-be-a-base-url-path-state">#cannot-be-a-base-url-path-state</a>
 */
public class CannotBeABaseURLPathState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (c == '?') { // 1
			context.setQuery("");
			context.setState(ParserStates.QUERY_STATE);
		} else if (c == '#') { // 2
			context.setFragment("");
			context.setState(ParserStates.FRAGMENT_STATE);
		} else { // 3
			if (c != 0 && !CodePointHelper.isURLCodePoint(c) && c != '%')
				context.reportSyntaxViolation(new StringBuilder("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(0))) ||
					!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(1)))
				context.reportSyntaxViolation("'%' is not followed by two hex digits.");

			if (c != 0) {
				String encoded = PercentEncoder.utf8Encode(c, SimpleEncodeSet.instance);
				context.path.set(0, context.path.get(0) + encoded);
			}
		}
	}
}
