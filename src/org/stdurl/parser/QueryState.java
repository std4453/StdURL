package org.stdurl.parser;

import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.CodePointHelper;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.SchemeHelper;
import org.stdurl.percent.PercentEncoder;

/**
 * @see <a href="https://url.spec.whatwg.org/#query-state">#query-state</a>
 */
public class QueryState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		if (c == 0 || (!ParserStates.hasState(machine.stateOverride) && c == '#')) { // 1
			if (!SchemeHelper.isSpecialScheme(machine.scheme) ||
					SchemeHelper.SCHEME_WS.equalsIgnoreCase(machine.scheme) ||
					SchemeHelper.SCHEME_WSS.equalsIgnoreCase(machine.scheme))
				machine.setEncoding(EncodingHelper.UTF8); // 1.1

			byte[] encoded = machine.buffer.toString().getBytes(machine.encoding); // 1.2

			// 1.3
			StringBuilder sb = new StringBuilder(
					machine.query == null ? "" : machine.query);
			for (byte b : encoded) {
				int i = ((int) b) & 0xFF;
				if (i < 0x21 || i > 0x7E ||
						i == 0x22 || i == 0x23 || i == 0x3C || i == 0x3E)
					PercentEncoder.encode(b, sb);
				else sb.appendCodePoint(i);
			}
			machine.setQuery(sb.toString());

			machine.buffer.setLength(0); // 1.4

			if (c == '#') { // 1.5
				machine.setFragment("");
				machine.setState(ParserStates.FRAGMENT_STATE);
			}
		} else { // 2
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				machine.reportValidationError(new StringBuffer("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(0)) ||
					!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(1))))
				machine.reportValidationError("'%' is not followed by two hex digits.");

			machine.buffer.appendCodePoint(c);
		}
	}
}
