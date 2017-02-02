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
	public void execute(ParserStateMachine machine) throws Throwable {
		machine.setScheme(SchemeHelper.SCHEME_FILE);

		switch (machine.c) {
			case 0:
				if (machine.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								machine.base.getSchemeInternal())) {
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery(machine.base.getQueryInternal());
				}
				break;
			case '/':
			case '\\':
				if (machine.c == '\\')
					machine.reportSyntaxViolation("Backslash should be slash.");
				machine.setState(ParserStates.FILE_SLASH_STATE);
				break;
			case '?':
				if (machine.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								machine.base.getSchemeInternal())) {
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery("");
					machine.setState(ParserStates.QUERY_STATE);
				}
				break;
			case '#':
				if (machine.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								machine.base.getSchemeInternal())) {
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery(machine.base.getQueryInternal());
					machine.setFragment("");
					machine.setState(ParserStates.FRAGMENT_STATE);
				}
				break;
			default:
				boolean flag = machine.base != null &&
						SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
								machine.base.getSchemeInternal());
				boolean flag2 = false;
				if (flag) {
					flag2 = !FileSchemeHelper.isWindowsDriveLetter(
							StringHelper.toString(machine.c, machine.getRemainingAt(0)));
					flag2 |= machine.getRemainingLength() == 1;
					flag2 |= "/\\?#".indexOf(machine.getRemainingAt(1)) == -1;
				}

				if (flag && flag2) { // 1
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());

					// shorten url's path, hardcoded
					PathHelper.shortenPath(machine.path, machine.scheme);
				} else if (flag) // 2
					machine.reportSyntaxViolation("Unexpected file path.");

				// 3
				machine.setPointer(machine.pointer - 1);
				machine.setState(ParserStates.PATH_STATE);
		}
	}
}
