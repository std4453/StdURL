package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.FileSchemeHelper;
import org.stdurl.helpers.SchemeHelper;

import java.util.Objects;

/**
 * @see <a href="https://url.spec.whatwg.org/#scheme-state">#scheme-state</a>
 */
public class SchemeState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		int c = context.c;
		boolean stateOverrideGiven = ParserStates.hasState(context.stateOverride);

		if (ASCIIHelper.isASCIIAlphanumeric(c) || "+-.".indexOf(c) != -1) { // 1
			context.buffer.appendCodePoint(ASCIIHelper.toLowerCase(c));
		} else if (c == ':') { // 2
			if (stateOverrideGiven) { // 2.1
				String buf = context.buffer.toString();
				if (SchemeHelper.isSpecialScheme(context.scheme) ^
						SchemeHelper.isSpecialScheme(buf)) {
					context.setTerminateRequested(true);
					return;
				}
			}

			context.setScheme(context.buffer.toString()); // 2.2
			context.buffer.setLength(0); // 2.3

			if (stateOverrideGiven) { // 2.4
				context.setTerminateRequested(true);
				return;
			}

			if (FileSchemeHelper.isFileScheme(context.scheme)) { // 2.5
				if (context.getRemainingAt(0) != '/' ||
						context.getRemainingAt(1) != '/')
					context.reportSyntaxViolation(
							"Path of file scheme doesn't start with \"//\".");
				context.setState(ParserStates.FILE_STATE);
			} else if (SchemeHelper.isSpecialScheme(context.scheme)) {
				// 2.6 + 2.7
				context.setState(context.base != null &&
						Objects.equals(context.base.getSchemeInternal(), context.scheme) ?
						ParserStates.SPECIAL_RELATIVE_OR_AUTHORITY_STATE :
						ParserStates.SPECIAL_AUTHORITY_SLASHES_STATE);
			} else if (context.getRemainingAt(0) == '/') { // 2.8
				context.setState(ParserStates.PATH_OR_AUTHORITY_STATE);
				context.setPointer(context.pointer + 1);
			} else { // 2.9
				context.setCannotBeABaseURL(true);
				context.path.add("");
				context.setState(ParserStates.CANNOTBEABASEURL_PATH_STATE);
			}
		} else if (!stateOverrideGiven) { // 3
			context.buffer.setLength(0);
			context.setState(ParserStates.NO_SCHEME_STATE);
			context.setPointer(-1);
		} else { //4
			context.reportSyntaxViolation("State override does nothing.");
			context.setReturnValue(URL.failure);
		}
	}
}
