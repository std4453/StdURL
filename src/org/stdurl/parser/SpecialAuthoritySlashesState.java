package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-authority-slashes-state">#special-authority-slashes-state</a>
 */
public class SpecialAuthoritySlashesState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		if (context.c == '/' && context.getRemainingAt(0) == '/') {
			context.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			context.setPointer(context.pointer + 1);
		} else {
			context.reportSyntaxViolation("Slash should be followed by another slash.");
			context.setState(ParserStates.SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
