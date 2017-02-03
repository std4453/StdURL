package org.stdurl.percent;

import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;

import java.nio.charset.Charset;

public class PercentEncoder {
	private static final char[] hexString = new char[]{
			'0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E', 'F'
	};

	/**
	 * Percent-encode a byte to a given {@link StringBuilder}.
	 *
	 * @param b
	 * 		The byte to encode
	 * @param sb
	 * 		The {@link StringBuilder} to store encoded code points.
	 */
	public static void encode(byte b, StringBuilder sb) {
		sb.append('%').append(hexString[(b >> 4) & 0xF]).append(hexString[b & 0xF]);
	}

	/**
	 * Percent-encode a byte into a percent-encoded byte.
	 *
	 * @param b
	 * 		The byte to encode.
	 *
	 * @return The encoded {@link String}.
	 * @see <a href="https://url.spec.whatwg.org/#percent-encode">#percent-encode</a>
	 */
	public static String encode(byte b) {
		StringBuilder sb = new StringBuilder();
		encode(b, sb);
		return sb.toString();
	}

	private static void encode(
			int codePoint, Charset charset, IEncodeSet encodeSet,
			StringBuilder sb) {
		boolean inEncodeSet = encodeSet.isInEncodeSet(codePoint);
		if (!inEncodeSet) {
			sb.appendCodePoint(codePoint);
			return;
		}

		byte[] bytes = StringHelper.toString(codePoint).getBytes(charset);
		for (byte aByte : bytes) encode(aByte, sb);
	}


	/**
	 * Encode a single code point to a {@link String} using a encoding charset and an
	 * encode set.
	 *
	 * @param codePoint
	 * 		The code point to encode
	 * @param charset
	 * 		The charset used to encode.
	 * @param encodeSet
	 * 		The {@link IEncodeSet} to use.
	 *
	 * @return The encoded code points.
	 * @see <a href="https://url.spec.whatwg.org/#utf-8-percent-encode">#utf-8-percent-encode</a>
	 */
	private static String encode(int codePoint, Charset charset, IEncodeSet encodeSet) {
		StringBuilder sb = new StringBuilder();
		encode(codePoint, charset, encodeSet, sb);
		return sb.toString();
	}

	/**
	 * Encode a single code point to a {@link String} using the UTf-8 charset and an
	 * given encode set.
	 *
	 * @param codePoint
	 * 		The code point to encode.
	 * @param encodeSet
	 * 		The {@link IEncodeSet} used to encode.
	 *
	 * @return The encoded code points.
	 * @see <a href="https://url.spec.whatwg.org/#utf-8-percent-encode">#utf-8-percent-encode</a>
	 */
	public static String utf8Encode(int codePoint, IEncodeSet encodeSet) {
		return encode(codePoint, EncodingHelper.UTF8, encodeSet);
	}

	/**
	 * Encode a sequence of code points to {@link String} using a encoding charset and an
	 * encode set.
	 *
	 * @param codePoints
	 * 		The sequence of code points to encode
	 * @param charset
	 * 		The charset used to encode.
	 * @param encodeSet
	 * 		The {@link IEncodeSet} to use.
	 *
	 * @return The encoded code points.
	 * @see <a href="https://url.spec.whatwg.org/#utf-8-percent-encode">#utf-8-percent-encode</a>
	 */
	public static String encode(int[] codePoints, Charset charset, IEncodeSet encodeSet) {
		StringBuilder sb = new StringBuilder();
		for (int codePoint : codePoints) encode(codePoint, charset, encodeSet, sb);
		return sb.toString();
	}

	/**
	 * Encode a sequence of code points to {@link String} using UTF-8 charset and an
	 * given encode set.
	 *
	 * @param codePoints
	 * 		The sequence of code points to encode.
	 * @param encodeSet
	 * 		The {@link IEncodeSet} to use.
	 *
	 * @return The encoded code points.
	 * @see <a href="https://url.spec.whatwg.org/#utf-8-percent-encode">#utf-8-percent-encode</a>
	 */
	public static String utf8Encode(int[] codePoints, IEncodeSet encodeSet) {
		return encode(codePoints, EncodingHelper.UTF8, encodeSet);
	}
}
