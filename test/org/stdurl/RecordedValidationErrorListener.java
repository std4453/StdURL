package org.stdurl;

import org.stdurl.parser.IValidationErrorListener;

/**
 * An implementation of {@link IValidationErrorListener} used in tests, records the number
 * of occurrences of validation errors.
 */
public class RecordedValidationErrorListener implements IValidationErrorListener {
	private int occurrences = 0;

	@Override
	public void onValidationError(String message) {
		++this.occurrences;
	}

	/**
	 * Clears the occurrences counter of this listener.
	 */
	public void clear() {
		this.occurrences = 0;
	}

	public int getOccurrences() {
		return this.occurrences;
	}

	public boolean occurred() {
		return this.occurrences > 0;
	}
}
