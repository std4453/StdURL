package org.stdurl.origin;

import org.stdurl.host.Domain;
import org.stdurl.host.Host;
import org.stdurl.idna.IDNA;

import java.util.Objects;

/**
 * Object representing the concept "origin" defined in the HTML Standard.
 *
 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#concept-origin">#concept-origin</a>
 */
public class Origin {
	/**
	 * Placeholder Origin "opaque". An Origin is an "opaque origin" if and only if it is
	 * the same object as {@code Origin.opaqueOrigin}.<br>
	 * Use {@code Origin.opaqueOrigin== url} or invoke {@link #isOpaqueOrigin()} to
	 * detect whether a given {@code origin} is an "opaque origin". Refer to
	 * {@code Origin.opaqueOrigin} to get an "opaque origin".
	 *
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#concept-origin-opaque">#concept-origin-opaque</a>
	 */
	public static final Origin opaqueOrigin = new Origin();

	/**
	 * @return Whether this Origin is an "opaque origin".
	 * @see #opaqueOrigin
	 */
	public boolean isOpaqueOrigin() {
		return this == opaqueOrigin;
	}

	private String scheme = "";
	private Host host = null;
	private int port = -1;
	private Domain domain = null;

	/**
	 * Internal constructor, uses default value for all the fields, called only to
	 * create {@link #opaqueOrigin}.
	 */
	private Origin() {
	}

	/**
	 * Standard constructor of {@link Origin}, constructs a <i>tuple origin</i> as
	 * defined in the HTML Standard.
	 *
	 * @param scheme
	 * 		The scheme of the origin.
	 * @param host
	 * 		The host of the origin.
	 * @param port
	 * 		The port of the origin.
	 * @param domain
	 * 		The domain of the origin.
	 *
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#concept-origin-tuple">#conceppt-origin-tuple</a>
	 */
	public Origin(String scheme, Host host, int port, Domain domain) {
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		this.domain = domain;
	}

	/**
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#unicode-serialisation-of-an-origin">#unicode-serialisation-of-an-origin</a>
	 */
	public String unicodeSerialize() {
		if (this.isOpaqueOrigin()) return "null";

		Host unicodeHost = this.host == null || !this.host.isDomain() ? this.host :
				new Domain(IDNA.domainToUnicode(this.host.toDomain().getDomain()));
		Origin unicodeOrigin = new Origin(this.scheme, unicodeHost, this.port, null);
		return unicodeOrigin.asciiSerialize();
	}

	/**
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#ascii-serialisation-of-an-origin">#ascii-serialisation-of-an-origin</a>
	 */
	public String asciiSerialize() {
		if (this.isOpaqueOrigin()) return "null";
		StringBuilder sb = new StringBuilder(this.scheme)
				.append("://")
				.append(this.host.serialize());
		if (this.port != -1)
			sb.append(':').append(this.port);
		return sb.toString();
	}

	/**
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#same-origin">#same-origin</a>
	 */
	public static boolean areSameOrigin(Origin origin1, Origin origin2) {
		return origin1 == origin2 || !(origin1 == null || origin2 == null) &&
				!(origin1.isOpaqueOrigin() || origin2.isOpaqueOrigin()) &&
				Objects.equals(origin1.scheme, origin2.scheme) &&
				Objects.equals(origin1.host, origin2.host) &&
				origin1.port == origin2.port;
	}

	/**
	 * @see <a href="https://html.spec.whatwg.org/multipage/browsers.html#same-origin-domain">#same-origin-domain</a>
	 */
	public static boolean areSameOriginDomain(Origin origin1, Origin origin2) {
		// please don't get scared at this giant boolean calculation
		// it is merely a direct translation of the description on the standard, after
		// which the IDE interfered in and inlined all the assertions :-)
		return origin1 == origin2 || !(origin1 == null || origin2 == null) &&
				!(origin1.isOpaqueOrigin() || origin2.isOpaqueOrigin()) &&
				(Objects.equals(origin1.scheme, origin2.scheme) &&
						(origin1.domain != null && origin2.domain != null) &&
						Objects.equals(origin1.domain, origin2.domain) ||
						areSameOrigin(origin1, origin2) &&
								origin1.domain == null && origin2.domain == null);
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof Origin &&
				areSameOrigin(this, (Origin) obj);
	}

	@Override
	public String toString() {
		return this.asciiSerialize();
	}
}
