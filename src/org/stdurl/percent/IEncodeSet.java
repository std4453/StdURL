package org.stdurl.percent;

import java.nio.charset.Charset;

/**
 * Interface for all encode sets defined by the URL Standard. Used in {@linkplain
 * PercentEncoder#encode(int[], Charset, IEncodeSet) PercentEncoder.encode()}.
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
