package org.stdurl.parser;

/**
 * A very simple implementation of {@link ISyntaxViolationListener}, prints the error
 * message to {@code System.err}.
 */
public class SimpleSyntaxViolationListener implements ISyntaxViolationListener {
	public static final SimpleSyntaxViolationListener instance =
			new SimpleSyntaxViolationListener();

	@Override
	public void onSyntaxViolation(String message) {
		System.err.println(message);
	}
}
