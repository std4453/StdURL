package org.stdurl;

import org.stdurl.encoding.PercentEncoder;
import org.stdurl.encoding.UserinfoEncodeSet;
import org.stdurl.helpers.PathHelper;
import org.stdurl.helpers.StringHelper;
import org.stdurl.host.Host;
import org.stdurl.host.HostHelper;
import org.stdurl.origin.Origin;
import org.stdurl.origin.OriginParser;
import org.stdurl.parser.BasicURLParser;
import org.stdurl.parser.ParserStates;

import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @see <a href="https://url.spec.whatwg.org/#url">#url</a>
 */
public class URL {
	// ========== FAILURE URL ==========

	/**
	 * Placeholder URL "failure". An URL is a "failure URL" if and only if it is the
	 * same object as {@code URL.failure}.<br>
	 * Use {@code URL.failure == url} or invoke {@link #isFailure()} to detect whether
	 * a given {@code url} is a "failure URL". Refer to {@code URL.failure} to get a
	 * "failure URL".
	 */
	public static final URL failure = new URL();

	/**
	 * @return Whether this URL is a "failure URL".
	 * @see #failure
	 */
	public boolean isFailure() {
		return this == failure;
	}

	// ========== INTERNAL URL CONSTRUCTORS & FACTORY METHODS ==========

	private URL(
			String scheme, String username, String password,
			Host host, int port,
			List<String> path, String query, String fragment,
			boolean cannotBeABaseURL,
			Object object, Charset queryEncoding) {
		this.setInternal(scheme, username, password, host, port, path, query, fragment,
				cannotBeABaseURL, object, queryEncoding);
	}

	/**
	 * Factory method to create a new empty {@link URL} instance, with all its parts
	 * set to the default value, called only internally.
	 *
	 * @return The constructed {@link URL} instance.
	 */
	public static URL createInternal() {
		return new URL();
	}

	/**
	 * Internal method, copies the fields of the given {@link URL} instance to {@code
	 * this}.
	 *
	 * @param url
	 * 		The {@link URL} instance to copy.
	 */
	public void copyInternal(URL url) {
		if (url == null) return;
		this.setInternal(url.scheme, url.username, url.password, url.host, url.port,
				url.path, url.query, url.fragment, url.cannotBeABaseURL, url.object,
				url.queryObject.getEncoding());
	}

	/**
	 * Called only internally, sets the parts of the {@link URL} without checking
	 * anything.
	 *
	 * @param scheme
	 * 		The scheme of the URL.
	 * @param username
	 * 		The username of the URL credential.
	 * @param password
	 * 		The password of the URL credential.
	 * @param host
	 * 		The host of the URL.
	 * @param port
	 * 		The port of the URL.
	 * @param path
	 * 		The path of the URL.
	 * @param query
	 * 		The query string of the URL.
	 * @param fragment
	 * 		The fragment part of the URL.
	 * @param cannotBeABaseURL
	 * 		Whether the URL can be a base URL.
	 * @param object
	 * 		The associated object to the URL.
	 */
	public void setInternal(
			String scheme, String username, String password,
			Host host, int port,
			List<String> path, String query, String fragment,
			boolean cannotBeABaseURL,
			Object object, Charset queryEncoding) {
		this.scheme = scheme;
		this.username = username == null ? "" : username;
		this.password = password == null ? "" : password;
		this.host = host;
		this.port = port;
		this.path.addAll(path);
		this.query = query;
		this.fragment = fragment;
		this.cannotBeABaseURL = cannotBeABaseURL;
		this.object = object;

		this.queryObject.clear();
		this.queryObject.setEncoding(queryEncoding);
		if (this.query != null && !this.query.isEmpty())
			this.queryObject.parse(this.query);
	}

	/**
	 * The simplest constructor of URL, called only internally, uses for every field
	 * the default value defined in the URL Standard.
	 */
	private URL() {
	}

	/**
	 * Construct a URL with every field completely equal to that of the given URL.
	 *
	 * @param url
	 * 		The URL to copy.
	 */
	public URL(URL url) {
		this.copyInternal(url);
	}

	// ========== URL PARTS & INTERNAL GETTERS ==========

	// https://url.spec.whatwg.org/#concept-url-scheme
	private String scheme = "";
	// https://url.spec.whatwg.org/#concept-url-username
	private String username = "";
	// https://url.spec.whatwg.org/#concept-url-password
	private String password = "";
	// https://url.spec.whatwg.org/#concept-url-host
	private Host host = null;
	// https://url.spec.whatwg.org/#concept-url-port
	private int port = -1;
	// https://url.spec.whatwg.org/#concept-url-path
	private List<String> path = new ArrayList<>();
	// https://url.spec.whatwg.org/#concept-url-query
	private String query = null;
	// https://url.spec.whatwg.org/#concept-url-fragment
	private String fragment = null;

	// https://url.spec.whatwg.org/#url-cannot-be-a-base-url-flag
	private boolean cannotBeABaseURL = false;
	// https://url.spec.whatwg.org/#concept-url-object
	private Object object = null;

	// https://url.spec.whatwg.org/#concept-url-query-object
	private URLSearchParams queryObject = new URLSearchParams();

	public String getSchemeInternal() {
		return this.scheme;
	}

	public String getUsernameInternal() {
		return this.username;
	}

	public String getPasswordInternal() {
		return this.password;
	}

	public Host getHostInternal() {
		return this.host;
	}

	public int getPortInternal() {
		return this.port;
	}

	public List<String> getPathInternal() {
		return this.path;
	}

	public String getQueryInternal() {
		return this.query;
	}

	/**
	 * Internal method, called only by {@link URLSearchParams#update()}.
	 */
	void setQueryInternal(String query) {
		this.query = query;
	}

	public String getFragmentInternal() {
		return this.fragment;
	}

	public boolean getCannotBeABaseURLInternal() {
		return this.cannotBeABaseURL;
	}

	public Charset getQueryEncodingInternal() {
		return this.queryObject.getEncoding();
	}

	// ========== GETTERS AND SETTERS OF STANDARD-DEFINED MEMBERS ==========

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-href">#dom-url-href</a>
	 */
	public String getHref() {
		return this.serialize(false);
	}

	/**
	 * @throws MalformedURLException
	 * 		If the {@link BasicURLParser} returns {@link URL#failure} while trying to
	 * 		parse the given {@code href}.
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-href">#dom-url-href</a>
	 */
	public void setHref(String href) throws MalformedURLException {
		URL parsedURL = BasicURLParser.parse(href);
		if (parsedURL == null || parsedURL.isFailure())
			throw new MalformedURLException();
		this.copyInternal(parsedURL);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-origin">#dom-url-origin</a>
	 */
	public String getOrigin() {
		return this.getOriginObject().unicodeSerialize();
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-protocol">#dom-url-protocol</a>
	 */
	public String getProtocol() {
		return this.scheme + ':';
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-protocol">#dom-url-protocol</a>
	 */
	public void setProtocol(String protocol) {
		BasicURLParser.parse(protocol + ':', this, ParserStates.SCHEME_START_STATE);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-username">#dom-url-username</a>
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-username">#dom-url-username</a>
	 * @see <a href="https://url.spec.whatwg.org/#set-the-username">#set-the-username</a>
	 */
	public void setUsername(String username) {
		if (this.host == null || this.cannotBeABaseURL) return;
		this.username = PercentEncoder.utf8Encode(StringHelper.toCodePoints(username),
				UserinfoEncodeSet.instance);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-password">#dom-url-password</a>
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-password">#dom-url-password</a>
	 * @see <a href="https://url.spec.whatwg.org/#set-the-password">#set-the-password</a>
	 */
	public void setPassword(String password) {
		if (this.host == null || this.cannotBeABaseURL) return;
		this.password = PercentEncoder.utf8Encode(StringHelper.toCodePoints(password),
				UserinfoEncodeSet.instance);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-host">#dom-url-host</a>
	 */
	public String getHost() {
		if (this.host == null) return "";
		if (this.port == -1) return HostHelper.serialize(this.host);
		return HostHelper.serialize(this.host) + ':' + this.port;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-host">#dom-url-host</a>
	 */
	public void setHost(String host) {
		if (this.cannotBeABaseURL) return;
		BasicURLParser.parse(host, this, ParserStates.HOST_STATE);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-hostname>#dom-url-hostname</a>
	 */
	public String getHostname() {
		if (this.host == null) return "";
		return HostHelper.serialize(this.host);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-hostname>#dom-url-hostname</a>
	 */
	public void setHostname(String hostname) {
		if (this.cannotBeABaseURL) return;
		BasicURLParser.parse(hostname, this, ParserStates.HOSTNAME_STATE);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-port">#dom-url-port</a>
	 */
	public String getPort() {
		if (this.port == -1) return "";
		return String.valueOf(this.port);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-port">#dom-url-port</a>
	 */
	public void setPort(String port) {
		if (this.host == null || this.cannotBeABaseURL ||
				"file".equalsIgnoreCase(this.scheme)) return;
		BasicURLParser.parse(port, this, ParserStates.PORT_STATE);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-pathname">#dom-url-pathname</a>
	 */
	public String getPathname() {
		if (this.cannotBeABaseURL) return this.path.get(0);
		return this.path.stream().map(segment -> '/' + segment)
				.collect(Collectors.joining());
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-pathname">#dom-url-pathname</a>
	 */
	public void setPathname(String pathname) {
		if (this.cannotBeABaseURL) return;
		this.path.clear();
		BasicURLParser.parse(pathname, this, ParserStates.PATH_START_STATE);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-search">#dom-url-search</a>
	 */
	public String getSearch() {
		if (this.query == null || this.query.isEmpty()) return "";
		return '?' + this.query;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-search">#dom-url-search</a>
	 */
	public void setSearch(String search) {
		if (search == null || search.isEmpty()) {
			this.query = "";
			this.queryObject.clear();
			return;
		}

		String input = search.startsWith("?") ? search.substring(1) : search;
		this.query = "";
		BasicURLParser.parse(input, this, ParserStates.QUERY_STATE);
		if (this.query != null && !this.query.isEmpty())
			this.queryObject.parse(this.query);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-searchparams">#dom-url-searchparams</a>
	 */
	public URLSearchParams getSearchParams() {
		return this.queryObject;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-hash">#dom-url-hash</a>
	 */
	public String getHash() {
		if (this.fragment == null || this.fragment.isEmpty()) return "";
		return '#' + this.fragment;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-hash">#dom-url-hash</a>
	 */
	public void setHash(String hash) {
		if ("javascript".equalsIgnoreCase(this.scheme)) return;
		if (hash == null || hash.isEmpty()) {
			this.fragment = null;
			return;
		}

		String input = hash.startsWith("#") ? hash.substring(1) : hash;
		this.fragment = "";
		BasicURLParser.parse(input, this, ParserStates.FRAGMENT_STATE);
	}

	// ========== STANDARD-DEFINED URL CONSTRUCTORS ==========

	/**
	 * Constructs a {@link URL} instance using a input URL string with no base
	 * {@link URL}.
	 *
	 * @param url
	 * 		The url string.
	 *
	 * @throws MalformedURLException
	 * 		If the parsing of {@code url} results in {@link #failure}.
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-url">#dom-url-url</a>
	 */
	public URL(String url) throws MalformedURLException {
		this(url, (URL) null);
	}

	/**
	 * Internal constructor, construct a {@link URL} instance using a input URL string
	 * and a base URL string.
	 * <br>
	 * It is encouraged to use {@link BasicURLParser#parse(String)} to obtain a
	 * {@link URL} instance instead of invoking this constructor for better performance.
	 *
	 * @param url
	 * 		The url string.
	 * @param base
	 * 		The base url string.
	 *
	 * @throws MalformedURLException
	 * 		If parsing of either {@code url} or {@code base} results in {@link #failure}.
	 */
	public URL(String url, String base) throws MalformedURLException {
		this(url, parseBase(base));
	}

	private static URL parseBase(String base) throws MalformedURLException {
		if (base == null) return null;
		URL parsedBase = BasicURLParser.parse(base);
		if (parsedBase == null || parsedBase.isFailure())
			throw new MalformedURLException();
		return parsedBase;
	}

	/**
	 * Construct a {@link URL} instance using an input URL string and a {@code base}
	 * URL by parsing the input with the {@code base} parameter.
	 * <br>
	 * It is encouraged to use {@link BasicURLParser#parse(String, URL)} to obtain a
	 * {@link URL} instance instead of invoking this constructor for better performance.
	 *
	 * @throws MalformedURLException
	 * 		If parsing {@code url} results in a {@link #failure}.
	 * @see <a href="https://url.spec.whatwg.org/#dom-url-url">#dom-url-url</a>
	 */
	public URL(String url, URL parsedBase) throws MalformedURLException {
		URL parsedURL = BasicURLParser.parse(url, parsedBase);
		if (parsedURL == null || parsedURL.isFailure())
			throw new MalformedURLException();

		this.copyInternal(parsedURL);
	}

	// ========== STANDARD-DEFINED URL OPERATIONS ==========

	/**
	 * @see <a href="https://url.spec.whatwg.org/#include-credentials">#include-credentials</a>
	 */
	public boolean includesCredentials() {
		return !this.username.isEmpty() || !this.password.isEmpty();
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#shorten-a-urls-path">#shorten-a-urls-path</a>
	 */
	public URL shortenPath() {
		List<String> newPath = new ArrayList<>(this.path);
		PathHelper.shortenPath(newPath, this.scheme);

		return new URL(this.scheme, this.username, this.password, this.host,
				this.port, newPath, this.query, this.fragment, this.cannotBeABaseURL,
				this.object, this.queryObject.getEncoding());
	}

	// #set-the-username omitted, see {@link #setUsername(String)}

	// #set-the-password omitted, see {@link #setPassword(String)}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#url-serializing">#url-serializing</a>
	 */
	public String serialize(boolean excludeFragmentsFlag) {
		StringBuilder output = new StringBuilder();
		output.append(this.scheme).append(':');

		if (this.host != null) {
			output.append("//");
			if (this.includesCredentials()) {
				output.append(this.username);
				if (this.password != null && !this.password.isEmpty())
					output.append(':').append(this.password);
				output.append('@');
			}
			output.append(HostHelper.serialize(this.host));
			if (this.port != -1)
				output.append(':').append(this.port);
		} else if ("file".equalsIgnoreCase(this.scheme))
			output.append("//");

		if (this.cannotBeABaseURL) output.append(this.path.get(0));
		else this.path.stream().map(segment -> '/' + segment).forEach(output::append);

		if (this.query != null && !this.query.isEmpty())
			output.append('?').append(this.query);

		if (!excludeFragmentsFlag && this.fragment != null && !this.fragment.isEmpty())
			output.append('#').append(this.fragment);

		return output.toString();
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#url-equivalence">#url-equivalence</a>
	 */
	public static boolean equals(URL url1, URL url2, boolean excludeFragmentsFlag) {
		if (url1 == url2) return true;
		if (url1 == null || url2 == null)
			return false;

		String serializedURL1 = url1.serialize(excludeFragmentsFlag);
		String serializedURL2 = url2.serialize(excludeFragmentsFlag);
		return Objects.equals(serializedURL1, serializedURL2);
	}

	/**
	 * @return An {@link Origin} instance of this {@link URL}.
	 * @see <a href="https://url.spec.whatwg.org/#origin">#origin</a>
	 */
	public Origin getOriginObject() {
		return OriginParser.parse(this);
	}

	// ========== JAVA-STYLE OVERRIDES ==========

	/**
	 * @see <a href="https://url.spec.whatwg.org/#url-equivalence">#url-equivalence</a>
	 */
	@Override
	public boolean equals(Object obj) {
		return obj != null && (this == obj || obj instanceof URL
				&& equals(this, (URL) obj, false));
	}

	@Override
	public String toString() {
		return this.serialize(false);
	}
}
