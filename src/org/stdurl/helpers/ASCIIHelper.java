package org.stdurl.helpers;

/**
 *
 */
public class ASCIIHelper {
	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-code-point">#ascii-code-point</a>
	 */
	public static boolean isASCIICodePoint(int codePoint) {
		// equal to definition
		return (codePoint & ~0x7F) == 0;
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-tab-or-newline">#ascii-tab-or-newline</a>
	 */
	public static boolean isASCIITabOrNewLine(int codePoint) {
		// since java doesn't allow escaping of U+000A and U+000D, use number instead
		return codePoint == '\u0009' ||
				codePoint == 0x000A ||
				codePoint == 0x000D;
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-whitespace">#ascii-whitespace</a>
	 */
	public static boolean isASCIIWhiteSpace(int codePoint) {
		return isASCIITabOrNewLine(codePoint) ||
				codePoint == '\u000C' ||
				codePoint == '\u0020';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#c0-control">#c0-control</a>
	 */
	public static boolean isC0Control(int codePoint) {
		// equal to definition
		return (codePoint & ~0x1F) == 0;
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#c0-control-or-space">#c0-control-or-space</a>
	 */
	public static boolean isC0ControlOrSpace(int codePoint) {
		// equal to definition
		return codePoint >= '\u0000' && codePoint <= '\u0020';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-digit">#ascii-digit</a>
	 */
	public static boolean isASCIIDigit(int codePoint) {
		return codePoint >= '\u0030' && codePoint <= '\u0039';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-upper-hex-digit">#ascii-upper-hex-digit</a>
	 */
	public static boolean isASCIIUpperHexDigit(int codePoint) {
		return isASCIIDigit(codePoint) || codePoint >= '\u0041' && codePoint <= '\u0046';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-lower-hex-digit">#ascii-lower-hex-digit</a>
	 */
	public static boolean isASCIILowerHexDigit(int codePoint) {
		return isASCIIDigit(codePoint) || codePoint >= '\u0061' && codePoint <= '\u0066';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-hex-digit">#ascii-hex-digit</a>
	 */
	public static boolean isASCIIHexDigit(int codePoint) {
		return isASCIIUpperHexDigit(codePoint) || isASCIILowerHexDigit(codePoint);
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-upper-alpha">#ascii-upper-alpha</a>
	 */
	public static boolean isASCIIUpperAlpha(int codePoint) {
		return codePoint >= '\u0041' && codePoint <= '\u005A';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-lower-alpha">#ascii-lower-alpha</a>
	 */
	public static boolean isASCIILowerAlpha(int codePoint) {
		return codePoint >= '\u0061' && codePoint <= '\u007A';
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-alpha">#ascii-alpha</a>
	 */
	public static boolean isASCIIAlpha(int codePoint) {
		return isASCIILowerAlpha(codePoint) || isASCIIUpperAlpha(codePoint);
	}

	/**
	 * @see <a href="https://infra.spec.whatwg.org/#ascii-alphanumeric">#ascii-alphanumeric</a>
	 */
	public static boolean isASCIIAlphanumeric(int codePoint) {
		return isASCIIAlpha(codePoint) || isASCIIDigit(codePoint);
	}

	public static int toLowerCase(int codePoint) {
		return isASCIIUpperAlpha(codePoint) ? (codePoint - 0x20) : codePoint;
	}

	public static boolean containsNonASCIICharacter(String str) {
		int[] codePoints = StringHelper.toCodePoints(str);
		for (int codePoint : codePoints) if (!isASCIICodePoint(codePoint)) return true;
		return true;
	}
}
