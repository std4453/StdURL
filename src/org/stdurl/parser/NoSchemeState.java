package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.FileSchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#no-scheme-state">#no-scheme-state</a>
 */
public class NoSchemeState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if (context.base == null || (context.base.getCannotBeABaseURLInternal() && c != '#')) { // 1
			context.reportSyntaxViolation("Must have a base URL or begin with '#'.");
			context.returnValue = URL.failure;
		} else if (context.base.getCannotBeABaseURLInternal() && c == '#') { // 2
			context.setScheme(context.base.getSchemeInternal());
			context.path.addAll(context.base.getPathInternal());
			context.setQuery(context.base.getQueryInternal());
			context.setFragment("");
			context.setCannotBeABaseURL(true);
			context.setState(ParserStates.FRAGMENT_STATE);
		} else if (!FileSchemeHelper.isFileScheme(context.scheme)) { // 3
			context.setState(ParserStates.RELATIVE_STATE);
			context.setPointer(context.pointer - 1);
		} else { // 4
			context.setState(ParserStates.FILE_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
