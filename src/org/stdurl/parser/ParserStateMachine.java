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
 * ISyntaxViolationListener) BasicURLParser.parse()} ).<br>
 */
public class ParserStateMachine {
	// ========== PARAMETERS ==========

	public int[] codePoints;
	public int length;
	private String input; // for syntax violation only, generated from codePoints
	public URL base;
	public Charset encoding; // changed only in query state, almost final
	public URL url;
	public int stateOverride;
	public ISyntaxViolationListener listener;

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
	}

	// ========== MACHINE CONTEXT ==========

	public int state;
	public int c;
	public int pointer;
	public StringBuffer buffer = new StringBuffer();

	public boolean atFlag = false,
			bracketsFlag = false,
			passwordTokenSeenFlag = false;

	public void setState(int state) {
		this.state = state;
	}

	public void setPointer(int pointer) {
		this.pointer = pointer;

		// c is updated to be the character that this.pointer points to
		// we use 0 to represent out-ouf-bounds pointer or EOF
		if (this.pointer >= this.length || this.pointer < 0) this.c = 0;
		else this.c = this.codePoints[this.pointer];
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

	// ========== EARLY TERMINATION ==========

	/**
	 * When non-null, indicates that the state machine should terminate and return the
	 * value without calling {@link #afterLoop()}. (e.g. When an exception is thrown)
	 */
	private URL returnValue = null;

	public void returnDirectly(URL returnValue) {
		this.returnValue = returnValue;
	}

	/**
	 * When {@code true}, indicates that the state machine should terminate after calling
	 * {@link #afterLoop()}
	 */
	private boolean terminateRequested = false;

	public void terminate() {
		this.terminateRequested = true;
	}

	// ========== MACHINE LOGIC ==========

	/**
	 * Standard constructor using the parameters specified in #concept-basic-parser of
	 * the URL Standard.
	 *
	 * @param inputCodePoints
	 * 		The input code points to process.
	 * @param base
	 * 		The base {@link URL}. (optional)
	 * @param encoding
	 * 		The overriding encoding to use. (optional)
	 * @param stateOverride
	 * 		The {@code stateOverride} parameter.
	 * @param listener
	 * 		The listener of the parser state machine. (optional)
	 * @param url
	 * 		The given {@link URL}. (optional)
	 */
	public ParserStateMachine(
			int[] inputCodePoints, URL base, Charset encoding, int stateOverride,
			ISyntaxViolationListener listener, URL url) {
		this(inputCodePoints, base, encoding, stateOverride, listener);

		this.url = url;
		if (url == null) this.url = URL.createInternal();
		this.setURLFields(this.url.getSchemeInternal(),
				this.url.getUsernameInternal(),
				this.url.getPasswordInternal(),
				this.url.getHostInternal(),
				this.url.getPortInternal(),
				this.url.getCannotBeABaseURLInternal(),
				this.url.getPathInternal(),
				this.url.getQueryInternal(),
				this.url.getFragmentInternal());
	}

	/**
	 * Set the fields in this instance that represent the respective URL fields.
	 *
	 * @param scheme
	 * 		The URL scheme.
	 * @param username
	 * 		The username of the URL.
	 * @param password
	 * 		The password of the URL.
	 * @param host
	 * 		The host of the URL.
	 * @param port
	 * 		The port of the URL.
	 * @param cannotBeABaseURL
	 * 		Whether the URL can be a base URL.
	 * @param path
	 * 		The path of the URL.
	 * @param query
	 * 		The query string of the URL.
	 * @param fragment
	 * 		The fragment string of the URL.
	 */
	protected void setURLFields(
			String scheme, String username, String password, Host host, int port,
			boolean cannotBeABaseURL, List<String> path, String query, String fragment) {
		this.scheme = scheme;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.cannotBeABaseURL = cannotBeABaseURL;
		if (this.path == null) this.path = new ArrayList<>();
		this.path.addAll(path);
		this.query = query;
		this.fragment = fragment;
	}

	/**
	 * Internal constructor, used by classes that extends {@link ParserStateMachine}
	 * and do not want to use the standard constructor.<br>
	 * The parameters {@code codePoints}, {@code base}, {@code encoding}, {@code
	 * stateOverride} and {@code listener} are always required in an instance of
	 * {@link ParserStateMachine}, all public constructors of
	 * {@link ParserStateMachine} and classes that extend {@link ParserStateMachine}
	 * must provide these while construction.
	 *
	 * @param codePoints
	 * 		The input code points to process.
	 * @param base
	 * 		The base {@link URL}. (optional)
	 * @param encoding
	 * 		The overriding encoding to use. (optional)
	 * @param stateOverride
	 * 		The {@code stateOverride} parameter.
	 * @param listener
	 * 		The listener of the parser state machine. (optional)
	 */
	protected ParserStateMachine(
			int[] codePoints, URL base, Charset encoding, int stateOverride,
			ISyntaxViolationListener listener) {
		this.codePoints = codePoints;
		this.length = codePoints.length;
		this.input = StringHelper.toString(this.codePoints);

		this.base = base;
		this.encoding = encoding == null ? EncodingHelper.UTF8 : encoding;
		this.stateOverride = stateOverride;
		this.listener = listener;
	}

	/**
	 * Runs the {@link ParserStateMachine}. {@link #returnValue} will represent the
	 * final result after the this method returns.
	 *
	 * @param initialState
	 * 		The initial state of the {@link ParserStateMachine}, will be overridden if
	 * 		{@code stateOverride} is set.
	 */
	public URL run(int initialState) {
		this.beforeLoop(initialState);
		for (
				this.setPointer(0);
				this.pointer <= this.length;
				this.setPointer(this.pointer + 1)) {
			this.loop();
			if (this.returnValue != null) return this.returnValue;
			if (this.terminateRequested) break;
		}
		this.afterLoop();
		return this.url;
	}

	/**
	 * Initialize the fields for the loop.
	 *
	 * @param initialState
	 * 		The initial state of the {@link ParserStateMachine}, will be overridden if
	 * 		{@code stateOverride} is set.
	 */
	protected void beforeLoop(int initialState) {
		if (ParserStates.hasState(this.stateOverride))
			initialState = this.stateOverride; // stateOverride overrides initialState
		this.state = initialState;
	}

	/**
	 * Execute the main loop of the state machine once.
	 */
	protected void loop() {
		int stateCode = this.state;
		try {
			ParserStates.getState(stateCode).execute(this);
		} catch (Throwable t) {
			System.err.println("Exception caught while executing state " + stateCode);
			t.printStackTrace();
			this.returnValue = URL.failure;
		}
	}

	/**
	 * Called after the loop.
	 */
	protected void afterLoop() {
		this.url.setInternal(this.scheme, this.username, this.password,
				this.host, this.port,
				this.path, this.query, this.fragment,
				this.cannotBeABaseURL, null,
				this.encoding == EncodingHelper.UTF8 ? null : this.encoding);
	}

	// ========== URL PARTS ==========

	public String scheme;
	public String username;
	public String password;
	public Host host;
	public int port;
	public List<String> path;
	public String query;
	public String fragment;
	public boolean cannotBeABaseURL;

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

	// ========== ALTERNATIVE OF REMAINING ==========

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

	// ========== SYNTAX VIOLATION ==========

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
			ISyntaxViolationListener listener,
			String input,
			int state,
			int pointer,
			String msg) {
		if (listener != null)
			listener.onSyntaxViolation(String.format(basicURLParserSVMT,
					input, state, pointer, msg));
	}
}
