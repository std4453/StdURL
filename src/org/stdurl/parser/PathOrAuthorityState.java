package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-or-authority-state">#path-or-authority-state</a>
 */
public class PathOrAuthorityState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		if (context.c == '/')
			context.setState(ParserStates.AUTHORITY_STATE);
		else {
			context.setState(ParserStates.PATH_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
