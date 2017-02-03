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
	public HostType getType() {
		return this.isDomain() ? HostType.DOMAIN :
				this.isIpv4Address() ? HostType.IPV4 :
						this.isIpv6Address() ? HostType.IPV6 :
								this.isOpaqueHost() ? HostType.OPAQUE_HOST :
										HostType.UNKNOWN;
	}

	/**
	 * The respective implementation of #concept-host-serializer in the URL Standard.
	 *
	 * @return The serialized host string.
	 * @see <a href="https://url.spec.whatwg.org/#concept-host-serializer">#concept-host-serializer</a>
	 */
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

	/**
	 * Converts this {@link Host} instance to a string. Note that the return value of
	 * {@code toString()} may be different from the return value of invoking {@link
	 * #serialize()}. It also contains the actual type of this instance.<br>
	 * Thus, {@code toString()} should be invoked only to obtain a description
	 * {@link String} for human-readable display.
	 *
	 * @return The converted {@link String}.
	 */
	@Override
	public String toString() {
		return this.getType().name + ": " + this.serialize();
	}
}
