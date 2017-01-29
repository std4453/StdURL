package org.stdurl.helpers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SchemeHelper {
	private static final Map<String, Integer> specialSchemes = new HashMap<>();
	private static final List<String> localSchemes = Arrays.asList(
			"about",
			"blob",
			"data",
			"filesystem");

	static {
		specialSchemes.put("ftp", 21);
		specialSchemes.put("file", -1);
		specialSchemes.put("gopher", 70);
		specialSchemes.put("http", 80);
		specialSchemes.put("https", 443);
		specialSchemes.put("ws", 80);
		specialSchemes.put("wss", 443);
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
		return "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#network-scheme">#network-scheme</a>
	 */
	public static boolean isNetworkScheme(String scheme) {
		return isHTTPScheme(scheme) || "ftp".equalsIgnoreCase(scheme);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#fetch-scheme">#fetch-scheme</a>
	 */
	public static boolean isFetchScheme(String scheme) {
		return isLocalScheme(scheme) ||
				isNetworkScheme(scheme) ||
				FileSchemeHelper.isFileScheme(scheme);
	}

	public static boolean isSpecialSchemeNotFile(String scheme) {
		return isSpecialScheme(scheme) && !"file".equalsIgnoreCase(scheme);
	}
}
