package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#relative-slash-state">#relative-slash-state</a>
 */
public class RelativeSlashState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		// 1
		if (c == '/' || (SchemeHelper.isSpecialScheme(context.scheme) && c == '\\')) {
			if (c == '\\')
				context.reportSyntaxViolation("Backslash should be slash.");
			context.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
		} else { // 2
			context.setUsername(context.base.getUsernameInternal());
			context.setPassword(context.base.getPasswordInternal());
			context.setHost(context.base.getHostInternal());
			context.setPort(context.base.getPortInternal());
			context.setState(ParserStates.PATH_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
