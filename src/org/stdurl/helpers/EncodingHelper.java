package org.stdurl.helpers;

import java.nio.charset.Charset;

/**
 *
 */
public class EncodingHelper {
	/**
	 * Exception-free method to get a {@link Charset} for a given name.
	 */
	private static Charset forName(String name) {
		try {
			return Charset.forName(name);
		} catch (Throwable t) {
			return null;
		}
	}

	// ========== STANDARD CHARSETS ==========

	public static final Charset US_ASCII = forName("US-ASCII");
	public static final Charset ISO_8859_1 = forName("ISO-8859-1");
	public static final Charset UTF8 = forName("UTF-8");
	public static final Charset UTF16BE = forName("UTF-16BE");
	public static final Charset UTF16LE = forName("UTF-16LE");
	public static final Charset UTF16 = forName("UTF-16");

	// ========== ADDITIONAL CHARSETS ==========

	public static final Charset GB2312 = forName("GB2312");
	public static final Charset UTF32 = forName("UTF-32");

	/**
	 * Decode the given byte array to a {@link String} with the given {@link Charset}.
	 *
	 * @param bytes
	 * 		The given byte array to decode.
	 * @param encoding
	 * 		The optional {@link Charset} to use, default to {@link #UTF8}.
	 *
	 * @return The decoded {@link String}.
	 */
	public static String decode(byte[] bytes, Charset encoding) {
		encoding = encoding == null ? UTF8 : encoding;
		assert encoding != null; // because UTF-8 is always supported
		return new String(bytes, encoding);
	}
}
