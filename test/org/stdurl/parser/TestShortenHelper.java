package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.host.Host;

import java.nio.charset.Charset;
import java.util.List;

/**
 * The static methods in this class helps shorten the test code in this package.<br>
 * Usage: <br>
 * {@code import static org.stdurl.parser.TestShortenHelper.*;}
 */
public class TestShortenHelper {
	// MACHINE CONTEXT

	public static MachineContext c(
			int state, int pointer, StringBuffer buffer,
			boolean atFlag, boolean bracketsFlag, boolean passwordTokenSeenFlag) {
		return new MachineContext(state, pointer, buffer,
				atFlag, bracketsFlag, passwordTokenSeenFlag);
	}

	public static MachineContext c(int state, int pointer, StringBuffer buffer) {
		return c(state, pointer, buffer, false, false, false);
	}

	public static MachineContext c(int state, int pointer, String bufferStr) {
		return c(state, pointer, new StringBuffer(bufferStr));
	}

	public static MachineContext c(int state, int pointer) {
		return c(state, pointer, (StringBuffer) null);
	}

	// MACHINE URL PARTS

	public static MachineURLParts u(
			String scheme, String username, String password, Host host, int port,
			boolean cannotBeABaseURL, List<String> path, String query,
			String fragment) {
		return new MachineURLParts(scheme, username, password, host, port,
				cannotBeABaseURL, path, query, fragment);
	}

	public static MachineURLParts u() {
		// default values from URL.java
		return u("", "", "", null, -1, false, null, null, null);
	}

	// MACHINE PARAMETERS

	public static MachineParameters p(
			int[] codePoints, URL base, Charset encoding, int stateOverride) {
		return new MachineParameters(codePoints, base, encoding, stateOverride);
	}

	public static MachineParameters p(
			String input, URL base, Charset encoding, int stateOverride) {
		return p(StringHelper.toCodePoints(input), base, encoding, stateOverride);
	}

	public static MachineParameters p(String input, int stateOverride) {
		return p(input, null, EncodingHelper.UTF8, stateOverride);
	}

	public static MachineParameters p(String input) {
		return p(input, ParserStates.NO_SUCH_STATE);
	}
}
