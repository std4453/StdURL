package org.stdurl.parser;

import org.stdurl.encoding.PercentEncoder;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#query-state">#query-state</a>
 */
public class QueryState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (c == 0 || (!ParserStates.hasState(context.stateOverride) && c == '#')) { // 1
			if (!SchemeHelper.isSpecialScheme(context.scheme) ||
					SchemeHelper.SCHEME_WS.equalsIgnoreCase(context.scheme) ||
					SchemeHelper.SCHEME_WSS.equalsIgnoreCase(context.scheme))
				context.setEncoding(EncodingHelper.UTF8); // 1.1

			byte[] encoded = context.buffer.toString().getBytes(context.encoding); // 1.2

			// 1.3
			StringBuilder sb = new StringBuilder(
					context.query == null ? "" : context.query);
			for (byte b : encoded) {
				int i = ((int) b) & 0xFF;
				if (i < 0x21 || i > 0x7E ||
						i == 0x22 || i == 0x23 || i == 0x3C || i == 0x3E)
					PercentEncoder.encode(b, sb);
				else sb.appendCodePoint(i);
			}
			context.setQuery(sb.toString());

			context.buffer.setLength(0); // 1.4

			if (c == '#') { // 1.5
				context.setFragment("");
				context.setState(ParserStates.FRAGMENT_STATE);
			}
		} else { // 2
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				context.reportSyntaxViolation(new StringBuffer("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(0)) ||
					!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(1))))
				context.reportSyntaxViolation("'%' is not followed by two hex digits.");

			context.buffer.appendCodePoint(c);
		}
	}
}
