package org.stdurl;

import org.stdurl.parser.ISyntaxViolationListener;

/**
 * An implementation of {@link ISyntaxViolationListener} used in tests, records the number
 * of occurrences of syntax violations.
 */
public class RecordedSyntaxViolationListener implements ISyntaxViolationListener {
	private int occurrences = 0;

	@Override
	public void onSyntaxViolation(String message) {
		++occurrences;
	}

	/**
	 * Clears the occurrences counter of this listener.
	 */
	public void clear() {
		this.occurrences = 0;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public boolean occurred() {
		return this.occurrences > 0;
	}
}
