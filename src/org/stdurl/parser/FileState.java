package org.stdurl.parser;

import org.stdurl.helpers.FileSchemeHelper;
import org.stdurl.helpers.PathHelper;
import org.stdurl.helpers.SchemeHelper;
import org.stdurl.helpers.StringHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#file-state">#file-state</a>
 */
public class FileState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		context.setScheme(SchemeHelper.SCHEME_FILE);

		switch (context.c) {
			case 0:
				if (context.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								context.base.getSchemeInternal())) {
					context.setHost(context.base.getHostInternal());
					context.path.addAll(context.base.getPathInternal());
					context.setQuery(context.base.getQueryInternal());
				}
				break;
			case '/':
			case '\\':
				if (context.c == '\\')
					context.reportSyntaxViolation("Backslash should be slash.");
				context.setState(ParserStates.FILE_SLASH_STATE);
				break;
			case '?':
				if (context.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								context.base.getSchemeInternal())) {
					context.setHost(context.base.getHostInternal());
					context.path.addAll(context.base.getPathInternal());
					context.setQuery("");
					context.setState(ParserStates.QUERY_STATE);
				}
				break;
			case '#':
				if (context.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								context.base.getSchemeInternal())) {
					context.setHost(context.base.getHostInternal());
					context.path.addAll(context.base.getPathInternal());
					context.setQuery(context.base.getQueryInternal());
					context.setFragment("");
					context.setState(ParserStates.FRAGMENT_STATE);
				}
				break;
			default:
				boolean flag = context.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								context.base.getSchemeInternal());
				boolean flag2 = false;
				if (flag) {
					flag2 = !FileSchemeHelper.isWindowsDriveLetter(
							StringHelper.toString(context.c, context.getRemainingAt(0)));
					flag2 |= context.getRemainingLength() == 1;
					flag2 |= "/\\?#".indexOf(context.getRemainingAt(1)) == -1;
				}

				if (flag && flag2) { // 1
					context.setHost(context.base.getHostInternal());
					context.path.addAll(context.base.getPathInternal());

					// shorten url's path, hardcoded
					PathHelper.shortenPath(context.path, context.scheme);
				} else if (flag) // 2
					context.reportSyntaxViolation("Unexpected file path.");

				// 3
				context.setPointer(context.pointer - 1);
				context.setState(ParserStates.PATH_STATE);
		}
	}
}
