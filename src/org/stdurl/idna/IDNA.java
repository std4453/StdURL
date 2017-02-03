package org.stdurl.idna;

import java.net.IDN;

/**
 * Although {@link IDN} doesn't 100% conforms to <i>RFC 3492</i>, it can act as a
 * fallback method. The self-implemented IDNA class hat not yet finished.
 *
 * @see <a href="https://url.spec.whatwg.org/#idna">#idna</a>
 */
public class IDNA {
	/**
	 * A {@code null} return value represents {@code failure} defined in the standard.
	 *
	 * @see <a href="https://url.spec.whatwg.org/#concept-domain-to-ascii">#concept-domain-to-ascii</a>
	 */
	public static String domainToASCII(String domain) {
		try {
			return IDN.toASCII(domain);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * A {@code null} return value represents {@code failure} defined in the standard.
	 *
	 * @see <a href="https://url.spec.whatwg.org/#concept-domain-to-unicode">#concept-domain-to-unicode</a>
	 */
	public static String domainToUnicode(String domain) {
		String unicodeDomain = IDN.toUnicode(domain);

		// Here if toUnicode() results in an error, domain is returned unchanged, so
		// they should be the very same object. Therefore we use == to compare between
		// unicode Domain instead of using String.equals()

		//noinspection StringEquality
		if (unicodeDomain == domain) return null;
		return unicodeDomain;
	}
}
