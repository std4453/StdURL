package org.stdurl.parser;

/**
 * @see <a href="https://url.spec.whatwg.org/#host-state">#host-state</a>
 */
public class HostState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		// Host state is the very same to #hostname-state
		IParserState hostnameState = ParserStates.getState(ParserStates.HOSTNAME_STATE);
		hostnameState.execute(context);
	}
}
