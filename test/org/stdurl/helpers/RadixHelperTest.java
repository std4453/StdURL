package org.stdurl.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class RadixHelperTest {
	/**
	 * Method: {@link RadixHelper#toHexChar(int)}
	 */
	@Test
	public void testToHexChar() {
		assertEquals('0', RadixHelper.toHexChar(0));
		assertEquals('1', RadixHelper.toHexChar(1));
		assertEquals('2', RadixHelper.toHexChar(2));
		assertEquals('3', RadixHelper.toHexChar(3));
		assertEquals('4', RadixHelper.toHexChar(4));
		assertEquals('5', RadixHelper.toHexChar(5));
		assertEquals('6', RadixHelper.toHexChar(6));
		assertEquals('7', RadixHelper.toHexChar(7));
		assertEquals('8', RadixHelper.toHexChar(8));
		assertEquals('9', RadixHelper.toHexChar(9));
		assertEquals('A', RadixHelper.toHexChar(10));
		assertEquals('B', RadixHelper.toHexChar(11));
		assertEquals('C', RadixHelper.toHexChar(12));
		assertEquals('D', RadixHelper.toHexChar(13));
		assertEquals('E', RadixHelper.toHexChar(14));
		assertEquals('F', RadixHelper.toHexChar(15));
	}

	/**
	 * Method: {@link RadixHelper#fromHexChar(int)}
	 */
	@Test
	public void testFromHexChar() {
		assertEquals(0, RadixHelper.fromHexChar('0'));
		assertEquals(1, RadixHelper.fromHexChar('1'));
		assertEquals(2, RadixHelper.fromHexChar('2'));
		assertEquals(3, RadixHelper.fromHexChar('3'));
		assertEquals(4, RadixHelper.fromHexChar('4'));
		assertEquals(5, RadixHelper.fromHexChar('5'));
		assertEquals(6, RadixHelper.fromHexChar('6'));
		assertEquals(7, RadixHelper.fromHexChar('7'));
		assertEquals(8, RadixHelper.fromHexChar('8'));
		assertEquals(9, RadixHelper.fromHexChar('9'));

		assertEquals(10, RadixHelper.fromHexChar('a'));
		assertEquals(11, RadixHelper.fromHexChar('b'));
		assertEquals(12, RadixHelper.fromHexChar('c'));
		assertEquals(13, RadixHelper.fromHexChar('d'));
		assertEquals(14, RadixHelper.fromHexChar('e'));
		assertEquals(15, RadixHelper.fromHexChar('f'));

		assertEquals(10, RadixHelper.fromHexChar('A'));
		assertEquals(11, RadixHelper.fromHexChar('B'));
		assertEquals(12, RadixHelper.fromHexChar('C'));
		assertEquals(13, RadixHelper.fromHexChar('D'));
		assertEquals(14, RadixHelper.fromHexChar('E'));
		assertEquals(15, RadixHelper.fromHexChar('F'));

		assertEquals(-1, RadixHelper.fromHexChar('G'));
		assertEquals(-1, RadixHelper.fromHexChar('g'));

		assertEquals(-1, RadixHelper.fromHexChar(' '));
		assertEquals(-1, RadixHelper.fromHexChar('+'));
		assertEquals(-1, RadixHelper.fromHexChar('-'));
		assertEquals(-1, RadixHelper.fromHexChar('*'));
		assertEquals(-1, RadixHelper.fromHexChar('/'));
	}

	/**
	 * Method: {@link RadixHelper#isRadixNDigit(int, int)}
	 */
	@Test
	public void testIsRadixDigit() {
		for (int i = 2; i <= 16; ++i) {
			for (int j = 0; j < i; ++j)
				assertTrue(RadixHelper.isRadixNDigit("0123456789abcdef".charAt(j), i));
			for (int j = 10; j < i; ++j)
				assertTrue(RadixHelper.isRadixNDigit("0123456789ABCDEF".charAt(j), i));
			assertFalse(RadixHelper.isRadixNDigit(' ', i));
			assertFalse(RadixHelper.isRadixNDigit("0123456789abcdefg".charAt(i), i));
			assertFalse(RadixHelper.isRadixNDigit("0123456789ABCDEFG".charAt(i), i));
		}
		assertFalse(RadixHelper.isRadixNDigit('0', 0));
		assertFalse(RadixHelper.isRadixNDigit('0', 1));
		assertFalse(RadixHelper.isRadixNDigit('0', 17));
	}
}
