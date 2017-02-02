package org.stdurl.encoding.deprecated;

import org.stdurl.helpers.ASCIIHelper;
import org.stdurl.helpers.StringHelper;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * @see <a href="https://url.spec.whatwg.org/#idna">#idna</a>
 * @deprecated Not yet finished, use {@link org.stdurl.encoding.IDNA} instead.
 */
@Deprecated
@SuppressWarnings("all")
public class IDNA {
	/**
	 * A {@code null} return value represents {@code failure} defined in the standard.
	 *
	 * @see <a href="https://url.spec.whatwg.org/#concept-domain-to-ascii">#concept-domain-to-ascii</a>
	 */
	public static String domainToASCII(String domain) {
		return toASCII(StringHelper.toCodePoints(domain),
				TRANSITIONAL_PROCESSING_FLAG);
	}

	/**
	 * A {@code null} return value represents {@code failure} defined in the standard.
	 *
	 * @see <a href="https://url.spec.whatwg.org/#concept-domain-to-unicode">#concept-domain-to-unicode</a>
	 */
	public static String domainToUnicode(String domain) {
		return toUnicode(StringHelper.toCodePoints(domain), 0);
	}

	public static final int USE_STD3_ASCII_RULES_FLAG = 0x1;
	public static final int TRANSITIONAL_PROCESSING_FLAG = 0x2;
	public static final int VERIFY_DNS_LENGTH_FLAG = 0x4;

	public static final String punycodeLabelPrefix = "xn--";

	/**
	 * Implement the Unicode toASCII operation. Return null to represent "failure".
	 *
	 * @param domainName
	 * 		The domain name to process.
	 * @param flags
	 * 		The bitwise or of any one(s) of allowed flags.
	 *
	 * @return The converted {@link String}.
	 * @see <a href="http://www.unicode.org/reports/tr46/#ToASCII">#ToASCII</a>
	 */
	public static String toASCII(int[] domainName, int flags) {
		String processed = processing(domainName, flags & (~VERIFY_DNS_LENGTH_FLAG));
		if (processed == null) return null;

		String[] labels = processed.split(Pattern.quote("."));
		String[] convertedLabels = new String[labels.length];
		for (int i = 0; i < labels.length; ++i) {
			String label = labels[i];
			if (ASCIIHelper.containsNonASCIICharacter(label))
				convertedLabels[i] = punycodeLabelPrefix + toPunycode(label);
			else convertedLabels[i] = label;
		}
		for (String label : convertedLabels) if (label == null) return null;

		if ((flags & VERIFY_DNS_LENGTH_FLAG) != 0) {
			int totalLength = convertedLabels.length - 1;
			for (String label : convertedLabels) {
				int length = label.length();
				if (length < 1 || length > 63) return null;
				totalLength += length;
			}
			if (totalLength < 1 || totalLength > 253) return null;
		}

		return String.join(".", convertedLabels);
	}

	/**
	 * Implement the Unicode toUnicode operation. Return null to represent "failure".
	 *
	 * @param domainName
	 * 		The domain name to process.
	 * @param flag
	 * 		THe UseSTD3ASCIIRules flag.
	 *
	 * @return The converted {@link String}.
	 * @see <a href="http://www.unicode.org/reports/tr46/#ToUnicode">#ToUnicode</a>
	 */
	public static String toUnicode(int[] domainName, int flag) {
		return processing(domainName, flag);
	}

	public static String fromPunycode(String punycode) {
		// TODO: implement this method
		return null;
	}

	/**
	 * Implement a character sequence into Punycode as defined in RFC3492.
	 *
	 * @param str
	 * 		The string to convert.
	 *
	 * @return The converted string.
	 */
	public static String toPunycode(String str) {
		// TODO: implement this method
		return null;
	}

	/**
	 * Implement the Unicode IDNA Compatibility Processing operation. Return null to
	 * represent "failure".
	 *
	 * @param domainName
	 * 		The domain name to process.
	 * @param flags
	 * 		The bitwise or of any one(s) of allowed flags.
	 *
	 * @return The converted {@link String}.
	 * @see <a href="http://www.unicode.org/reports/tr46/#Processing">#Processing</a>
	 */
	public static String processing(int[] domainName, int flags) {
		// 1. Map
		StringBuilder output = new StringBuilder();
		boolean std3 = (flags & USE_STD3_ASCII_RULES_FLAG) != 0;
		boolean transitional = (flags & TRANSITIONAL_PROCESSING_FLAG) != 0;
		for (int c : domainName) {
			int type = IDNAMapper.getType(c);
			if (type == IDNAMapper.VALUE_DISALLOWED_STD3_MAPPED)
				type = std3 ? IDNAMapper.VALUE_DISALLOWED : IDNAMapper.VALUE_MAPPED;
			else if (type == IDNAMapper.VALUE_DISALLOWED_STD3_VALID)
				type = std3 ? IDNAMapper.VALUE_DISALLOWED : IDNAMapper.VALUE_VALID;

			switch (type) {
				case IDNAMapper.VALUE_VALID:
					output.appendCodePoint(c);
					break;
				case IDNAMapper.VALUE_DISALLOWED:
					return null;
				case IDNAMapper.VALUE_IGNORED:
					break;
				case IDNAMapper.VALUE_MAPPED:
					output.append(IDNAMapper.map(c));
					break;
				case IDNAMapper.VALUE_DEVIATION:
					if (transitional)
						output.append(IDNAMapper.map(c));
					else output.appendCodePoint(c);
					break;
				default:
					return null;
			}
		}

		// 2. Normalize
		String normalized = Normalizer.normalize(output.toString(), Normalizer.Form.NFC);

		// 3. Break
		String[] labels = normalized.split(Pattern.quote("."));

		// 4. Convert / Validate
		for (int i = 0; i < labels.length; ++i) {
			String label = labels[i];
			if (label.startsWith(punycodeLabelPrefix)) {
				String punycode = fromPunycode(
						label.substring(punycodeLabelPrefix.length()));
				if (punycode == null) return null;
				labels[i] = punycode;
				if (!meetsValidityCriteria(labels[i], 0)) return null;
			} else if (!meetsValidityCriteria(label,
					flags & TRANSITIONAL_PROCESSING_FLAG)) return null;
		}

		return String.join(".", labels);
	}

	/**
	 * @see <a href="http://www.unicode.org/reports/tr46/#Validity_Criteria">#Validity_Criteria</a>
	 */
	public static boolean meetsValidityCriteria(String str, int flag) {
		if (!Normalizer.isNormalized(str, Normalizer.Form.NFC)) return false;

		int[] codePoints = StringHelper.toCodePoints(str);
		int length = codePoints.length;

		if (length >= 4)
			if (codePoints[2] == 0x2D && codePoints[3] == 0x2D) return false;

		if (length >= 1)
			if (codePoints[0] == 0x2D || codePoints[length - 1] == 0x2D) return false;

		for (int codePoint : codePoints) {
			if (codePoint == 0x2E) return false;
			int type = IDNAMapper.getType(codePoint);
			if (type == IDNAMapper.VALUE_VALID) continue;

			if ((flag & TRANSITIONAL_PROCESSING_FLAG) == 0 &&
					type == IDNAMapper.VALUE_DEVIATION) continue;
			return false;
		}

		return true;
	}
}
