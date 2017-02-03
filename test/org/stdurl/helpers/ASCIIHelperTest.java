package org.stdurl.helpers;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ASCIIHelperTest {
	/**
	 * Method: {@link ASCIIHelper#isASCIICodePoint(int)}
	 */
	@Test
	public void testIsASCIICodePoint() throws Exception {
		for (int i = 0; i <= 127; ++i)
			assertTrue(ASCIIHelper.isASCIICodePoint(i));
		for (int i = 128; i <= 512; ++i)
			assertFalse(ASCIIHelper.isASCIICodePoint(i));
	}

	/**
	 * Method: {@link ASCIIHelper#isC0Control(int)}
	 */
	@Test
	public void testIsC0Control() throws Exception {
		for (int i = 0; i <= 31; ++i)
			assertTrue(ASCIIHelper.isC0Control(i));
		for (int i = 32; i <= 128; ++i)
			assertFalse(ASCIIHelper.isC0Control(i));
	}
}
