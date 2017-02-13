package org.stdurl.helpers;

/**
 *
 */
public class StringHelper {
	/**
	 * Converts the data of the {@link String} to array of code points. This is
	 * probably different from the return value of a direct invocation of the
	 * {@link String#toCharArray()} method, which returns actually an array of code
	 * units instead of code points.
	 *
	 * @param str
	 * 		The string to convert.
	 *
	 * @return The converted string as code points.
	 */
	public static int[] toCodePoints(String str) {
		int codePointCount = str.codePointCount(0, str.length());
		int[] codePoints = new int[codePointCount];

		for (int i = 0; i < codePointCount; ++i) {
			int index = str.offsetByCodePoints(0, i);
			codePoints[i] = str.codePointAt(index);
		}

		return codePoints;
	}

	/**
	 * Converts a given sequence of code points to {@link String}.
	 *
	 * @param codePoints
	 * 		The sequence of code points to convert.
	 *
	 * @return The converted {@link String}.
	 */
	public static String toString(int... codePoints) {
		StringBuilder sb = new StringBuilder();
		for (int codePoint : codePoints)
			sb.appendCodePoint(codePoint);
		return sb.toString();
	}
}
