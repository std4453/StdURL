package org.stdurl.parser;

import org.stdurl.encoding.PercentEncoder;
import org.stdurl.encoding.SimpleEncodeSet;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#fragment-state">#fragment-state</a>
 */
public class FragmentState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		// equal to definition
		int c = context.c;

		// U+0000 is omitted, because we represent EOF by 0x0000, therefore they coincide.
		if (c != 0) {
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				context.reportSyntaxViolation(new StringBuffer("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(0)) ||
					!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(1))))
				context.reportSyntaxViolation("'%' is not followed by two hex digits.");

			String encoded = PercentEncoder.utf8Encode(c, SimpleEncodeSet.instance);
			context.setFragment(context.fragment + encoded);
		}
	}
}
