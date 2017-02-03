package org.stdurl.helpers;

import java.util.List;

/**
 *
 */
public class PathHelper {
	/**
	 * @see <a href="https://url.spec.whatwg.org/#syntax-url-path-segment-dotdot">#syntax-url-path-segment-dotdot</a>
	 */
	public static boolean isDoubleDotPathSegment(String segment) {
		return "..".equals(segment) ||
				".%2e".equalsIgnoreCase(segment) ||
				"%2e.".equalsIgnoreCase(segment) ||
				"%2e%2e".equalsIgnoreCase(segment);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#syntax-url-path-segment-dot">#syntax-url-path-segment-dot</a>
	 */
	public static boolean isSingleDotPathSegment(String segment) {
		return ".".equals(segment) || "%2e".equalsIgnoreCase(segment);
	}

	/**
	 * Shorten a URL's path according to its scheme in-place.
	 *
	 * @see <a href="https://url.spec.whatwg.org/#shorten-a-urls-path">#shorten-a-urls-path</a>
	 */
	public static void shortenPath(List<String> path, String scheme) {
		if (path.size() == 0) return;
		if (SchemeHelper.SCHEME_FILE.equalsIgnoreCase(scheme) && path.size() == 1 &&
				FileSchemeHelper.isWindowsDriveLetter(path.get(0))) return;
		path.remove(path.size() - 1);
	}
}
