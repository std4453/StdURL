package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;

import java.nio.charset.Charset;

/**
 * Implements a standard-defined basic URL parser.
 *
 * @see <a href="https://url.spec.whatwg.org/#concept-basic-url-parser">#concept-basic-url-parser</a>
 */
public class BasicURLParser {
	/**
	 * Simplest URL parser, parse the given {@code input} without any special arguments.
	 *
	 * @param input
	 * 		The input string.
	 *
	 * @return The parsed {@link URL} object;
	 */
	public static URL parse(String input) {
		return parse(input, null, ParserStates.NO_SUCH_STATE);
	}

	/**
	 * Internal method, used in various setters in {@link URL}.
	 *
	 * @param input
	 * 		The input string.
	 * @param url
	 * 		The {@link URL} instance.
	 * @param stateOverride
	 * 		Override the state.
	 *
	 * @return The parsed URL.
	 */
	public static URL parse(String input, URL url, int stateOverride) {
		return parse(input, null, null, SimpleParserListener.instance,
				url, stateOverride);
	}

	/**
	 * Parse input {@link String} with {@code base} given and use for all other
	 * parameters the default value.
	 *
	 * @param input
	 * 		The input {@link String}.
	 * @param base
	 * 		The base URL.
	 *
	 * @return The parsed {@link URL}.
	 */
	public static URL parse(String input, URL base) {
		return parse(input, base, null);
	}

	/**
	 * Parse input {@link String} with {@code base} and {@code encodingOverride} given
	 * and use for all other parameters the default value.
	 *
	 * @param input
	 * 		The input {@link String}.
	 * @param base
	 * 		The base URL.
	 * @param encodingOverride
	 * 		Override the encoding. (optional)
	 *
	 * @return The parsed {@link URL}.
	 */
	@SuppressWarnings("SameParameterValue")
	public static URL parse(String input, URL base, Charset encodingOverride) {
		return parse(input, base, encodingOverride, SimpleParserListener.instance,
				null, ParserStates.NO_SUCH_STATE);
	}

	/**
	 * Implements the #concept-basic-url-parser as in the URL Standard.<br>
	 *
	 * We don't use <i>remaining</i> as a separate variable in our state machine
	 * for performance reasons. At any time, {@code remaining[i]} can be retrieved
	 * using {@code codePoints[pointer + 1 + i]}.<br>
	 *
	 * The return value of this method should always be non-null: a failure to parse a
	 * valid URL from the given arguments should be represented with a return value of
	 * a "failure URL".<br>
	 *
	 * @param input
	 * 		Input string to the parser.
	 * @param base
	 * 		The base URL. (optional)
	 * @param encodingOverride
	 * 		Overrides the encoding of the URL query string. (optional)
	 * @param listener
	 * 		Listener of the parser, listens various events during the execution of the
	 * 		parser.
	 * @param url
	 * 		The given url. (optional)
	 * @param stateOverride
	 * 		Override the start state, 0 for default. (optional)
	 *
	 * @return The parsed URL.
	 */
	public static URL parse(
			String input, URL base, Charset encodingOverride,
			SyntaxViolationListener listener,
			URL url, int stateOverride) {
		int[] codePoints = StringHelper.toCodePoints(input);
		int length;

		if (url == null) {
			// 1.1 is omitted, because we construct the URL object after the whole parsing
			// has terminated and every URL part has been retrieved ( and encoded )

			// 1.2 + 1.3
			int[] trimmedCodePoints = trimC0ControlOrSpace(codePoints);
			if (trimmedCodePoints != codePoints)
				ParserContext.reportSyntaxViolation(listener, input,
						ParserStates.NO_SUCH_STATE, -1,
						"Leading or Trailing C0 control or space found.");
			codePoints = trimmedCodePoints;
		}

		length = codePoints.length;

		// 2
		boolean tabOrNewLineFound = false;
		for (int i = 0; i < length; ++i)
			if (ASCIIHelper.isASCIITabOrNewLine(codePoints[i])) {
				tabOrNewLineFound = true;
				ParserContext.reportSyntaxViolation(listener, input,
						ParserStates.NO_SUCH_STATE, i,
						"Tab or new line found.");
			}

		// 3
		if (tabOrNewLineFound) {
			int j = 0;
			for (int i = 0; i < length; ++i)
				if (!ASCIIHelper.isASCIITabOrNewLine(codePoints[i])) {
					codePoints[j] = codePoints[i];
					++j;
				}
			length = j;
		}

		// 4
		int initialState = ParserStates.hasState(stateOverride) ?
				stateOverride : ParserStates.SCHEME_START_STATE;

		// 5: omitted

		// 6 + 7
		if (url != null && encodingOverride == null)
			encodingOverride = url.getQueryEncodingInternal();
		Charset encoding = encodingOverride == null ?
				EncodingHelper.UTF8 : encodingOverride;

		// 7 + 8 + 9 + 10: omitted, initial values set by means of passing
		// parameters to setters in ParserContext

		// 11

		// construct context
		ParserContext context = new ParserContext();

		context.setState(initialState);
		context.setEncoding(encoding);

		context.setListener(listener);

		context.setInput(input);
		context.setStateOverride(stateOverride);
		context.setCodePoints(codePoints);
		context.setLength(length);
		context.setBase(base);
		context.setPointer(0);
		context.setAtFlag(false);
		context.setBracketsFlag(false);
		context.setPasswordTokenSeenFlag(false);
		context.setBuffer(new StringBuffer());

		if (url != null) {
			context.setScheme(url.getSchemeInternal());
			context.setUsername(url.getUsernameInternal());
			context.setPassword(url.getPasswordInternal());
			context.setHost(url.getHostInternal());
			context.setPort(url.getPortInternal());
			context.path.addAll(url.getPathInternal());
			context.setQuery(url.getQueryInternal());
			context.setFragment(url.getFragmentInternal());
			context.setCannotBeABaseURL(url.getCannotBeABaseURLInternal());
		}

		context.setReturnValue(null);
		context.setTerminateRequested(false);

		// run state machine
		int c = context.codePoints[context.pointer];
		while (true) {
			context.setC(c);
			int stateCode = context.state;
			IParserState state = ParserStates.getState(stateCode);
			try {
				state.execute(context);
			} catch (Throwable t) {
				System.err.printf("Exception caught while execution state %d. \n",
						stateCode);
				t.printStackTrace();

				return URL.failure;
			}

			// early terminations
			URL returnValue = context.returnValue;
			if (returnValue != null) return returnValue;
			if (context.terminateRequested) break;

			if (context.pointer == context.length) break;
			context.setPointer(context.pointer + 1);
			c = context.pointer == context.length ? 0 :
					context.codePoints[context.pointer]; // use 0 to represent EOF
		}

		// 12

		// we construct the URL instance here when all the URL parts are ready.
		if (url != null) {
			url.setInternal(context.scheme, context.username, context.password,
					context.host, context.port, context.path,
					context.query, context.fragment,
					context.cannotBeABaseURL, null, encodingOverride);
			return url;
		} else {
			return URL.createInternal(context.scheme, context.username, context.password,
					context.host, context.port, context.path,
					context.query, context.fragment,
					context.cannotBeABaseURL, null, encodingOverride);
		}
	}

	@SuppressWarnings("StatementWithEmptyBody")
	private static int[] trimC0ControlOrSpace(int[] codePoints) {
		int len = codePoints.length;

		int head = 0, tail = len - 1;
		for (; head < len && ASCIIHelper.isC0ControlOrSpace(codePoints[head]); ++head) ;
		for (; tail >= 0 && ASCIIHelper.isC0ControlOrSpace(codePoints[tail]); --tail) ;

		if (tail < head)
			return new int[0];
		if (head == 0 && tail == len - 1) return codePoints;
		int newLen = tail + 1 - head;
		int[] newCodePoints = new int[newLen];
		System.arraycopy(codePoints, head, newCodePoints, 0, newLen);
		return newCodePoints;
	}
}
