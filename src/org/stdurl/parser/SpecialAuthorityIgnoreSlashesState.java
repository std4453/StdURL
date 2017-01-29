package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#special-authority-ignore-slashes-state">#special-authority-ignore-slashes-state</a>
 */
public class SpecialAuthorityIgnoreSlashesState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		if ("/\\".indexOf(context.c) != -1)
			context.reportSyntaxViolation("Slash or backslash unexpected.");
		else {
			context.setState(ParserStates.AUTHORITY_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
