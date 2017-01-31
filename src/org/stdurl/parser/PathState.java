package org.stdurl.parser;

import org.stdurl.encoding.DefaultEncodeSet;
import org.stdurl.encoding.PercentEncoder;
import org.stdurl.helpers.*;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-state">#path-state</a>
 */
public class PathState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		// 1
		boolean flag1 = SchemeHelper.isSpecialScheme(context.scheme) && c == '\\';
		if (c == 0 || c == '/' || flag1 ||
				(!ParserStates.hasState(context.stateOverride)
						&& "?#".indexOf(c) != -1)) {
			if (flag1) // 1.1
				context.reportSyntaxViolation("Backslash should be slash.");

			String buf = context.buffer.toString();

			if (PathHelper.isDoubleDotPathSegment(buf)) { // 1.2
				PathHelper.shortenPath(context.path, context.scheme);
				if (c != '/' && !flag1)
					context.path.add("");
			} else if (PathHelper.isSingleDotPathSegment(buf) &&
					c != '/' && !flag1) { // 1.3
				context.path.add("");
			} else if (!PathHelper.isSingleDotPathSegment(buf)) { // 1.4
				if (SchemeHelper.SCHEME_FILE.equalsIgnoreCase(context.scheme) &&
						context.path.isEmpty() &&
						FileSchemeHelper.isWindowsDriveLetter(buf)) { // 1.4.1
					if (context.host != null) context.reportSyntaxViolation(
							"Local file shouldn't have a host");
					context.setHost(null);
					context.buffer.setCharAt(
							context.buffer.offsetByCodePoints(0, 1), ':');
				}
				context.path.add(context.buffer.toString());
			}

			context.buffer.setLength(0); // 1.5

			if (c == '?') { // 1.6
				context.setQuery("");
				context.setState(ParserStates.QUERY_STATE);
			} else if (c == '#') { // 1.7
				context.setFragment("");
				context.setState(ParserStates.FRAGMENT_STATE);
			}
		} else { // 2
			if (!CodePointHelper.isURLCodePoint(c) && c != '%')
				context.reportSyntaxViolation(new StringBuilder("Character '")
						.appendCodePoint(c).append("' unexpected.").toString());
			if (c == '%' && (
					!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(0)) ||
							!ASCIIHelper.isASCIIHexDigit(context.getRemainingAt(1))))
				context.reportSyntaxViolation("'%' is not followed by two hex digits.");

			String encoded = PercentEncoder.utf8Encode(c, DefaultEncodeSet.instance);
			context.buffer.append(encoded);
		}
	}
}
