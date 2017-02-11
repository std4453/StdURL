package org.stdurl.parser;

import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.percent.PercentEncoder;
import org.stdurl.percent.SimpleEncodeSet;

/**
 * @see <a href="https://url.spec.whatwg.org/#fragment-state">#fragment-state</a>
 */
public class FragmentState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		// equal to definition
		int c = machine.c;

		// U+0000 is omitted, because we represent EOF by 0x0000, therefore they coincide.
		if (c != 0) {
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				machine.reportValidationError(new StringBuffer("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(0)) ||
					!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(1))))
				machine.reportValidationError("'%' is not followed by two hex digits.");

			String encoded = PercentEncoder.utf8Encode(c, SimpleEncodeSet.instance);
			machine.setFragment(machine.fragment + encoded);
		}
	}
}
