package org.stdurl.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SchemeHelper {
	public static final String SCHEME_ABOUT = "about";
	public static final String SCHEME_BLOB = "blob";
	public static final String SCHEME_DATA = "data";
	public static final String SCHEME_FILESYSTEM = "filesystem";
	public static final String SCHEME_FTP = "ftp";
	public static final String SCHEME_FILE = "file";
	public static final String SCHEME_GOPHER = "gopher";
	public static final String SCHEME_HTTP = "http";
	public static final String SCHEME_HTTPS = "https";
	public static final String SCHEME_WS = "ws";
	public static final String SCHEME_WSS = "wss";

	private static final Map<String, Integer> specialSchemes = new HashMap<>();
	private static final List<String> localSchemes = Arrays.asList(
			SCHEME_ABOUT,
			SCHEME_BLOB,
			SCHEME_DATA,
			SCHEME_FILESYSTEM);

	static {
		specialSchemes.put(SCHEME_FTP, 21);
		specialSchemes.put(SCHEME_FILE, -1);
		specialSchemes.put(SCHEME_GOPHER, 70);
		specialSchemes.put(SCHEME_HTTP, 80);
		specialSchemes.put(SCHEME_HTTPS, 443);
		specialSchemes.put(SCHEME_WS, 80);
		specialSchemes.put(SCHEME_WSS, 443);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#special-scheme">#special-scheme</a>
	 */
	public static boolean isSpecialScheme(String scheme) {
		return specialSchemes.containsKey(scheme.toLowerCase());
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#default-port">#default-port</a>
	 */
	public static int getDefaultPort(String scheme) {
		return specialSchemes.getOrDefault(scheme.toLowerCase(), -1);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#local-scheme">#local-scheme</a>
	 */
	public static boolean isLocalScheme(String scheme) {
		return localSchemes.contains(scheme.toLowerCase());
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#http-scheme">#http-scheme</a>
	 */
	public static boolean isHTTPScheme(String scheme) {
		return SCHEME_HTTP.equalsIgnoreCase(scheme) ||
				SCHEME_HTTPS.equalsIgnoreCase(scheme);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#network-scheme">#network-scheme</a>
	 */
	public static boolean isNetworkScheme(String scheme) {
		return isHTTPScheme(scheme) || SCHEME_FTP.equalsIgnoreCase(scheme);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#fetch-scheme">#fetch-scheme</a>
	 */
	public static boolean isFetchScheme(String scheme) {
		return isLocalScheme(scheme) ||
				isNetworkScheme(scheme) ||
				SCHEME_FILE.equalsIgnoreCase(scheme);
	}

	public static boolean isSpecialSchemeNotFile(String scheme) {
		return isSpecialScheme(scheme) && !SCHEME_FILE.equalsIgnoreCase(scheme);
	}
}
