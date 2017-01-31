package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.FileSchemeHelper;
import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#file-slash-state">#file-slash-state</a>
 */
public class FileSlashState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;

		if ("\\/".indexOf(c) != -1) { // 1
			if (c == '\\')
				context.reportSyntaxViolation("Backslash should be slash.");
			context.setState(ParserStates.FILE_HOST_STATE);
		} else { // 2
			URL base = context.base;
			if (base != null &&
					SchemeHelper.SCHEME_FILE.equalsIgnoreCase(base.getSchemeInternal()) &&
					base.getPathInternal().size() >= 1 &&
					FileSchemeHelper.isWindowsDriveLetter(base.getPathInternal().get(0)))
				context.path.add(base.getPathInternal().get(0));
			context.setState(ParserStates.PATH_STATE);
			context.setPointer(context.pointer - 1);
		}
	}
}
