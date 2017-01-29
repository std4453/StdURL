package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.host.Host;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Context of the parser, used to pass data between states
 */
class ParserContext {
	// ========== CONTEXTUAL FIELDS ==========

	public int state;
	public int c;

	public SyntaxViolationListener listener;

	public String input;
	public int stateOverride;
	public int[] codePoints;
	public int length;
	public URL base;
	public Charset encoding;
	public int pointer;
	public boolean atFlag, bracketsFlag, passwordTokenSeenFlag;
	public StringBuffer buffer;

	/**
	 * If non-null indicates that an error as occurred while executing the state
	 * machine algorithm, that the program should terminate without further operations
	 * and returning directly the value of {@code returnValue};
	 */
	public URL returnValue = null;
	/**
	 * If true indicates that the state machine algorithm should terminate after
	 * necessary termination actions.
	 */
	public boolean terminateRequested;

	// Empty constructor
	public ParserContext() {
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setC(int c) {
		this.c = c;
	}

	public void setListener(SyntaxViolationListener listener) {
		this.listener = listener;
	}

	public void setBase(URL base) {
		this.base = base;
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	public void setBuffer(StringBuffer buffer) {
		this.buffer = buffer;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public void setStateOverride(int stateOverride) {
		this.stateOverride = stateOverride;
	}

	public void setCodePoints(int[] codePoints) {
		this.codePoints = codePoints;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;
	}

	public void setAtFlag(boolean atFlag) {
		this.atFlag = atFlag;
	}

	public void setBracketsFlag(boolean bracketsFlag) {
		this.bracketsFlag = bracketsFlag;
	}

	public void setPasswordTokenSeenFlag(boolean passwordTokenSeenFlag) {
		this.passwordTokenSeenFlag = passwordTokenSeenFlag;
	}

	public void setReturnValue(URL returnValue) {
		this.returnValue = returnValue;
	}

	public void setTerminateRequested(boolean terminateRequested) {
		this.terminateRequested = terminateRequested;
	}

	// ========== URL PARTS ==========
	// @see URL
	// default values are identical to those in URL.java

	public String scheme = "";
	public String username = "";
	public String password = "";
	public Host host = null;
	public int port = -1;
	public List<String> path = new ArrayList<>();
	public String query = null;
	public String fragment = null;
	public boolean cannotBeABaseURL = false;

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setCannotBeABaseURL(boolean cannotBeABaseURL) {
		this.cannotBeABaseURL = cannotBeABaseURL;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

	// ========== ALTERNATIVE FOR REMAINING ==========

	/**
	 * Gets the ith character of <i>remaining</i> (by definition).
	 *
	 * @param index
	 * 		The index of the character to get.
	 *
	 * @return The character, U+0000 if no such character exists.
	 */
	public int getRemainingAt(int index) {
		index = this.pointer + 1 + index;
		if (index >= this.length || index < 0) return 0;
		return this.codePoints[index];
	}

	/**
	 * Gets the length of <i>remaining</i> (by definition);
	 *
	 * @return length of remaining.
	 */
	public int getRemainingLength() {
		return this.length - this.pointer - 1;
	}

	// ========== CONVENIENCE METHODS ==========

	/**
	 * SVMT stands for <i>Syntax Violation Message Template</i>.
	 */
	private static final String basicURLParserSVMT =
			"Syntax violation while parsing URL: \"%s\"\n" +
					"Current state: %d, Violation index: %d\nMessage: %s";

	public void reportSyntaxViolation(String msg) {
		reportSyntaxViolation(this.listener, this.input, this.state, this.pointer, msg);
	}

	public static void reportSyntaxViolation(
			SyntaxViolationListener listener,
			String input,
			int state,
			int pointer,
			String msg) {
		if (listener != null)
			listener.onSyntaxViolation(String.format(basicURLParserSVMT,
					input, state, pointer, msg));
	}
}
