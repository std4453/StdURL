package org.stdurl.parser;

import org.stdurl.helpers.*;
import org.stdurl.percent.DefaultEncodeSet;
import org.stdurl.percent.PercentEncoder;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-state">#path-state</a>
 */
public class PathState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		// 1
		boolean flag1 = SchemeHelper.isSpecialScheme(machine.scheme) && c == '\\';
		if (c == 0 || c == '/' || flag1 ||
				(!ParserStates.hasState(machine.stateOverride)
						&& "?#".indexOf(c) != -1)) {
			if (flag1) // 1.1
				machine.reportValidationError("Backslash should be slash.");

			String buf = machine.buffer.toString();

			if (PathHelper.isDoubleDotPathSegment(buf)) { // 1.2
				PathHelper.shortenPath(machine.path, machine.scheme);
				if (c != '/' && !flag1)
					machine.path.add("");
			} else if (PathHelper.isSingleDotPathSegment(buf) &&
					c != '/' && !flag1) { // 1.3
				machine.path.add("");
			} else if (!PathHelper.isSingleDotPathSegment(buf)) { // 1.4
				if (SchemeHelper.SCHEME_FILE.equalsIgnoreCase(machine.scheme) &&
						machine.path.isEmpty() &&
						FileSchemeHelper.isWindowsDriveLetter(buf)) { // 1.4.1
					if (machine.host != null) machine.reportValidationError(
							"Local file shouldn't have a host");
					machine.setHost(null);
					machine.buffer.setCharAt(
							machine.buffer.offsetByCodePoints(0, 1), ':');
				}
				machine.path.add(machine.buffer.toString());
			}

			machine.buffer.setLength(0); // 1.5

			if (c == '?') { // 1.6
				machine.setQuery("");
				machine.setState(ParserStates.QUERY_STATE);
			} else if (c == '#') { // 1.7
				machine.setFragment("");
				machine.setState(ParserStates.FRAGMENT_STATE);
			}
		} else { // 2
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				machine.reportValidationError(new StringBuilder("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (
					!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(0)) ||
							!ASCIIHelper.isASCIIHexDigit(machine.getRemainingAt(1))))
				machine.reportValidationError("'%' is not followed by two hex digits.");

			String encoded = PercentEncoder.utf8Encode(c, DefaultEncodeSet.instance);
			machine.buffer.append(encoded);
		}
	}
}
