package org.stdurl.host;

/**
 * @see <a href="https://url.spec.whatwg.org/#concept-domain">#concept-domain</a>
 */
public class Domain extends Host {
	public static final Domain localhost = new Domain("localhost");

	private String domain;

	public Domain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return this.domain;
	}

	@Override
	public String serialize() {
		return this.domain;
	}

	@Override
	public boolean equals(Object obj) {
		return !(obj == null || !(obj instanceof Domain)) &&
				this.domain.equalsIgnoreCase(((Domain) obj).domain);
	}
}
