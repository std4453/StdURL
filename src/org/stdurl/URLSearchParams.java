package org.stdurl;

import org.stdurl.helpers.EncodingHelper;
import org.stdurl.urlencoded.URLEncodedParser;
import org.stdurl.urlencoded.URLEncodedSerializer;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @see <a href="https://url.spec.whatwg.org/#urlsearchparams">#urlsearchparams</a>
 */
public class URLSearchParams {
	private Map<String, List<String>> list = new HashMap<>();
	private URL url = null;

	private Charset encoding = EncodingHelper.UTF8;

	public URLSearchParams() {
	}

	public URLSearchParams(Charset encoding) {
		this.encoding = encoding;
	}

	public Charset getEncoding() {
		return this.encoding;
	}

	/**
	 * Internal methods that sets the {@code encoding} of this {@link URLSearchParams}
	 * instance, however doesn't invoke {@link #update()}, therefore the effect of
	 * changing the encoding will be revealed after the next call on {@link #update()},
	 * that is, either by {@link #delete(String)} or {@link #append(String, String)}.
	 *
	 * @param encoding
	 * 		The new encoding.
	 */
	public void setEncoding(Charset encoding) {
		// "Why doesn't this method invoke #update()?"

		// Because it is invoked after the corresponding URL's query field is set and
		// before the new query field has been parsed with #parse(). It is easy to
		// understand that field #encoding affects the result of #parse(), so
		// #setEncoding() has to be called before #parse(). Then, imagine, what will
		// happen if #setEncoding() calls #update()? Say that #list is empty at the
		// invocation of #setEncoding(), because #parse() has not yet been called, then
		// if #setEncoding() invokes #update(), it will try to write the result of
		// #serialize() to the URL object's query field, which, of course, is the empty
		// string. And when the URL calls #parse(), the input parameter, which is
		// actually the query field of the URL object, is also the empty string. But
		// that is not what we want. We want the new query field of the URL object to
		// be passed as input to #parse().
		// Thus, if #setEncoding() calls #update(), #parse() will get a wrong input,
		// and that's why #setEncoding() shouldn't call #update().

		this.encoding = encoding;
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#concept-urlsearchparams-update">#concept-urlsearchparams-update</a>
	 */
	private void update() {
		this.update(false);
	}

	private void update(boolean sorted) {
		if (this.url != null) this.url.setQueryInternal(this.serialize(sorted));
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-append">#dom-urlsearchparams-append</a>
	 */
	public void append(String key, String value) {
		if (this.list.containsKey(key))
			this.list.get(key).add(value);
		else {
			List<String> values = new ArrayList<>();
			values.add(value);
			this.list.put(key, values);
		}
		this.update();
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-delete">#dom-urlsearchparams-delete</a>
	 */
	public void delete(String key) {
		this.list.remove(key);
		this.update();
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-get">#dom-urlsearchparams-get</a>
	 */
	public String get(String key) {
		List<String> values = this.list.get(key);
		if (values == null) return null;
		return values.get(0);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-getall">#dom-urlsearchparams-getall</a>
	 */
	public List<String> getAll(String key) {
		List<String> values = this.list.get(key);
		if (values == null) return new ArrayList<>();
		return new ArrayList<>(values);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-has">#dom-urlsearchparams-has</a>
	 */
	public boolean has(String key) {
		return this.list.containsKey(key);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-set">#dom-urlsearchparams-set</a>
	 */
	public void set(String key, String value) {
		if (this.list.containsKey(key)) this.list.remove(key);
		this.append(key, value);
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#dom-urlsearchparams-sort">#dom-urlsearchparams-sort</a>
	 */
	public void sort() {
		this.update(true);
	}

	/**
	 * Clears the {@code list} stored in this {@link URLSearchParams} instance. This
	 * method would not invoke {@link #update()} and therefore not affect the {@code
	 * query} field in the URL object.
	 */
	public void clear() {
		// See #setEncoding() for explanation on "why doesn't this method invoke
		// #update()".
		// That #clear() is called after the URL object's query field is set and before
		// #parse().

		this.list.clear();
	}

	public void parse(String query) {
		List<Map.Entry<String, String>> tuples = URLEncodedParser
				.parse(query.getBytes(EncodingHelper.UTF8), this.encoding);

		for (Map.Entry<String, String> entry : tuples) {
			String key = entry.getKey(), value = entry.getValue();
			if (this.list.containsKey(key))
				this.list.get(key).add(value);
			else {
				List<String> values = new ArrayList<>();
				values.add(value);
				this.list.put(key, values);
			}
		}
	}

	/**
	 * @see <a href="https://url.spec.whatwg.org/#urlencoded-serializing">#urlencoded-serializing</a>
	 */
	public String serialize() {
		return this.serialize(false);
	}

	public String serialize(boolean sorted) {
		List<Map.Entry<String, String>> tuples = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : this.list.entrySet()) {
			String key = entry.getKey();
			for (String value : entry.getValue())
				tuples.add(new AbstractMap.SimpleEntry<>(key, value));
		}

		if (sorted)
			tuples.sort((o1, o2) -> {
				if (o1 == o2) return 0;
				if (o1 == null) return -1;
				if (o2 == null) return 1;
				return o1.getKey().compareTo(o2.getKey());
			});
		return URLEncodedSerializer.serialize(tuples);
	}

	@Override
	public String toString() {
		return this.serialize();
	}
}
