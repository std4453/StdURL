package org.stdurl.host;

/**
 *
 */
public class HostHelper {
	/**
	 * Implements the #concept-host-serializer in the URL Standard.
	 *
	 * @param host
	 * 		The input host.
	 *
	 * @return The serialized host string.
	 * @see <a href="https://url.spec.whatwg.org/#concept-host-serializer">#concept-host-serializer</a>
	 */
	public static String serialize(Host host) {
		// For the actual serializing code, see each Host's subclass' serialize() method
		return host == null ? "null" : host.serialize();
	}

	/**
	 * The forbidden host code points, in a {@link String}.<br>
	 * \n = U+000A, \r = U+000D
	 *
	 * @see <a href="https://url.spec.whatwg.org/#forbidden-host-code-point">#forbidden-host-code-point</a>
	 */
	private static final String forbiddenHostCodePoints = "\u0000\u0009\n\r #%/:?@[\\]";

	/**
	 * @see <a href="https://url.spec.whatwg.org/#forbidden-host-code-point">#forbidden-host-code-point</a>
	 */
	public static boolean containsForbiddenHostCodePoint(String hostStr) {
		int cps = hostStr.codePointCount(0, hostStr.length());
		for (int i = 0; i < cps; ++i) {
			int index = hostStr.offsetByCodePoints(0, i);
			int codePoint = hostStr.codePointAt(index);
			if (forbiddenHostCodePoints.indexOf(codePoint) != -1) return true;
		}
		return false;
	}
}
