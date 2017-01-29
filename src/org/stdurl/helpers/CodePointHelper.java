package org.stdurl.helpers;

public class CodePointHelper {
	// kind of small hack
	private static final int[] urlCodePointRanges = new int[]{
			0x00A0, 0xD7FF, 0xE000, 0xFDCF, 0xFDF0, 0xFFFD, 0x10000, 0x1FFFD,
			0x20000, 0x2FFFD, 0x30000, 0x3FFFD, 0x40000, 0x4FFFD, 0x50000, 0x5FFFD,
			0x60000, 0x6FFFD, 0x70000, 0x7FFFD, 0x80000, 0x8FFFD, 0x90000, 0x9FFFD,
			0xA0000, 0xAFFFD, 0xB0000, 0xBFFFD, 0xC0000, 0xCFFFD, 0xD0000, 0xDFFFD,
			0xE0000, 0xEFFFD, 0xF0000, 0xFFFFD, 0x100000, 0x10FFFD,
	};

	/**
	 * @see <a href="https://url.spec.whatwg.org/#url-code-points">#url-code-points</a>
	 */
	public static boolean isURLCodePoint(int codePoint) {
		if (ASCIIHelper.isASCIIAlphanumeric(codePoint) ||
				"!$&'()*+,-./:;=?@_~".indexOf(codePoint) != -1) return true;
		for (int i = 0; i < urlCodePointRanges.length; i += 2) {
			if (codePoint < urlCodePointRanges[i]) return false;
			if (codePoint >= urlCodePointRanges[i] &&
					codePoint <= urlCodePointRanges[i + 1])
				return true;
		}

		return false;
	}
}
