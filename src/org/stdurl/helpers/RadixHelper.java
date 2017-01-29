package org.stdurl.helpers;

/**
 *
 */
public class RadixHelper {
	/**
	 * Convert {@code n % 16} to its corresponding hex digit.
	 *
	 * @param n
	 * 		The number to convert.
	 *
	 * @return The converted hex digit.
	 */
	public static int toHex(int n) {
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
	public static int fromHex(int c) {
		return Math.max("0123456789ABCDEF".indexOf(c), "0123456789abcdef".indexOf(c));
	}

	public static boolean isRadixNDigit(int c, int n) {
		int index = fromHex(c); // small hack, works the same
		return index != -1 && index < n;
	}
}
