package org.stdurl.host;

/**
 *
 */
public enum HostType {
	UNKNOWN("unknown"),
	DOMAIN("domain"),
	IPV4("ipv4"),
	IPV6("ipv6"),
	OPAQUE_HOST("opaque host");

	public String name;

	HostType(String name) {
		this.name = name;
	}
}
