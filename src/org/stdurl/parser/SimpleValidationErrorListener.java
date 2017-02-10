package org.stdurl.parser;

/**
 * A very simple implementation of {@link IValidationErrorListener}, prints the error
 * message to {@code System.err}.
 */
public class SimpleValidationErrorListener implements IValidationErrorListener {
	public static final SimpleValidationErrorListener instance =
			new SimpleValidationErrorListener();

	@Override
	public void onSyntaxViolation(String message) {
		System.err.println(message);
	}
}
