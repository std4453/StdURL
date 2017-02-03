package org.stdurl.host;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class HostSerializerTest {
	/**
	 * Since the respective implementations of {@link Host#serialize()} are not
	 * complex, use only one test method.
	 */
	@Test
	public void testSerialization() {
		// Domain
		assertEquals("www.example.com", new Domain("www.example.com").serialize());

		// OpaqueHost
		assertEquals("www.example.com", new OpaqueHost("www.example.com").serialize());

		// serialization of ipv6 and ipv4 addresses are more tested in HostParserTest,
		// thus excluded here.
	}
}
