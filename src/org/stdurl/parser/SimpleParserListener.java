package org.stdurl.parser;

/**
 * A very simple implementation of {@link SyntaxViolationListener}, prints the error
 * message to {@code System.err}.
 */
public class SimpleParserListener implements SyntaxViolationListener {
	public static final SimpleParserListener instance = new SimpleParserListener();

	@Override
	public void onSyntaxViolation(String message) {
		System.err.println(message);
	}
}
