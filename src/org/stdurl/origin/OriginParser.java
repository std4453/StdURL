package org.stdurl.origin;

import org.stdurl.URL;
import org.stdurl.parser.BasicURLParser;

/**
 * Implements the parsing of origin as defined in URL Standard.
 *
 * @see <a href="https://url.spec.whatwg.org/#origin">#origin</a>
 */
public class OriginParser {
	/**
	 * Parser the given url for a {@link Origin}.
	 *
	 * @param url
	 * 		The url to parse.
	 *
	 * @return The parsed {@link Origin}.
	 */
	public static Origin parse(URL url) {
		String scheme = url.getSchemeInternal();

		switch (scheme) {
			case "blob":
				URL parsedURL = BasicURLParser.parse(url.getPathInternal().get(0));
				return parsedURL == null || parsedURL.isFailure() ?
						Origin.opaqueOrigin : parsedURL.getOriginObject();
			case "ftp":
			case "gopher":
			case "http":
			case "https":
			case "ws":
			case "wss":
				return new Origin(url.getSchemeInternal(),
						url.getHostInternal(),
						url.getPortInternal(),
						null);
			case "file":
				// It is pretty frustrating to see written in the standard:
				//   Unfortunate as it is, this is left as an exercise to the reader.
				//   When in doubt, return a new opaque origin.
				// So, therefore, we return the opaque origin here. Sorry.
				return Origin.opaqueOrigin;
			default:
				return Origin.opaqueOrigin;
		}
	}
}
