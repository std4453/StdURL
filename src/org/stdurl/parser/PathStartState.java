package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#path-start-state">#path-start-state</a>
 */
public class PathStartState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		// 1
		boolean flag = SchemeHelper.isSpecialScheme(context.scheme) && c == '\\';
		if (flag) context.reportSyntaxViolation("Backslash should be slash.");

		// 2
		context.setState(ParserStates.PATH_STATE);
		if (c != '/' && !flag) context.setPointer(context.pointer - 1);
	}
}
