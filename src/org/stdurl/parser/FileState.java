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
		int c = machine.c;
		machine.setScheme(SchemeHelper.SCHEME_FILE);

		if ("/\\".indexOf(c) != -1) {
			if (c == '\\') machine.reportValidationError("Backslash should be slash.");
			machine.setState(ParserStates.FILE_SLASH_STATE);
		} else if (machine.base != null && SchemeHelper.SCHEME_FILE.equalsIgnoreCase(
				machine.base.getSchemeInternal()))
			switch (machine.c) {
				case 0:
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery(machine.base.getQueryInternal());
					break;
				case '?':
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery("");
					machine.setState(ParserStates.QUERY_STATE);
					break;
				case '#':
					machine.setHost(machine.base.getHostInternal());
					machine.path.addAll(machine.base.getPathInternal());
					machine.setQuery(machine.base.getQueryInternal());
					machine.setFragment("");
					machine.setState(ParserStates.FRAGMENT_STATE);
					break;
				default:
					boolean flag2 = !FileSchemeHelper.isWindowsDriveLetter(
							StringHelper.toString(machine.c, machine.getRemainingAt(0)));
					flag2 |= machine.getRemainingLength() == 1;
					flag2 |= "/\\?#".indexOf(machine.getRemainingAt(1)) == -1;

					if (flag2) { // 1
						machine.setHost(machine.base.getHostInternal());
						machine.path.addAll(machine.base.getPathInternal());
						PathHelper.shortenPath(machine.path, machine.scheme);
					} else // 2
						machine.reportValidationError("Unexpected file path.");

					// 3
					machine.setPointer(machine.pointer - 1);
					machine.setState(ParserStates.PATH_STATE);
			}
		else {
			machine.setPointer(machine.pointer - 1);
			machine.setState(ParserStates.PATH_STATE);
		}
	}
}
