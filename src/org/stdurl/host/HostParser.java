package org.stdurl.host;

import org.stdurl.encoding.IDNA;
import org.stdurl.encoding.PercentDecoder;
import org.stdurl.encoding.PercentEncoder;
import org.stdurl.encoding.SimpleEncodeSet;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.RadixHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.parser.SyntaxViolationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Implement a standard-defined URL host parser & host parser.
 *
 * @see <a href="https://url.spec.whatwg.org/#concept-url-host-parser">#conecpt-url-host-parser</a>
 * @see <a href="https://url.spec.whatwg.org/#concept-host-parser">#concept-host-parser</a>
 */
public class HostParser {
	/**
	 * SVMT stands for <i>Syntax Violation Message Template</i>.
	 */
	private static final String hostParserSVMT =
			"Syntax violation while parsing Host: \"%s\"\n" +
					"Violation index: %d, Message: %s";

	/**
	 * Implements the #concept-url-host-parser as in the URL Standard.
	 * <br>
	 * The return value would be {@code null} if the parser should, as defined, return
	 * a "failure" host.
	 *
	 * @param input
	 * 		The input host string.
	 * @param isSpecial
	 * 		The isSpecial flag.
	 * @param listener
	 * 		The syntax violation listener of the parser.
	 *
	 * @return The parsed host.
	 */
	public static Host parseURLHost(
			String input, boolean isSpecial, SyntaxViolationListener listener) {
		if (isSpecial)
			return parseHost(input, listener);
		if (HostHelper.containsForbiddenHostCodePoint(input)) {
			listener.onSyntaxViolation(String.format(hostParserSVMT,
					input, -1, "Input string contains forbidden host code point."));
			return null;
		}

		String output = PercentEncoder.utf8Encode(StringHelper.toCodePoints(input),
				SimpleEncodeSet.instance);
		return new OpaqueHost(output);
	}

	/**
	 * Implements the #concept-host-parser as in the URL Standard.
	 *
	 * @param input
	 * 		The input host string.
	 * @param unicodeFlag
	 * 		The unicodeFlag.
	 * @param listener
	 * 		The syntax violation listener of the parser
	 *
	 * @return The parsed host.
	 */
	@SuppressWarnings("SameParameterValue")
	public static Host parseHost(
			String input, boolean unicodeFlag, SyntaxViolationListener listener) {
		int[] codePoints = StringHelper.toCodePoints(input);
		int length = codePoints.length;

		if (codePoints[0] == '[') { // 1
			if (codePoints[length - 1] != ']') {
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, -1, "Brackets unpaired."));
				return null;
			}
			return parseIpv6(input.substring(1, input.length() - 1), listener);
		}

		String domain = EncodingHelper.decode(
				PercentDecoder.decode(
						input.getBytes(EncodingHelper.UTF8)),
				EncodingHelper.UTF8); // 2
		String asciiDomain = IDNA.domainToASCII(domain); // 3
		if (asciiDomain == null) return null; // 4

		if (HostHelper.containsForbiddenHostCodePoint(asciiDomain)) { // 5
			listener.onSyntaxViolation(String.format(hostParserSVMT,
					input, -1, "Input string contains forbidden host code point."));
		}

		Host ipv4Host = parseIpv4(asciiDomain, listener); // 6
		if (ipv4Host == null || ipv4Host.isIpv4Address()) return ipv4Host; // 7
		return new Domain(unicodeFlag ? asciiDomain : IDNA.domainToUnicode(asciiDomain));
	}

	/**
	 * Parse host according to #concept-host-parser with {@code unicodeFlag} unset.
	 *
	 * @param input
	 * 		The input host string.
	 * @param listener
	 * 		The syntax violation listener of the parser.
	 *
	 * @return The parsed host.
	 */
	public static Host parseHost(String input, SyntaxViolationListener listener) {
		return parseHost(input, false, listener);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#concept-ipv6-parser">#concept-ipv6-parser</a>
	 */
	@SuppressWarnings("ConstantConditions")
	private static Ipv6Address parseIpv6(String input, SyntaxViolationListener listener) {
		// 1
		char[] pieces = new char[8];
		Arrays.fill(pieces, (char) 0);

		int piecePointer = 0; // 2
		int compressPointer = -1; // 3

		// 4
		int[] codePoints = StringHelper.toCodePoints(input);
		int length = codePoints.length;
		int pointer = 0;

		if (codePoints[pointer] == ':') { // 5
			if (getRemaining(codePoints, pointer, 0) != ':') {
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, pointer, "Ipv6 address should begin with \"::\"."));
				return null;
			}
			pointer += 2;
			++piecePointer;
			compressPointer = piecePointer;
		}

		boolean jumpToIpv4 = false;

		Main:
		while (pointer != length) { // 6
			int c = codePoints[pointer];

			if (piecePointer == 8) { // 6.1
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, pointer, "Ipv6 address has at most 8 pieces."));
				return null;
			}

			if (c == ':') { // 6.2
				if (compressPointer != -1) {
					listener.onSyntaxViolation(String.format(hostParserSVMT,
							input, pointer, "Compress pointer not null."));
					return null;
				}
				++pointer;
				++piecePointer;
				compressPointer = piecePointer;
				continue;
			}

			int v = 0, l = 0; // 6.3

			while (l < 4 && ASCIIHelper.isASCIIHexDigit(c)) { // 6.4
				v = (v << 4) + RadixHelper.fromHex(c);
				++pointer;
				++l;
				c = pointer == length ? 0 : codePoints[pointer];
			}

			switch (c) { // 6.5
				case '.':
					if (l == 0) {
						listener.onSyntaxViolation(String.format(hostParserSVMT,
								input, pointer, "Length can't be 0."));
						return null;
					}
					pointer -= l;
					jumpToIpv4 = true;
					break Main;
				case ':':
					++pointer;
					if (pointer == length) {
						listener.onSyntaxViolation(String.format(hostParserSVMT,
								input, pointer - 1, "':' can't be the last character."));
						return null;
					}
					break;
				case 0:
					break;
				default:
					listener.onSyntaxViolation(String.format(hostParserSVMT,
							input, pointer,
							new StringBuilder("Character '")
									.appendCodePoint(c)
									.append("' unexpected.")
									.toString()));
					return null;
			}

			pieces[piecePointer] = (char) v; // 6.6
			++piecePointer; // 6.7
		}

		boolean jumpToFinale = false;

		if (!jumpToIpv4 && pointer == length) jumpToFinale = true; // 7

		if (!jumpToFinale) {
			if (piecePointer > 6) // 8
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, pointer, "Unknown error."));

			int numbersSeen = 0; // 9
			while (pointer != length) { // 10
				int c = codePoints[pointer];

				int value = -1;

				if (numbersSeen > 0) { // 10.2
					if (c == '.' && numbersSeen < 4) {
						++pointer;
						c = pointer == length ? 0 : codePoints[pointer];
					} else {
						listener.onSyntaxViolation(String.format(hostParserSVMT,
								input, pointer, "numbersSeen greater than 4."));
						return null;
					}
				}

				if (!ASCIIHelper.isASCIIDigit(c)) { // 10.3
					listener.onSyntaxViolation(String.format(hostParserSVMT,
							input, pointer, "ASCII digit expected."));
					return null;
				}

				while (pointer < length && ASCIIHelper.isASCIIDigit(c)) { // 10.4
					// little hack, a decimal digit is always a hex digit
					int number = RadixHelper.fromHex(c);
					if (value == -1) value = number;
					else if (value == 0) {
						listener.onSyntaxViolation(String.format(hostParserSVMT,
								input, pointer, "First digit should not be 0."));
						return null;
					} else value = value * 10 + number;

					++pointer;
					c = pointer == length ? 0 : codePoints[pointer];

					if (value > 255)
						listener.onSyntaxViolation(String.format(hostParserSVMT,
								input, pointer, "Value " + value + " too big."));
				}

				// 10.5
				pieces[piecePointer] = (char) ((pieces[piecePointer] << 8) + value);
				++numbersSeen; // 10.6
				if (numbersSeen == 2 || numbersSeen == 4) ++piecePointer; // 10.7

				if (pointer == length && numbersSeen != 4) { // 10.8
					listener.onSyntaxViolation(String.format(hostParserSVMT,
							input, pointer, "Pieces not enough."));
					return null;
				}
			}
		}

		if (compressPointer != -1) { // 11
			int swaps = piecePointer - compressPointer;
			piecePointer = 7;
			while (piecePointer != 0 && swaps > 0) {
				char tmp = pieces[compressPointer + swaps - 1];
				pieces[compressPointer + swaps - 1] = pieces[piecePointer];
				pieces[piecePointer] = tmp;
				--piecePointer;
				--swaps;
			}
		} else { // 12
			if (piecePointer != 8)
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, pointer, "Pieces not enough."));
			return null;
		}

		return new Ipv6Address(pieces);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#concept-ipv4-parser">#concept-ipv4-parser</a>
	 */
	private static Host parseIpv4(String input, SyntaxViolationListener listener) {
		boolean syntaxViolationFlag = false; // 1
		String[] parts = input.split(Pattern.quote(".")); // 2

		// 3
		int partsLength = parts.length;
		if (parts[partsLength - 1].isEmpty()) {
			syntaxViolationFlag = true;
			if (partsLength > 1) --partsLength;
		}

		// if the return value of parseIpv4() is not instance of Ipv4Address or null,
		// it is discarded, so here we only want to express, "The input is not an ipv4
		// address, however also not a failure, you should regard it as a domain".
		if (partsLength > 4) return new OpaqueHost(input); // 4

		List<Integer> numbers = new ArrayList<>(); // 5

		for (String part : parts) { // 6
			if (part.isEmpty()) return new OpaqueHost(input);
			Object[] ret = parseIpv4Number(part, syntaxViolationFlag);
			int n = (Integer) ret[0];
			syntaxViolationFlag = (Boolean) ret[1];
			if (n == -1) return new OpaqueHost(input);
			numbers.add(n);
		}

		if (syntaxViolationFlag) // 7
			listener.onSyntaxViolation(String.format(hostParserSVMT,
					input, -1, "Error parsing ipv4 number."));

		// 8 + 9
		int numbersSize = numbers.size();
		for (int i = 0; i < numbersSize; ++i)
			if (numbers.get(i) > 255) {
				listener.onSyntaxViolation(String.format(hostParserSVMT,
						input, -1, "Ipv4 number greater than 255."));
				if (i != numbersSize - 1) return null;
			}

		if (numbers.get(numbersSize - 1) >= (1L << ((5 - numbersSize) << 3))) { // 10
			listener.onSyntaxViolation(String.format(hostParserSVMT,
					input, -1, "Last part of ipv4 address too big."));
			return null;
		}

		int ipv4 = numbers.get(numbersSize - 1); // 11
		numbers.remove(numbersSize - 1); // 12
		int counter = 0; // 13
		for (int n : numbers) {
			ipv4 += n << ((3 - counter) << 3);
			++counter;
		}

		return new Ipv4Address(ipv4);
	}

	/**
	 * Return value -1 represents "failure".
	 *
	 * @see <a href="https://url.spec.whatwg.org/#ipv4-number-parser">#ipv4-number-parser</a>
	 */
	private static Object[] parseIpv4Number(
			String input, boolean syntaxViolationFlag) {
		int R = 10; // 1

		int[] codePoints = StringHelper.toCodePoints(input);
		int length = codePoints.length;

		// 2
		if (length >= 2 && codePoints[0] == '0' && "xX".indexOf(codePoints[1]) != -1) {
			syntaxViolationFlag = true;
			int[] newCodePoints = new int[length - 2];
			System.arraycopy(codePoints, 2, newCodePoints, 0, length - 2);
			codePoints = newCodePoints;
			length -= 2;
			R = 16;
		} else if (length >= 2 && codePoints[0] == '0') { // 3
			syntaxViolationFlag = true;
			int[] newCodePoints = new int[length - 1];
			System.arraycopy(codePoints, 1, newCodePoints, 0, length - 1);
			codePoints = newCodePoints;
			length -= 1;
			R = 8;
		}

		if (length == 0) return new Object[]{0, syntaxViolationFlag};
		for (int codePoint : codePoints)
			if (!RadixHelper.isRadixNDigit(codePoint, R))
				return new Object[]{-1, syntaxViolationFlag};

		input = StringHelper.toString(codePoints);
		try {
			int n = Integer.parseInt(input, R);
			return new Object[]{n, syntaxViolationFlag};
		} catch (NumberFormatException e) {
			return new Object[]{-1, syntaxViolationFlag};
		}
	}

	@SuppressWarnings("SameParameterValue")
	private static int getRemaining(int[] codePoints, int pointer, int index) {
		index += pointer + 1;
		if (index < 0 || index >= codePoints.length) return 0;
		return codePoints[index];
	}
}
