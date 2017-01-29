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
			// FIXME: the compression part maybe bugful, standard being unclear
			if (compressPointer == i) {
				output.append("::");
				//noinspection StatementWithEmptyBody
				for (++i; this.pieces[i] == 0; ++i) ;
			}
			char piece = this.pieces[i];
			int intPiece = ((int) piece) & 0xFFFF;
			output.append(Integer.toHexString(intPiece));
			if (i != this.pieces.length - 1) output.append(':');
		}

		return output.toString();
	}
}
