package org.stdurl.idna.unfinished;

/**
 *
 */
public class IDNAMapper {
	public static final int VALUE_VALID = 1;
	public static final int VALUE_IGNORED = 2;
	public static final int VALUE_MAPPED = 3;
	public static final int VALUE_DEVIATION = 4;
	public static final int VALUE_DISALLOWED = 5;
	public static final int VALUE_DISALLOWED_STD3_VALID = 6;
	public static final int VALUE_DISALLOWED_STD3_MAPPED = 7;

	public static int getType(int codePoint) {
		// TODO: implement this method

		return 0;
	}

	public static String map(int codePoint) {
		// TODO: implement this method

		return "";
	}
}
