package org.stdurl.urlencoded;

import org.stdurl.encoding.PercentEncoder;
import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.EncodingHelper;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://url.spec.whatwg.org/#urlencoded-serializing">#urlencoded-serializing</a>
 */
public class URLEncodedSerializer {
	/**
	 * @param tuples
	 * 		The tuples to serialize.
	 * @param encodingOverride
	 * 		Override the default (UTF-8) encoding. (optional)
	 *
	 * @return The serialized {@link String}.
	 * @see <a href="https://url.spec.whatwg.org/#concept-urlencoded-serializer">#concept-urlencoded-serializer</a>
	 */
	public static String serialize(
			List<Map.Entry<String, String>> tuples,
			Charset encodingOverride) {
		Charset encoding = encodingOverride == null ?
				EncodingHelper.UTF8 : encodingOverride;
		StringBuilder output = new StringBuilder();

		boolean firstPair = true;
		for (Map.Entry<String, String> tuple : tuples) {
			String name = serializeByte(tuple.getKey().getBytes(encoding));
			String value = serializeByte(tuple.getValue().getBytes(encoding));
			// special tuples are omitted, since tuples here don't have 'type' attribute.

			if (!firstPair) output.append('&');
			firstPair = false;
			output.append(name).append('=').append(value);
		}

		return output.toString();
	}

	/**
	 * @param input
	 * 		The bytes to serialize.
	 *
	 * @return The serialized {@link String}.
	 * @see <a href="https://url.spec.whatwg.org/#concept-urlencoded-byte-serializer">#concept-urlencoded-byte-serializer</a>
	 */
	public static String serializeByte(byte[] input) {
		StringBuilder output = new StringBuilder();
		for (byte b : input) {
			if (b == 0x20) output.appendCodePoint(0x2B);
			else if ("\u002A\u002D\u002E\u005F".indexOf(b) != -1 ||
					ASCIIHelper.isASCIIAlphanumeric(b))
				output.appendCodePoint(b);
			else PercentEncoder.encode(b, output);
		}
		return output.toString();
	}

	/**
	 * Serialize the given {@code tuples} with no {@code encodingOverride}.
	 *
	 * @param tuples
	 * 		The tuples to serialize.
	 *
	 * @return The serialized {@link String}.
	 */
	public static String serialize(List<Map.Entry<String, String>> tuples) {
		return serialize(tuples, null);
	}
}
