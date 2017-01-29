package org.stdurl.encoding;

/**
 * Interface for all encode sets defined by the URL Standard.
 */
public interface IEncodeSet {
	/**
	 * @param codePoint
	 * 		The code point to check.
	 *
	 * @return Whether the given code point is in the encode set.
	 */
	boolean isInEncodeSet(int codePoint);
}
