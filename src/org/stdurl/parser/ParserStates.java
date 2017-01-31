package org.stdurl.parser;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ParserStates {
	private static final Map<Integer, IParserState> states = new HashMap<>();

	private static void register(int stateCode, IParserState state) {
		states.put(stateCode, state);
	}

	public static final int NO_SUCH_STATE = 0;
	/**
	 * @see SchemeStartState
	 */
	public static final int SCHEME_START_STATE = 1;
	/**
	 * @see SchemeState
	 */
	public static final int SCHEME_STATE = 2;
	/**
	 * @see NoSchemeState
	 */
	public static final int NO_SCHEME_STATE = 3;
	/**
	 * @see SpecialRelativeOrAuthorityState
	 */
	public static final int SPECIAL_RELATIVE_OR_AUTHORITY_STATE = 4;
	/**
	 * @see PathOrAuthorityState
	 */
	public static final int PATH_OR_AUTHORITY_STATE = 5;
	/**
	 * @see RelativeState
	 */
	public static final int RELATIVE_STATE = 6;
	/**
	 * @see RelativeSlashState
	 */
	public static final int RELATIVE_SLASH_STATE = 7;
	/**
	 * @see SpecialAuthoritySlashesState
	 */
	public static final int SPECIAL_AUTHORITY_SLASHES_STATE = 8;
	/**
	 * @see SpecialAuthorityIgnoreSlashesState
	 */
	public static final int SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE = 9;
	/**
	 * @see AuthorityState
	 */
	public static final int AUTHORITY_STATE = 10;
	/**
	 * @see HostState
	 */
	public static final int HOST_STATE = 11;
	/**
	 * @see HostnameState
	 */
	public static final int HOSTNAME_STATE = 12;
	/**
	 * @see PortState
	 */
	public static final int PORT_STATE = 13;
	/**
	 * @see FileState
	 */
	public static final int FILE_STATE = 14;
	/**
	 * @see FileSlashState
	 */
	public static final int FILE_SLASH_STATE = 15;
	/**
	 * @see FileHostState
	 */
	public static final int FILE_HOST_STATE = 16;
	/**
	 * @see PathStartState
	 */
	public static final int PATH_START_STATE = 17;
	/**
	 * @see PathState
	 */
	public static final int PATH_STATE = 18;
	/**
	 * @see CannotBeABaseURLPathState
	 */
	public static final int CANNOT_BE_A_BASE_URL_PATH_STATE = 19;
	/**
	 * @see QueryState
	 */
	public static final int QUERY_STATE = 20;
	/**
	 * @see FragmentState
	 */
	public static final int FRAGMENT_STATE = 21;

	static {
		register(SCHEME_START_STATE, new SchemeStartState());
		register(SCHEME_STATE, new SchemeState());
		register(NO_SCHEME_STATE, new NoSchemeState());
		register(SPECIAL_RELATIVE_OR_AUTHORITY_STATE,
				new SpecialRelativeOrAuthorityState());
		register(PATH_OR_AUTHORITY_STATE, new PathOrAuthorityState());
		register(RELATIVE_STATE, new RelativeState());
		register(RELATIVE_SLASH_STATE, new RelativeSlashState());
		register(SPECIAL_AUTHORITY_SLASHES_STATE,
				new SpecialAuthoritySlashesState());
		register(SPECIAL_AUTHORITY_IGNORE_SLASHES_STATE,
				new SpecialAuthorityIgnoreSlashesState());
		register(AUTHORITY_STATE, new AuthorityState());
		register(HOST_STATE, new HostState());
		register(HOSTNAME_STATE, new HostnameState());
		register(PORT_STATE, new PortState());
		register(FILE_STATE, new FileState());
		register(FILE_SLASH_STATE, new FileSlashState());
		register(FILE_HOST_STATE, new FileHostState());
		register(PATH_START_STATE, new PathStartState());
		register(PATH_STATE, new PathState());
		register(CANNOT_BE_A_BASE_URL_PATH_STATE, new CannotBeABaseURLPathState());
		register(QUERY_STATE, new QueryState());
		register(FRAGMENT_STATE, new FragmentState());
	}

	public static IParserState getState(int stateCode) {
		return states.get(stateCode);
	}

	public static boolean hasState(int stateCode) {
		return states.containsKey(stateCode);
	}
}
