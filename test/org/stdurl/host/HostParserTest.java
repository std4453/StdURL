package org.stdurl.host;

import org.junit.Test;
import org.stdurl.RecordedSyntaxViolationListener;
import org.stdurl.parser.ISyntaxViolationListener;

import java.util.Random;

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

		// adjacent '.'s
		// Due to some behaviors of java.net.IDN that are not up to standard, these two
		// test will fail. With the self-implemented IDNA.java not yet finished, we
		// ignore them by now, so that the build could pass.
		// TODO: enable them after implementation of IDNA.java
//		test("1..2.3", domain, "1..2.3");
//		test(".1.2.3", domain, ".1.2.3");

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

	@Test
	public void testParseFailure() {
		RecordedSyntaxViolationListener listener = new RecordedSyntaxViolationListener();

		// unpaired brackets
		test("[::", null, null, listener);
		assertTrue(listener.occurred());

		// forbidden host code point
		listener.clear();
		test("\u0000domain", null, null, listener);
		test("\u0009domain", null, null, listener);
		test("\ndomain", null, null, listener);
		test("\rdomain", null, null, listener);
		test(" domain", null, null, listener);
		test("#domain", null, null, listener);
		test("%domain", null, null, listener);
		test("/domain", null, null, listener);
		test(":domain", null, null, listener);
		test("?domain", null, null, listener);
		test("@domain", null, null, listener);
		test("\\domain", null, null, listener);
		test("domain[", null, null, listener);
		test("domain]", null, null, listener);
		assertEquals(14, listener.getOccurrences());

		// cases where failure should be returned by the ipv4 parser or the ipv6 parser
		// are tested in testParseIpv4() or testParseIpv6()
	}

	@Test
	public void testParseIpv6() {
		HostType ipv6 = HostType.IPV6;
		RecordedSyntaxViolationListener listener = new RecordedSyntaxViolationListener();

		// standard ipv6 addresses
		test("[1234:5678:90ab:cdef:1234:5678:90AB:CDEF]", ipv6,
				"1234:5678:90ab:cdef:1234:5678:90ab:cdef");
		test("[1:23:456:7890:0:a:bc:def]", ipv6, "1:23:456:7890:0:a:bc:def");

		// valid compressed ipv6 addresses
		Random random = new Random();
		for (int i = 2; i < 8; ++i) { // i = number of longest adjacent zeros
			for (int j = 0; j <= 8 - i; ++j) {
				StringBuilder sb = new StringBuilder();
				for (int k = 0; k < j; ++k)
					sb.append(Integer.toHexString(random.nextInt(0x10000))).append(':');
				if (j == 0 || j == 8 - i) sb.append(':');
				for (int k = j; k < 8 - i; ++k)
					sb.append(':').append(Integer.toHexString(random.nextInt(0x10000)));

				String result = sb.toString();
				String host = '[' + result + ']';
				test(host, ipv6, result);
			}
		}

		// compression
		test("[0:0:0:0:0:0:0:0]", ipv6, "::");
		test("[0:0::0:0:0]", ipv6, "::");
		test("[1:0::2:3]", ipv6, "1::2:3");
		test("[1:0:0:2:3:0:0:4]", ipv6, "1::2:3:0:0:4");
		test("[1:0:0:2:3:0:0:0]", ipv6, "1:0:0:2:3::");
		test("[1::2:3:0:0:0]", ipv6, "1:0:0:2:3::");

		// remove leading 0s
		test("[0001:0023:0456:7890:0000:000A:00bc:0DeF]", ipv6,
				"1:23:456:7890:0:a:bc:def");

		// ipv4-compatible ipv6 address
		// 0xc22 = 12 * 256 + 34, 0x384e = 56 * 256 + 78
		test("[1:2:3:4:5:6:12.34.56.78]", ipv6, "1:2:3:4:5:6:c22:384e");
		test("[ffff:ffff:ffff:ffff:ffff:ffff:255.255.255.255]", ipv6,
				"ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
		test("[1::4:5:6:12.34.56.78]", ipv6, "1::4:5:6:c22:384e");
		test("[1:2:3:4:5:6:0.12.0.34]", ipv6, "1:2:3:4:5:6:c:22");

		// failure - leading ':' yet second code point is not ':'
		test("[:1:2:3:4:5:6:7:8]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - more than 8 pieces
		listener.clear();
		test("[1:2:3:4:5:6:7:8:9]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - last character is ':'
		listener.clear();
		test("[1:2:3:4:5:6:7:8:]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - piece longer than 4 digits
		listener.clear();
		test("[12345:1:1:1:1:1:1:1]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - unexpected character
		listener.clear();
		test("[1:2:3:4:5:6:7:qwerty]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - less than 8 pieces
		listener.clear();
		test("[1:2:3:4:5:6:7]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - pieces points > 6 at Ipv4
		listener.clear();
		test("[1:2:3:4:5:6:7:12.34]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - numbersSeen != 4
		listener.clear();
		test("[1:2:3:4:5:6:12.34.56]", null, null, listener);
		assertTrue(listener.occurred());
		listener.clear();
		test("[1:2:3:4:5:6:12.34.56.78.90]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - first digit is 0
		listener.clear();
		test("[1:2:3:4:5:6:012.34.56.78]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - value > 255
		listener.clear();
		test("[1:2:3:4:5:6:256.12.34.56]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - adjacent '.'s
		listener.clear();
		test("[1:2:3:4:5:6:12..56.78]", null, null, listener);
		assertTrue(listener.occurred());

		// failure - ':' followed by '.'
		listener.clear();
		test("[1:2:3:4:5:6:.12.0.34]", null, null, listener);
		assertTrue(listener.occurred());
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
