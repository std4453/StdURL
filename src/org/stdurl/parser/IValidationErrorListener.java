package org.stdurl.parser;

/**
 *
 */
public interface IValidationErrorListener {
	/**
	 * Called when the an operation reports a <a href="https://url.spec.whatwg.org/#validation-error">validation
	 * error</a>.
	 * Note that the operation will not terminate on an validation error, implementations
	 * of the listener can choose to discard the result returned by the operation if
	 * validation errors are not allowed.
	 *
	 * @param message
	 * 		An optional message of the validation error.
	 */
	void onValidationError(String message);
}
