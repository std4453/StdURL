package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.ASCIIHelper;
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
	 * Simplest URL parser, parse the given {@code input} without any special arguments.
	 *
	 * @param input
	 * 		The input string.
	 *
	 * @return The parsed {@link URL} object;
	 */
	public static URL parse(String input, SyntaxViolationListener listener) {
		return parse(input, null, null, listener, null, ParserStates.NO_SUCH_STATE);
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

		if (url == null) { // 1
			// 1.1: omitted, see ParserStateMachine

			// 1.2 + 1.3
			int[] trimmedCodePoints = trimC0ControlOrSpace(codePoints);
			if (trimmedCodePoints != codePoints)
				ParserContext.reportSyntaxViolation(listener, input,
						ParserStates.NO_SUCH_STATE, -1,
						"Leading or Trailing C0 control or space found.");
			codePoints = trimmedCodePoints;
		}

		// 2 + 3
		int[] removedCodePoints = removeTabOrNewLine(codePoints);
		if (removedCodePoints != codePoints)
			ParserStateMachine.reportSyntaxViolation(listener, input,
					ParserStates.NO_SUCH_STATE, -1,
					"U+0009, U+000A or U+000D unexpected.");
		codePoints = removedCodePoints;

		int initialState = ParserStates.hasState(stateOverride) ?
				stateOverride : ParserStates.SCHEME_START_STATE; // 4

		// 5: omitted

		if (url != null && encodingOverride == null)
			encodingOverride = url.getQueryEncodingInternal(); // 6 + 7

		// 8 + 9 + 10 + 11 + 12: see ParserStateMachine
		ParserStateMachine machine = new ParserStateMachine(codePoints, base,
				encodingOverride, url, stateOverride, listener);
		machine.run(initialState);
		return machine.returnValue;
	}

	private static int[] trimC0ControlOrSpace(int[] codePoints) {
		int len = codePoints.length;

		int head = 0, tail = len - 1;
		//noinspection StatementWithEmptyBody
		for (; head < len && ASCIIHelper.isC0ControlOrSpace(codePoints[head]); ++head) ;
		//noinspection StatementWithEmptyBody
		for (; tail >= 0 && ASCIIHelper.isC0ControlOrSpace(codePoints[tail]); --tail) ;

		if (tail < head)
			return new int[0];
		if (head == 0 && tail == len - 1) return codePoints;
		int newLen = tail + 1 - head;
		int[] newCodePoints = new int[newLen];
		System.arraycopy(codePoints, head, newCodePoints, 0, newLen);
		return newCodePoints;
	}

	private static int[] removeTabOrNewLine(int[] codePoints) {
		int len = codePoints.length;
		int len1 = 0;
		for (int codePoint1 : codePoints)
			if (!ASCIIHelper.isASCIITabOrNewLine(codePoint1)) ++len1;
		if (len1 == len) return codePoints;

		int[] newCodePoints = new int[len1];
		int j = 0;
		for (int codePoint : codePoints)
			if (!ASCIIHelper.isASCIITabOrNewLine(codePoint))
				newCodePoints[j++] = codePoint;
		return newCodePoints;
	}
}
