package org.stdurl.encoding;

import org.stdurl.helpers.ASCIIHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#simple-encode-set">#simple-encode-set</a>
 */
public class SimpleEncodeSet implements IEncodeSet {
	public static final SimpleEncodeSet instance = new SimpleEncodeSet();

	@Override
	public boolean isInEncodeSet(int codePoint) {
		return ASCIIHelper.isC0Control(codePoint) || codePoint > '\u007E';
	}
}
