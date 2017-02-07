package org.stdurl.parser;

import org.stdurl.URL;
import org.stdurl.helpers.EncodingHelper;
import org.stdurl.helpers.StringHelper;

import java.nio.charset.Charset;

/**
 * Represents a set of parameters in {@link ParserStateMachine}.
 */
public class MachineParameters {
	public int[] codePoints;
	public URL base;
	public Charset encoding;
	public int stateOverride;

	public MachineParameters(
			int[] codePoints, URL base, Charset encoding, int stateOverride) {
		this.codePoints = codePoints;
		this.base = base;
		this.encoding = encoding;
		this.stateOverride = stateOverride;
	}

	public MachineParameters(
			String input, URL base, Charset encoding, int stateOverride) {
		this(StringHelper.toCodePoints(input), base, encoding, stateOverride);
	}

	public MachineParameters(String input, int stateOverride) {
		this(input, null, EncodingHelper.UTF8, stateOverride);
	}

	public MachineParameters(String input) {
		this(input, ParserStates.NO_SUCH_STATE);
	}
}
