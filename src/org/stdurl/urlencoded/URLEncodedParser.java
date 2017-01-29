package org.stdurl.urlencoded;

import org.stdurl.encoding.PercentDecoder;
import org.stdurl.helpers.EncodingHelper;

import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://url.spec.whatwg.org/#urlencoded-parsing">#urlencoded-parsing</a>
 */
public class URLEncodedParser {
	/**
	 * @param input
	 * 		The input byte array.
	 * @param encoding
	 * 		The optional {@link Charset} to use, default to	 {@link EncodingHelper#UTF8}.
	 *
	 * @return The parsed tuples.
	 * @see <a href="https://url.spec.whatwg.org/#concept-urlencoded-parser">#concept-urlencoded-parser</a>
	 */
	public static List<Map.Entry<String, String>> parse(byte[] input, Charset encoding) {
		List<byte[]> sequences = split(input);
		List<Map.Entry<byte[], byte[]>> tuples = new ArrayList<>();
		for (byte[] bytes : sequences) {
			if (bytes.length == 0) continue;

			// search for '='
			int length = bytes.length;
			int i = 0;
			//noinspection StatementWithEmptyBody
			for (; i < length && bytes[i] != '='; ++i) ;

			byte[] name, value;
			if (i == length) {
				name = bytes;
				value = new byte[0];
			} else {
				name = new byte[i];
				value = new byte[length - i - 1];
				System.arraycopy(bytes, 0, name, 0, i);
				System.arraycopy(bytes, i + 1, value, 0, length - i - 1);
			}

			// replace '+' for 0x20
			for (i = 0; i < name.length; ++i)
				if (name[i] == '+') name[i] = 0x20;
			for (i = 0; i < value.length; ++i)
				if (value[i] == '+') value[i] = 0x20;

			tuples.add(new AbstractMap.SimpleEntry<>(name, value));
		}

		List<Map.Entry<String, String>> output = new ArrayList<>();
		for (Map.Entry<byte[], byte[]> entry : tuples) {
			byte[] decodedName = PercentDecoder.decode(entry.getKey());
			byte[] decodedValue = PercentDecoder.decode(entry.getValue());
			output.add(new AbstractMap.SimpleEntry<>(
					EncodingHelper.decode(decodedName, encoding),
					EncodingHelper.decode(decodedValue, encoding)));
		}

		return output;
	}

	private static List<byte[]> split(byte[] input) {
		List<byte[]> sequences = new ArrayList<>();

		int start = 0, end = 0, length = input.length;
		while (end < length) {
			if (input[end] == '&') {
				byte[] bytes = new byte[end - start];
				System.arraycopy(input, start, bytes, 0, bytes.length);
				sequences.add(bytes);
				start = end + 1;
			}
			++end;
		}

		// last sequence
		byte[] bytes = new byte[end - start];
		System.arraycopy(input, start, bytes, 0, bytes.length);
		sequences.add(bytes);

		return sequences;
	}
}
