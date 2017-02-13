package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.host.Host;
import org.stdurl.host.HostParser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

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

	// a stands for atFlag, sets the atFlag
	public static MachineContext a(MachineContext context) {
		return new MachineContext(context.state, context.pointer,
				new StringBuffer(context.buffer),
				true, context.bracketsFlag, context.passwordTokenSeenFlag);
	}

	// b stands for bracketsFlag, sets the bracketsFlag
	public static MachineContext b(MachineContext context) {
		return new MachineContext(context.state, context.pointer,
				new StringBuffer(context.buffer),
				context.atFlag, true, context.passwordTokenSeenFlag);
	}

	// p stands for passwordTokenSeenFlag, sets the passwordTokenSeenFlag
	public static MachineContext p(MachineContext context) {
		return new MachineContext(context.state, context.pointer,
				new StringBuffer(context.buffer),
				context.atFlag, context.bracketsFlag, true);
	}

	public static MachineContext c(int state, int pointer, StringBuffer buffer) {
		return c(state, pointer, buffer, false, false, false);
	}

	public static MachineContext c(int state, int pointer, String bufferStr) {
		return c(state, pointer, new StringBuffer(bufferStr));
	}

	public static MachineContext c(MachineContext context, String bufferStr) {
		return c(context.state, context.pointer, bufferStr);
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

	public static MachineURLParts u(
			String scheme, String username, String password, String hostStr, int port,
			boolean cannotBeABaseURL, String path, String query, String fragment) {
		Host host = HostParser.parseHost(hostStr, null);
		return a(u(scheme, username, password, host, port, cannotBeABaseURL,
				null, query, fragment), path);
	}

	public static MachineURLParts u(MachineURLParts parts, String hostStr, int port) {
		Host host = null;
		if (hostStr != null) host = HostParser.parseHost(hostStr, null);
		return u(parts.scheme, parts.username, parts.password, host, port,
				parts.cannotBeABaseURL, new ArrayList<>(parts.path), parts.query,
				parts.fragment);
	}

	public static MachineURLParts u(String scheme, boolean cannotBeABaseURL) {
		return u(scheme, "", "", (Host) null, -1, cannotBeABaseURL, null, null,
				null);
	}

	public static MachineURLParts u(String scheme) {
		return u(scheme, false);
	}

	public static MachineURLParts u() {
		// default values from URL.java
		return u("");
	}

	// a stands for append
	public static MachineURLParts a(MachineURLParts parts, String path) {
		String[] segments = new String[0];
		if (path != null) segments = path.split(Pattern.quote("/"), -1);
		List<String> newPath = new ArrayList<>(parts.path);
		Collections.addAll(newPath, segments);
		return u(parts.scheme, parts.username, parts.password, parts.host, parts.port,
				parts.cannotBeABaseURL, newPath, parts.query, parts.fragment);
	}

	// c stands for credentials
	public static MachineURLParts c(
			MachineURLParts parts, String username, String password) {
		return u(parts.scheme, username, password, parts.host, parts.port,
				parts.cannotBeABaseURL, new ArrayList<>(parts.path), parts.query,
				parts.fragment);
	}

	// a stands for additional
	public static MachineURLParts a(
			MachineURLParts parts, String query, String fragment) {
		return u(parts.scheme, parts.username, parts.password, parts.host, parts.port,
				parts.cannotBeABaseURL, new ArrayList<>(parts.path), query, fragment);
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

	public static MachineParameters p(String input, URL base, int stateOverride) {
		return p(input, base, EncodingHelper.UTF8, stateOverride);
	}

	public static MachineParameters p(String input, URL base) {
		return p(input, base, ParserStates.NO_SUCH_STATE);
	}

	public static MachineParameters p(String input, String baseStr) {
		return p(input, BasicURLParser.parse(baseStr, (IValidationErrorListener) null));
	}

	public static MachineParameters p(String input, Charset encoding, int stateOverride) {
		return p(input, null, encoding, stateOverride);
	}

	public static MachineParameters p(String input, int stateOverride) {
		return p(input, null, EncodingHelper.UTF8, stateOverride);
	}

	public static MachineParameters p(String input) {
		return p(input, ParserStates.NO_SUCH_STATE);
	}

	// c stands for charset
	public static MachineParameters c(MachineParameters params, Charset encoding) {
		return p(params.codePoints, params.base, encoding, params.stateOverride);
	}
}
