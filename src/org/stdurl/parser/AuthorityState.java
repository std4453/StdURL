package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.encoding.PercentEncoder;
import org.stdurl.encoding.UserinfoEncodeSet;
import org.stdurl.helpers.SchemeHelper;
import org.stdurl.helpers.StringHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#authority-state">#authority-state</a>
 */
public class AuthorityState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		// 1
		if (c == '@') {
			context.reportSyntaxViolation("'@' unexpected."); // 1.1
			if (context.atFlag) // 1.2
				context.buffer.append("%40");
			context.setAtFlag(true); // 1.3

			// 1.4
			int[] bufferCPs = StringHelper.toCodePoints(context.buffer.toString());
			for (int codePoint : bufferCPs) {
				if (codePoint == ':' && !context.passwordTokenSeenFlag) {
					context.setPasswordTokenSeenFlag(true);
					continue;
				}
				String encodedCodePoints = PercentEncoder.utf8Encode(
						codePoint, UserinfoEncodeSet.instance);
				if (context.passwordTokenSeenFlag)
					context.setPassword(context.password + encodedCodePoints);
				else context.setUsername(context.username + encodedCodePoints);
			}

			context.buffer.setLength(0); // 1.5
		} else // 2
			if (c == 0 || "/?#".indexOf(c) != -1 ||
					(c == '\\' && SchemeHelper.isSpecialScheme(context.scheme)))
				if (context.atFlag && context.buffer.length() == 0) {
					context.reportSyntaxViolation("Empty host forbidden.");
					context.setReturnValue(URL.failure);
				} else {
					context.setPointer(context.pointer - 1
							- context.buffer.codePointCount(0, context.buffer.length()));
					context.buffer.setLength(0);
					context.setState(ParserStates.HOST_STATE);
				}
			else context.buffer.appendCodePoint(c);
	}
}
