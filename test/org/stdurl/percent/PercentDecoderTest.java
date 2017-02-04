package org.stdurl.percent;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 */
public class PercentDecoderTest {
	@Test
	public void testDecode() {
		// no '%'
		byte[] alphanumerics = b(
				"0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
		test(alphanumerics, alphanumerics);

		// all '%'
		StringBuilder sb = new StringBuilder();
		byte[] result = new byte[256];
		for (int i = 0; i < 256; ++i) {
			sb.append(String.format("%%%02x", i));
			result[i] = (byte) i;
		}
		test(result, b(sb.toString()));

		// '%' not followed by 2 hex digits
		test(b("a%1"), b("a%1"));
		test(b("a%1a"), b("a%1%61"));
		test(b("a%a"), b("a%%61"));
		test(b("a%1g"), b("a%1g"));
	}

	private static void test(byte[] result, byte[] target) {
		assertArrayEquals(result, PercentDecoder.decode(target));
	}

	private static byte[] b(String str) {
		return str.getBytes();
	}

	private static byte[] b(byte... bytes) {
		return bytes;
	}
}
