package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.SchemeHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.percent.PercentEncoder;
import org.stdurl.percent.UserinfoEncodeSet;

/**
 * @see <a href="https://url.spec.whatwg.org/#authority-state">#authority-state</a>
 */
public class AuthorityState implements IParserState {
	@Override
	public void execute(ParserStateMachine machine) throws Throwable {
		int c = machine.c;

		// 1
		if (c == '@') {
			machine.reportSyntaxViolation("'@' unexpected."); // 1.1
			if (machine.atFlag) // 1.2
				machine.buffer.append("%40");
			machine.setAtFlag(true); // 1.3

			// 1.4
			int[] bufferCPs = StringHelper.toCodePoints(machine.buffer.toString());
			for (int codePoint : bufferCPs) {
				if (codePoint == ':' && !machine.passwordTokenSeenFlag) {
					machine.setPasswordTokenSeenFlag(true);
					continue;
				}
				String encodedCodePoints = PercentEncoder.utf8Encode(
						codePoint, UserinfoEncodeSet.instance);
				if (machine.passwordTokenSeenFlag)
					machine.setPassword(machine.password + encodedCodePoints);
				else machine.setUsername(machine.username + encodedCodePoints);
			}

			machine.buffer.setLength(0); // 1.5
		} else // 2
			if (c == 0 || "/?#".indexOf(c) != -1 ||
					(c == '\\' && SchemeHelper.isSpecialScheme(machine.scheme)))
				if (machine.atFlag && machine.buffer.length() == 0) {
					machine.reportSyntaxViolation("Empty host forbidden.");
					machine.setReturnValue(URL.failure);
				} else {
					machine.setPointer(machine.pointer - 1
							- machine.buffer.codePointCount(0, machine.buffer.length()));
					machine.buffer.setLength(0);
					machine.setState(ParserStates.HOST_STATE);
				}
			else machine.buffer.appendCodePoint(c);
	}
}
