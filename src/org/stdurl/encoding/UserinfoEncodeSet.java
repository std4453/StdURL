package org.stdurl.encoding;

/**
 * @see <a href="https://url.spec.whatwg.org/#userinfo-encode-set">#userinfo-encode-set</a>
 */
public class UserinfoEncodeSet implements IEncodeSet {
	public static final UserinfoEncodeSet instance = new UserinfoEncodeSet();

	@Override
	public boolean isInEncodeSet(int codePoint) {
		return DefaultEncodeSet.instance.isInEncodeSet(codePoint) ||
				"/:;=@[]\\^|".indexOf(codePoint) != -1;
	}
}
