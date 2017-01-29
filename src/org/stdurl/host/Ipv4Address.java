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
		output.append(n & 0xFF).append('.');
		n >>= 8;
		output.append(n & 0xFF).append('.');
		n >>= 8;
		output.append(n & 0xFF).append('.');
		n >>= 8;
		output.append(n & 0xFF);
		return output.toString();
	}
}
