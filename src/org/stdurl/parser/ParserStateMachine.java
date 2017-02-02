package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.host.Host;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * The extracted state machine for the {@link BasicURLParser}. It is used in the 11th
 * step of the parser defined in the URL Standard. It is extracted into a separate
 * class, and provided with internal control flags and methods, it can be used by test
 * classes of each separate parser states ( which cannot be achieved only by calling
 * {@linkplain BasicURLParser#parse(String, URL, Charset, URL, int,
 * SyntaxViolationListener) BasicURLParser.parse()} ).<br>
 */
public class ParserStateMachine {
	// ========== STATE MACHINE CONTEXT ==========

	// final parameters from BasicURLParser
	public final int[] codePoints;
	public final int length;
	private final String input; // for syntax violation only, generated from codePoints
	public final URL base;
	public Charset encoding; // changed only in query state, almost final
	public URL url;
	public final int stateOverride;
	public final SyntaxViolationListener listener;

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	// predominant state machine fields
	public int state;
	public int c;
	public int pointer;
	public StringBuffer buffer;

	public boolean atFlag, bracketsFlag, passwordTokenSeenFlag;

	public void setState(int state) {
		this.state = state;
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

	public void setReturnValue(URL returnValue) {
		this.returnValue = returnValue;
	}

	public void setTerminateRequested() {
		this.terminateRequested = true;
	}

	public ParserStateMachine(
			int[] inputCodePoints, URL base, Charset encoding, URL url, int stateOverride,
			SyntaxViolationListener listener) {
		this.codePoints = inputCodePoints;
		this.length = inputCodePoints.length;
		this.input = StringHelper.toString(this.codePoints);
		this.base = base;
		this.encoding = encoding == null ? EncodingHelper.UTF8 : encoding;
		this.url = url == null ? URL.createInternal() : this.url;
		this.stateOverride = stateOverride;
		this.listener = listener;

		this.pointer = 0;
		this.buffer = new StringBuffer();
		this.atFlag = this.bracketsFlag = this.passwordTokenSeenFlag = false;

		this.setAllURLFields();
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

	private void setAllURLFields() {
		this.scheme = this.url.getSchemeInternal();
		this.username = this.url.getUsernameInternal();
		this.password = this.url.getPasswordInternal();
		this.host = this.url.getHostInternal();
		this.port = this.url.getPortInternal();
		this.cannotBeABaseURL = this.url.getCannotBeABaseURLInternal();
		if (this.path == null) this.path = new ArrayList<>();
		this.path.addAll(this.url.getPathInternal());
		this.query = this.url.getQueryInternal();
		this.fragment = this.url.getFragmentInternal();
	}

	private void constructFinalURL() {
		this.url.setInternal(this.scheme, this.username, this.password,
				this.host, this.port,
				this.path, this.query, this.fragment,
				this.cannotBeABaseURL, null,
				this.encoding == EncodingHelper.UTF8 ? null : this.encoding);
	}

	// ========== STATE MACHINE EXECUTION ===========

	public void run(int initialState) {
		this.state = initialState;

		this.c = this.codePoints[this.pointer];
		while (true) {
			int stateCode = this.state;
			IParserState state = ParserStates.getState(stateCode);
			try {
				state.execute(this);
			} catch (Throwable t) {
				System.err.printf("Exception caught while execution state %d. \n",
						stateCode);
				t.printStackTrace();

				this.returnValue = URL.failure;
				return;
			}

			// early terminations
			if (this.returnValue != null) return;
			if (this.terminateRequested) break;

			if (this.pointer == this.length) break;
			++this.pointer;
			if (this.pointer == this.length) this.c = 0; // use 0 to represent EOF
			else this.c = this.codePoints[this.pointer];
		}

		this.constructFinalURL();
		this.returnValue = this.url;
	}

	// ========== REMAINING ==========

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
