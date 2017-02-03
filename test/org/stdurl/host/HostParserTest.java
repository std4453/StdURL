package org.stdurl.host;

import org.junit.Test;
import org.stdurl.RecordedSyntaxViolationListener;
import org.stdurl.parser.ISyntaxViolationListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class HostParserTest {
	@Test
	public void testParseIpv4() {
		HostType ipv4 = HostType.IPV4;
		RecordedSyntaxViolationListener listener = new RecordedSyntaxViolationListener();

		// standard ipv4
		test("192.168.202.134", ipv4, "192.168.202.134");
		test("1.23.123.231", ipv4, "1.23.123.231");

		// syntax violation - trailing point
		test("13.25.46.209.", ipv4, "13.25.46.209", listener);
		assertTrue(listener.occurred());

		// syntax violation - part > 255 & return failure
		listener.clear();
		test("256.255.255.255", ipv4, null, listener);
		test("255.256.255.255", ipv4, null, listener);
		test("255.255.256.255", ipv4, null, listener);
		test("255.255.255.256", ipv4, null, listener);
		// last part greater than 255 will result in one more syntax violation
		assertEquals(5, listener.getOccurrences());

		// syntax violation - last part > 255 & ignored
		listener.clear();
		int part1 = 192, part2 = 168, part3 = 202, part4 = 134;
		String result = String.valueOf(part1) + '.' + part2 + '.' + part3 + '.' + part4;
		test(part1 + "." + part2 + "." + ((part3 << 8) + part4),
				ipv4, result, listener);
		test(part1 + "." + (((part2 << 8) + part3 << 8) + part4),
				ipv4, result, listener);
		test("" + (((((long) part1 << 8) + part2 << 8) + part3 << 8) + part4),
				ipv4, result, listener);
		assertEquals(3, listener.getOccurrences());

		// syntax violation - last part too big -> failure
		listener.clear();
		test("1.1.65536", ipv4, null, listener);
		test("1.16777216", ipv4, null, listener);
		assertEquals(4, listener.getOccurrences());

		// base 16 / 8
		listener.clear();
		test(String.format("0x%x.0%o.0x%x.0%o",
				part1, part2, part3, part4), ipv4, result, listener);
		test(String.format("0%o.0x%x.0%o.0x%x",
				part1, part2, part3, part4), ipv4, result, listener);
		assertEquals(2, listener.getOccurrences());
	}

	@Test
	public void testParseDomain() {
		HostType domain = HostType.DOMAIN;

		// more than 4 items
		test("12.34.56.78.90", domain, "12.34.56.78.90");
		test("1.2.3.4.5.6", domain, "1.2.3.4.5.6");

		// value greater than 2^32 - 1
		test("4294967296", domain, "4294967296");
		test("0x100000000", domain, "0x100000000");
		test("040000000000", domain, "040000000000");

		// out-ouf-radix digits
		test("123ABC", domain, "123ABC");
		test("0x123Gg", domain, "0x123Gg");
		test("0123456789", domain, "0123456789");

		// standard domains
		test("www.example.com", domain, "www.example.com");
		test("a.b.c.d.example.com", domain, "a.b.c.d.example.com");
	}

	private static void test(String host, HostType type, String result) {
		test(host, type, result, null);
	}

	private static void test(
			String host, HostType type, String result,
			ISyntaxViolationListener listener) {
		Host parsedHost = HostParser.parseHost(host, false, listener);
		String msg = "Input = " + host;
		if (result == null)
			assertEquals(msg, null, parsedHost);
		else {
			assertNotEquals(msg, null, parsedHost);
			assert parsedHost != null; // should always be true
			assertEquals(msg, type, parsedHost.getType());
			assertEquals(msg, result, parsedHost.serialize());
		}
	}
}
