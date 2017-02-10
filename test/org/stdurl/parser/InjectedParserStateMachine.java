package org.stdurl.parser;

/**
 * A class that extends {@link ParserStateMachine} that makes various tests possible.<br>
 * This is realized by injecting into several key points of the state machine, while
 * opening access of some originally protected fields to test codes.
 */
public class InjectedParserStateMachine extends ParserStateMachine {
	private ITerminateCondition condition;

	// these two field are available after invoking #run()
	public MachineURLParts finalParts;
	public MachineContext finalContext;

	private boolean terminateRequiredByInjection = false;
	private boolean savedTerminateRequired;

	public InjectedParserStateMachine(
			MachineParameters params,
			MachineURLParts parts,
			MachineContext context,
			IValidationErrorListener listener,
			ITerminateCondition condition) {
		super(params.codePoints, params.base, params.encoding, params.stateOverride,
				listener);
		this.setURLFields(parts.scheme, parts.username, parts.password, parts.host,
				parts.port, parts.cannotBeABaseURL, parts.path, parts.query,
				parts.fragment);

		this.state = context.state;
		this.setPointer(context.pointer);
		this.buffer = new StringBuffer(context.buffer);
		this.atFlag = context.atFlag;
		this.bracketsFlag = context.bracketsFlag;
		this.passwordTokenSeenFlag = context.passwordTokenSeenFlag;

		this.condition = condition;
	}

	@Override
	protected void beforeLoop(int ignored) {
		// injected, the state is set by #setContext(MachineContext), therefore is the
		// parameter initialState (which is always 0) ignored.
	}

	@Override
	protected void loop() {
		super.loop();

		// if terminated required by injected method, save the current state and
		// terminate the state machine.
		if (this.condition.shouldTerminate(this)) {
			this.terminateRequiredByInjection = true;
			this.savedTerminateRequired = this.terminateRequested;
			this.terminateRequested = true;
		}
	}

	@Override
	protected void afterLoop() {
		// injected, the fields are not passed to this.url. Instead, this.finalParts and
		// this.finalContext are generated from the fields in this instance
		this.finalParts = new MachineURLParts(
				this.scheme,
				this.username,
				this.password,
				this.host,
				this.port,
				this.cannotBeABaseURL,
				this.path,
				this.query,
				this.fragment);
		this.finalContext = new MachineContext(
				this.state,
				this.pointer,
				this.buffer,
				this.atFlag,
				this.bracketsFlag,
				this.passwordTokenSeenFlag);

		// if termination is requested by injected method, restore the saved state
		if (this.terminateRequiredByInjection)
			this.terminateRequested = this.savedTerminateRequired;
	}

	public void run() {
		// initialState is always 0, see comment in #beforeLoop(int)
		super.run(0);
	}
}
