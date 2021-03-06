package org.stdurl.parser;

import org.stdurl.RecordedValidationErrorListener;
import org.stdurl.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ParserStateTestHelper {
	public static void testCompare(
			ITerminateCondition condition,
			MachineParameters params, boolean validationErrorExpected,
			MachineURLParts startParts, MachineURLParts endParts,
			MachineContext startContext, MachineContext endContext) {
		InjectedParserStateMachine machine =
				run(condition, params, startParts, startContext, validationErrorExpected);
		assertNotEquals(URL.failure, machine.returnValue);
		assertFalse(machine.terminateRequested);
		check(endParts, machine.finalParts);
		check(endContext, machine.finalContext);
	}

	public static void testFailure(
			ITerminateCondition condition,
			MachineParameters params,
			MachineURLParts parts,
			MachineContext context,
			boolean validationErrorExpected) {
		assertEquals(URL.failure,
				run(condition, params, parts, context, validationErrorExpected)
						.returnValue);
	}

	public static void testTerminated(
			ITerminateCondition condition,
			MachineParameters params,
			MachineURLParts parts,
			MachineContext context,
			boolean validationErrorExpected) {
		assertTrue(run(condition, params, parts, context, validationErrorExpected)
				.terminateRequested);
	}

	public static void testTerminatedAndCompare(
			ITerminateCondition condition,
			MachineParameters params,
			boolean validationErrorExpected,
			MachineURLParts startParts, MachineURLParts endParts,
			MachineContext startContext, MachineContext endContext) {
		InjectedParserStateMachine machine =
				run(condition, params, startParts, startContext, validationErrorExpected);
		assertNotEquals(URL.failure, machine.returnValue);
		assertTrue(machine.terminateRequested);
		check(endParts, machine.finalParts);
		check(endContext, machine.finalContext);
	}

	private static InjectedParserStateMachine run(
			ITerminateCondition condition,
			MachineParameters params, MachineURLParts parts, MachineContext context,
			boolean validationErrorExpected) {
		RecordedValidationErrorListener listener = new RecordedValidationErrorListener();
		InjectedParserStateMachine stateMachine = new InjectedParserStateMachine(
				params, parts, context, listener, condition);
		stateMachine.run();
		assertEquals(validationErrorExpected, listener.occurred());
		return stateMachine;
	}

	private static void check(MachineURLParts expected, MachineURLParts actual) {
		if (expected == actual) return;
		assertFalse(expected == null || actual == null);
		assertEquals(expected.scheme, actual.scheme);
		assertEquals(expected.username, actual.username);
		assertEquals(expected.password, actual.password);
		assertEquals(expected.host, actual.host);
		assertEquals(expected.port, actual.port);
		assertEquals(expected.cannotBeABaseURL, actual.cannotBeABaseURL);
		int expectedPathSize = expected.path.size();
		int actualPathSize = actual.path.size();
		assertEquals(expectedPathSize, actualPathSize);
		for (int i = 0; i < expectedPathSize; ++i)
			assertEquals(expected.path.get(i), actual.path.get(i));
		assertEquals(expected.query, actual.query);
		assertEquals(expected.fragment, actual.fragment);
	}

	private static void check(MachineContext expected, MachineContext actual) {
		if (expected == actual) return;
		assertFalse(expected == null || actual == null);
		assertEquals(expected.state, actual.state);
		assertEquals(expected.pointer, actual.pointer);
		assertEquals(expected.buffer.toString(), actual.buffer.toString());
		assertEquals(expected.atFlag, actual.atFlag);
		assertEquals(expected.bracketsFlag, actual.bracketsFlag);
		assertEquals(expected.passwordTokenSeenFlag, actual.passwordTokenSeenFlag);
	}
}
