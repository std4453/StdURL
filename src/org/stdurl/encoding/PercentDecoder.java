package org.stdurl.encoding;

import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.RadixHelper;

import java.io.ByteArrayOutputStream;

/**
 * Implements the percent decoder defined in the URL Standard.
 *
 * @see <a href="https://url.spec.whatwg.org/#percent-decode">#percent-decode</a>
 */
public class PercentDecoder {
	/**
	 * Decode the given input byte array to another byte array.
	 *
	 * @param input
	 * 		The input byte array.
	 *
	 * @return The decoded byte array.
	 * @see <a href=https://url.spec.whatwg.org/#percent-decode">#percent-decode</a>
	 */
	public static byte[] decode(byte[] input) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		for (int i = 0; i < input.length; ++i) {
			byte b = input[i];
			if (b != '%') output.write(b);
			else if (i >= input.length - 2) output.write(b);
			else {
				int d1 = ((int) input[i + 1] & 0xFF), d2 = ((int) input[i + 2] & 0xFF);
				if (!ASCIIHelper.isASCIIHexDigit(d1) || !ASCIIHelper.isASCIIHexDigit(d2))
					output.write(b);
				else {
					int bytePoint = (RadixHelper.fromHex(d1) << 4) |
							RadixHelper.fromHex(d2);
					output.write(bytePoint);
					i += 2;
				}
			}
		}

		return output.toByteArray();
	}
}
