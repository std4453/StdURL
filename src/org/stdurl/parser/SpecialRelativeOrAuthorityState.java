package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-relative-or-authority-state">#special-relative-or-authoritu-state</a>
 */
public class SpecialRelativeOrAuthorityState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;
		if (c == '/' && context.getRemainingAt(0) == '/') {
			context.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			context.setPointer(context.pointer + 1);
		} else {
			context.reportSyntaxViolation("Should start with \"//\".");
			context.setState(ParserStates.RELATIVE_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
