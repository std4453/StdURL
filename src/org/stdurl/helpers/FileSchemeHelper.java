package org.stdurl.helpers;

/**
 *
 */
public class FileSchemeHelper {
	/**
	 * @see <a href="https://url.spec.whatwg.org/#windows-drive-letter">#windows-drive-letter</a>
	 */
	public static boolean isWindowsDriveLetter(String codePoints) {
		return codePoints != null && codePoints.length() == 2
				&& ASCIIHelper.isASCIIAlpha(codePoints.codePointAt(0)) &&
				":|".indexOf(codePoints.codePointAt(1)) != -1;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#normalized-windows-drive-letter">#normalized-windows-drive-letter</a>
	 */
	public static boolean isNormalizedWindowsDriveLetter(String codePoints) {
		// equal to definition
		return codePoints != null && codePoints.length() == 2 &&
				ASCIIHelper.isASCIIAlpha(codePoints.codePointAt(0)) &&
				codePoints.codePointAt(1) == ':';
	}
}
