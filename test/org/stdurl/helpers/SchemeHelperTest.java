package org.stdurl.helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class SchemeHelperTest {
	/**
	 * Method: {@link SchemeHelper#isSpecialScheme(String)}
	 */
	@Test
	public void testIsSpecialScheme() {
		assertTrue(SchemeHelper.isSpecialScheme("fTp"));
		assertTrue(SchemeHelper.isSpecialScheme("Ftp"));
		assertTrue(SchemeHelper.isSpecialScheme("FIlE"));
		assertTrue(SchemeHelper.isSpecialScheme("fiLE"));
		assertTrue(SchemeHelper.isSpecialScheme("gOPhEr"));
		assertTrue(SchemeHelper.isSpecialScheme("GOpheR"));
		assertTrue(SchemeHelper.isSpecialScheme("HTtp"));
		assertTrue(SchemeHelper.isSpecialScheme("hTTP"));
		assertTrue(SchemeHelper.isSpecialScheme("HtTPs"));
		assertTrue(SchemeHelper.isSpecialScheme("htTpS"));
		assertTrue(SchemeHelper.isSpecialScheme("wS"));
		assertTrue(SchemeHelper.isSpecialScheme("Ws"));
		assertTrue(SchemeHelper.isSpecialScheme("WsS"));
		assertTrue(SchemeHelper.isSpecialScheme("wSS"));

		assertFalse(SchemeHelper.isSpecialScheme("about"));
		assertFalse(SchemeHelper.isSpecialScheme("ABOUT"));
		assertFalse(SchemeHelper.isSpecialScheme("blob"));
		assertFalse(SchemeHelper.isSpecialScheme("BLOB"));
		assertFalse(SchemeHelper.isSpecialScheme("data"));
		assertFalse(SchemeHelper.isSpecialScheme("DATA"));
		assertFalse(SchemeHelper.isSpecialScheme("filesystem"));
		assertFalse(SchemeHelper.isSpecialScheme("FILESYSTEM"));

		assertFalse(SchemeHelper.isSpecialScheme("fake"));
	}

	/**
	 * Method: {@link SchemeHelper#getDefaultPort(String)}
	 */
	@Test
	public void testGetDefaultPort() {
		assertEquals(21, SchemeHelper.getDefaultPort("fTp"));
		assertEquals(21, SchemeHelper.getDefaultPort("Ftp"));
		assertEquals(-1, SchemeHelper.getDefaultPort("FIlE"));
		assertEquals(-1, SchemeHelper.getDefaultPort("fiLE"));
		assertEquals(70, SchemeHelper.getDefaultPort("gOPhEr"));
		assertEquals(70, SchemeHelper.getDefaultPort("GOpheR"));
		assertEquals(80, SchemeHelper.getDefaultPort("HTtp"));
		assertEquals(80, SchemeHelper.getDefaultPort("hTTP"));
		assertEquals(443, SchemeHelper.getDefaultPort("HtTPs"));
		assertEquals(443, SchemeHelper.getDefaultPort("htTpS"));
		assertEquals(80, SchemeHelper.getDefaultPort("wS"));
		assertEquals(80, SchemeHelper.getDefaultPort("Ws"));
		assertEquals(443, SchemeHelper.getDefaultPort("WsS"));
		assertEquals(443, SchemeHelper.getDefaultPort("wSS"));

		assertEquals(-1, SchemeHelper.getDefaultPort("about"));
		assertEquals(-1, SchemeHelper.getDefaultPort("ABOUT"));
		assertEquals(-1, SchemeHelper.getDefaultPort("blob"));
		assertEquals(-1, SchemeHelper.getDefaultPort("BLOB"));
		assertEquals(-1, SchemeHelper.getDefaultPort("data"));
		assertEquals(-1, SchemeHelper.getDefaultPort("DATA"));
		assertEquals(-1, SchemeHelper.getDefaultPort("filesystem"));
		assertEquals(-1, SchemeHelper.getDefaultPort("FILESYSTEM"));

		assertEquals(-1, SchemeHelper.getDefaultPort("fake"));
	}
}
