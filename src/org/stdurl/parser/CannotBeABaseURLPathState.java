package org.stdurl.parser;

import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.percent.PercentEncoder;
import org.stdurl.percent.SimpleEncodeSet;

/**
 * @see <a href="https://url.spec.whatwg.org/#cannot-be-a-base-url-path-state">#cannot-be-a-base-url-path-state</a>
 */
public class CannotBeABaseURLPathState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (c == '?') { // 1
			machine.setQuery("");
			machine.setState(ParserStates.QUERY_STATE);
		} else if (c == '#') { // 2
			machine.setFragment("");
			machine.setState(ParserStates.FRAGMENT_STATE);
		} else { // 3
			if (c != 0 && !CodePointHelper.isURLCodePoint(c) && c != '%')
				machine.reportValidationError(new StringBuilder("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(0)) ||
					!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(1))))
				machine.reportValidationError("'%' is not followed by two hex digits.");

			if (c != 0) {
				String encoded = PercentEncoder.utf8Encode(c, SimpleEncodeSet.instance);
				machine.path.set(0, machine.path.get(0) + encoded);
			}
		}
	}
}
