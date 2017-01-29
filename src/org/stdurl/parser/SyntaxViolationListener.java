package org.stdurl.parser;

/**
 *
 */
public interface SyntaxViolationListener {
	/**
	 * Called when the an operation reports a <a href="https://url.spec.whatwg.org/#syntax-violation">syntax
	 * violation</a>.
	 * Note that the operation will not terminate on an syntax violation, implementations
	 * of the listener can choose to discard the result returned by the operation if
	 * syntax violations are not allowed.
	 *
	 * @param message
	 * 		An optional message of the syntax violation.
	 */
	void onSyntaxViolation(String message);
}
