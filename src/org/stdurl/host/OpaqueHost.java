package org.stdurl.host;

/**
 * @see <a href="https://url.spec.whatwg.org/#opaque-host">#opaque-host</a>
 */
public class OpaqueHost extends Host {
	/**
	 * Although {@link String} is used, this field is due to store only ASCII characters.
	 */
	private String string;

	/**
	 * Internal constructor, constructs an {@link OpaqueHost} without checking whether
	 * the string contains only ASCII characters.
	 *
	 * @param string
	 * 		The stored string.
	 */
	public OpaqueHost(String string) {
		this.string = string;
	}

	@Override
	public String serialize() {
		return this.string;
	}
}
