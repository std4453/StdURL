package org.stdurl.parser;

import org.stdurl.URL;

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
}
