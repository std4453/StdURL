package org.stdurl.host;

/**
 * @see <a href="https://url.spec.whatwg.org/#concept-ipv4">#concept-ipv4</a>
 */
public class Ipv4Address extends Host {
	private int address;

	public Ipv4Address(int address) {
		this.address = address;
	}

	@Override
	public String serialize() {
		StringBuilder output = new StringBuilder();
		int n = this.address;
		output.insert(0, String.valueOf(n & 0xFF)).insert(0, '.');
		n >>= 8;
		output.insert(0, String.valueOf(n & 0xFF)).insert(0, '.');
		n >>= 8;
		output.insert(0, String.valueOf(n & 0xFF)).insert(0, '.');
		n >>= 8;
		output.insert(0, String.valueOf(n & 0xFF));
		return output.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return !(obj == null || !(obj instanceof Ipv4Address)) &&
				this.address == ((Ipv4Address) obj).address;
	}
}
