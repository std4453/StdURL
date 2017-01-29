package org.stdurl.encoding;

/**
 * @see <a href="https://url.spec.whatwg.org/#default-encode-set">#default-encode-set</a>
 */
public class DefaultEncodeSet implements IEncodeSet {
	public static final DefaultEncodeSet instance = new DefaultEncodeSet();

	@Override
	public boolean isInEncodeSet(int codePoint) {
		return SimpleEncodeSet.instance.isInEncodeSet(codePoint) ||
				"` \"#<>?{}".indexOf(codePoint) != -1;
	}
}
