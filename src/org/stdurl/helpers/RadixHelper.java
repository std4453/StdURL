package org.stdurl.helpers;

/**
 *
 */
public class RadixHelper {
	/**
	 * Convert {@code n % 16} to its corresponding upper case hex digit.
	 *
	 * @param n
	 * 		The number to convert.
	 *
	 * @return The converted hex digit.
	 */
	public static int toHexChar(int n) {
		return "0123456789ABCDEF".charAt(n & 0xF);
	}

	/**
	 * Convert hex digit {@code n} to its corresponding hex value.
	 *
	 * @param c
	 * 		The hex digit to convert.
	 *
	 * @return The converted value.
	 */
	public static int fromHexChar(int c) {
		return Math.max("0123456789ABCDEF".indexOf(c), "0123456789abcdef".indexOf(c));
	}

	/**
	 * @param c
	 * 		The character to check.
	 * @param n
	 * 		The radix used. (2 <= n <= 16)
	 *
	 * @return Whether character {@code c} is a valid digit of radix {@code n}.
	 */
	public static boolean isRadixNDigit(int c, int n) {
		int index = fromHexChar(c); // small hack, works the same
		return n >= 2 && n <= 16 && index != -1 && index < n;
	}
}
