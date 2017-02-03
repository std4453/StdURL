package org.stdurl.helpers;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 */
public class StringHelperTest {
	/**
	 * Test for pure ASCII characters.
	 *
	 * Method: {@link StringHelper#toCodePoints(String)}
	 */
	@Test
	public void testToCodePoints_pureASCII() {
		StringBuilder sb = new StringBuilder();
		int[] codePoints = new int[128 - 20];
		for (int i = 20; i < 128; ++i) {
			codePoints[i - 20] = i;
			sb.append((char) i);
		}
		assertArrayEquals(codePoints, StringHelper.toCodePoints(sb.toString()));
	}

	/**
	 * Test for Unicode characters on BMP. (Basic Multilingual Plane)
	 *
	 * Method: {@link StringHelper#toCodePoints(String)}
	 */
	@Test
	public void testToCodePoints_BMP() {
		StringBuilder sb = new StringBuilder();
		List<Integer> cps = new ArrayList<>();

		Random random = new Random();
		for (int i = 0; i < 1024; ++i) {
			int n = random.nextInt(0x10000); // BMP
			// filter away high / low surrogates (which will result in an error)
			if (Character.isHighSurrogate((char) n) ||
					Character.isLowSurrogate((char) n))
				continue;
			add(sb, cps, n);
		}

		// assertion
		int[] codePoints = new int[cps.size()];
		for (int i = 0, size = cps.size(); i < size; ++i)
			codePoints[i] = cps.get(i);

		assertArrayEquals(codePoints, StringHelper.toCodePoints(sb.toString()));
	}

	private static void add(StringBuilder sb, List<Integer> cps, int codePoint) {
		sb.appendCodePoint(codePoint);
		cps.add(codePoint);
	}
}
