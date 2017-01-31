package org.stdurl.origin;

import org.stdurl.URL;
import org.stdurl.helpers.SchemeHelper;
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
			case SchemeHelper.SCHEME_BLOB:
				URL parsedURL = BasicURLParser.parse(url.getPathInternal().get(0));
				return parsedURL == null || parsedURL.isFailure() ?
						Origin.opaqueOrigin : parsedURL.getOriginObject();
			case SchemeHelper.SCHEME_FTP:
			case SchemeHelper.SCHEME_GOPHER:
			case SchemeHelper.SCHEME_HTTP:
			case SchemeHelper.SCHEME_HTTPS:
			case SchemeHelper.SCHEME_WS:
			case SchemeHelper.SCHEME_WSS:
				return new Origin(url.getSchemeInternal(),
						url.getHostInternal(),
						url.getPortInternal(),
						null);
			case SchemeHelper.SCHEME_FILE:
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
