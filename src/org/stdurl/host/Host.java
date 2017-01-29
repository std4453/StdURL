package org.stdurl.host;

/**
 * Instance of {@link Host} represents a host defined in the URL Standard. The
 * {@link Host} class is the superclass to those more specific types of host, and the
 * class itself contains merely a few methods to detect the actual type of host the
 * instance is of and to convert an instance of {@link Host} to the actual class.
 * <br>
 * Known subclasses of {@link Host} are:
 * <ol>
 * <li>{@link Domain}: <a href="https://url.spec.whatwg.org/#concept-domain">#concept-domain</a></li>
 * <li>{@link Ipv4Address}: <a href="https://url.spec.whatwg.org/#concept-ipv4">#concept-ipv4</a></li>
 * <li>{@link Ipv6Address}: <a href="https://url.spec.whatwg.org/#concept-ipv6">#concept-ipv6</a></li>
 * <li>{@link OpaqueHost}: <a href="https://url.spec.whatwg.org/#opaque-host">#opaque-host</a></li>
 * </ol>
 *
 * @see <a href="https://url.spec.whatwg.org/#hosts-%28domains-and-ip-addresses%29">#hosts-(domains-and-ip-addresses)</a>
 */
public abstract class Host {
	private static final int TYPE_UNKNOWN = 0;
	private static final int TYPE_DOMAIN = 1;
	private static final int TYPE_IPV4_ADDRESS = 2;
	private static final int TYPE_IPV6_ADDRESS = 3;
	private static final int TYPE_OPAQUE_HOST = 4;

	public int getType() {
		return this.isDomain() ? TYPE_DOMAIN :
				this.isIpv4Address() ? TYPE_IPV4_ADDRESS :
						this.isIpv6Address() ? TYPE_IPV6_ADDRESS :
								this.isOpaqueHost() ? TYPE_OPAQUE_HOST :
										TYPE_UNKNOWN;
	}

	public abstract String serialize();

	public boolean isDomain() {
		return this instanceof Domain;
	}

	public boolean isIpv4Address() {
		return this instanceof Ipv4Address;
	}

	public boolean isIpv6Address() {
		return this instanceof Ipv6Address;
	}

	public boolean isOpaqueHost() {
		return this instanceof OpaqueHost;
	}

	public Domain toDomain() {
		return this.isDomain() ? (Domain) this : null;
	}

	public Ipv4Address toIpv4Address() {
		return this.isIpv4Address() ? (Ipv4Address) this : null;
	}

	public Ipv6Address toIpv6Address() {
		return this.isIpv6Address() ? (Ipv6Address) this : null;
	}

	public OpaqueHost toOpaqueHost() {
		return this.isOpaqueHost() ? (OpaqueHost) this : null;
	}
}
