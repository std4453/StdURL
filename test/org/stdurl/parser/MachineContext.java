package org.stdurl.parser;

/**
 * Represents a set of contextual fields in {@link ParserStateMachine}.
 */
public class MachineContext {
	public int state;
	public int pointer;
	public StringBuffer buffer;
	public boolean atFlag, bracketsFlag, passwordTokenSeenFlag;

	public MachineContext(
			int state, int pointer, StringBuffer buffer, boolean atFlag,
			boolean bracketsFlag,
			boolean passwordTokenSeenFlag) {
		this.state = state;
		this.pointer = pointer;
		this.buffer = buffer;
		if (buffer == null)
			this.buffer = new StringBuffer(); // buffer is always non-null
		this.atFlag = atFlag;
		this.bracketsFlag = bracketsFlag;
		this.passwordTokenSeenFlag = passwordTokenSeenFlag;
	}

	public MachineContext(int state, int pointer, StringBuffer buffer) {
		this(state, pointer, buffer, false, false, false);
	}

	public MachineContext(int state, int pointer, String bufferStr) {
		this(state, pointer, new StringBuffer(bufferStr));
	}

	public MachineContext(int state, int pointer) {
		this(state, pointer, (StringBuffer) null);
	}
}
