package org.stdurl.parser;

/**
 * A very simple implementation of {@link SyntaxViolationListener}, discards the error
 * message.
 */
public class DumbParserListener implements SyntaxViolationListener {
	public static final DumbParserListener instance = new DumbParserListener();

	@Override
	public void onSyntaxViolation(String message) {
	}
}
