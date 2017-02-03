package org.stdurl.host;

import java.util.Arrays;

/**
 * @see <a href="https://url.spec.whatwg.org/#concept-ipv6">#concept-ipv6</a>
 */
public class Ipv6Address extends Host {
	private char[] pieces;

	public Ipv6Address(char[] pieces) {
		this.pieces = Arrays.copyOf(pieces, pieces.length);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#concept-ipv6-serializer">#concept-ipv6-serializer</a>
	 */
	@Override
	public String serialize() {
		StringBuilder output = new StringBuilder();

		// don't want to implement the fast algorithm here, since there are only 8 pieces
		int longest = this.pieces.length, first = -1;
		longestLoop:
		for (; longest > 0; --longest) {
			for (int j = 0; j <= 8 - longest; ++j) {
				boolean found = true;
				for (int k = 0; k < longest; ++k)
					if (this.pieces[j + k] != 0) {
						found = false;
						break;
					}
				if (found) {
					first = j;
					break longestLoop;
				}
			}
		}
		int compressPointer = longest > 1 ? first : -1;

		for (int i = 0; i < this.pieces.length; ++i) {
			if (compressPointer == i) {
				output.append(i == 0 ? "::" : ":");
				//noinspection StatementWithEmptyBody
				for (++i; i < this.pieces.length && this.pieces[i] == 0; ++i) ;
			}
			if (i == this.pieces.length) break;

			char piece = this.pieces[i];
			int intPiece = ((int) piece) & 0xFFFF;
			output.append(Integer.toHexString(intPiece));
			if (i != this.pieces.length - 1) output.append(':');
		}

		return output.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Ipv6Address)) return false;
		char[] p1 = this.pieces, p2 = ((Ipv6Address) obj).pieces;
		if (p1 == p2) return true;
		if (p1 == null || p2 == null) return false;
		if (p1.length != p2.length) return false;
		for (int i = 0; i < p1.length; ++i) if (p1[i] != p2[i]) return false;
		return true;
	}
}
