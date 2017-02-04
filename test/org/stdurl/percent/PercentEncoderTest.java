package org.stdurl.percent;

import org.junit.Test;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PercentEncoderTest {
	/**
	 * Method: {@link org.stdurl.percent.PercentEncoder#encode(byte,
	 * java.lang.StringBuilder)}, {@link org.stdurl.percent.PercentEncoder#encode(byte)}
	 */
	@Test
	public void testEncodeByte() {
		for (int i = 0; i < 256; ++i)
			assertEquals(String.format("%%%02x", i).toUpperCase(),
					PercentEncoder.encode((byte) i));
		// PercentEncoder#encode(byte) uses a faster algorithm, since String.format() is
		// guaranteed to be correct however relatively slow
	}

	/**
	 * Method: {@link PercentEncoder#encode(int[], java.nio.charset.Charset,
	 * org.stdurl.percent.IEncodeSet)}, {@link PercentEncoder#utf8Encode(int[],
	 * org.stdurl.percent.IEncodeSet)}
	 *
	 * Methods {@link PercentEncoder#encode(int, java.nio.charset.Charset,
	 * org.stdurl.percent.IEncodeSet, java.lang.StringBuilder)},
	 * {@link PercentEncoder#encode(int, java.nio.charset.Charset,
	 * org.stdurl.percent.IEncodeSet)}, {@link PercentEncoder#utf8Encode(int,
	 * org.stdurl.percent.IEncodeSet)} are tested within these methods.
	 */
	@Test
	public void testEncodeCodePoints() {
		// simple encode set + standard ascii (+ utf8)
		test("0123456789abcedfghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", null,
				SimpleEncodeSet.instance,
				"0123456789abcedfghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

		// default encode set + standard ascii (+ utf8)
		test("qWeRtY1234567890%` \"#<>?{}", null, DefaultEncodeSet.instance,
				"qWeRtY1234567890%%60%20%22%23%3C%3E%3F%7B%7D");
		// userinfo encode set + standard ascii (+utf8)
		test("qWeRtY1234567890%` \"#<>?{}/:;=@[]\\^|", null,
				UserinfoEncodeSet.instance,
				"qWeRtY1234567890%" +
						"%60%20%22%23%3C%3E%3F%7B%7D%2F%3A%3B%3D%40%5B%5D%5C%5E%7C");

		// simple encode set + unicode + utf8
		test("\u65b0\u5e74\u5feb\u4e50", null, SimpleEncodeSet.instance,
				"%E6%96%B0%E5%B9%B4%E5%BF%AB%E4%B9%90");
		// simple encode set + unicode + gb2312 (if available)
		if (EncodingHelper.GB2312 != null) {
			test("\u65b0\u5e74\u5feb\u4e50", EncodingHelper.GB2312,
					SimpleEncodeSet.instance, "%D0%C2%C4%EA%BF%EC%C0%D6");
		}
	}

	private static void test(
			String str, Charset encoding, IEncodeSet encodeSet,
			String result) {
		String encoded;

		if (encoding != null)
			encoded = PercentEncoder.encode(
					StringHelper.toCodePoints(str), encoding, encodeSet);
		else encoded = PercentEncoder.utf8Encode(
				StringHelper.toCodePoints(str), encodeSet);

		assertEquals(encoded, result);
	}
}
