package org.stdurl.parser;

import org.stdurl.helpers.SchemeHelper;

/**
 * @see <a href="https://url.spec.whatwg.org/#relative-state">#relative-state</a>
 */
public class RelativeState implements IParserState {
	@Override
	public void execute(ParserContext context) throws Throwable {
		context.setScheme(context.base.getSchemeInternal());

		switch (context.c) {
			case 0:
				context.setUsername(context.base.getUsernameInternal());
				context.setPassword(context.base.getPasswordInternal());
				context.setHost(context.base.getHostInternal());
				context.setPort(context.base.getPortInternal());
				context.path.addAll(context.base.getPathInternal());
				context.setQuery(context.base.getQueryInternal());
				break;
			case '/':
				context.setState(ParserStates.RELATIVE_SLASH_STATE);
				break;
			case '?':
				context.setUsername(context.base.getUsernameInternal());
				context.setPassword(context.base.getPasswordInternal());
				context.setHost(context.base.getHostInternal());
				context.setPort(context.base.getPortInternal());
				context.path.addAll(context.base.getPathInternal());
				context.setQuery("");
				context.setState(ParserStates.QUERY_STATE);
				break;
			case '#':
				context.setUsername(context.base.getUsernameInternal());
				context.setPassword(context.base.getPasswordInternal());
				context.setHost(context.base.getHostInternal());
				context.setPort(context.base.getPortInternal());
				context.path.addAll(context.base.getPathInternal());
				context.setQuery(context.base.getQueryInternal());
				context.setFragment("");
				context.setState(ParserStates.FRAGMENT_STATE);
				break;
			default:
				if (SchemeHelper.isSpecialScheme(context.scheme) && context.c == '\\') {
					context.reportSyntaxViolation("Backslash should be slash.");
					context.setState(ParserStates.RELATIVE_SLASH_STATE);
				} else {
					context.setUsername(context.base.getUsernameInternal());
					context.setPassword(context.base.getPasswordInternal());
					context.setHost(context.base.getHostInternal());
					context.setPort(context.base.getPortInternal());
					context.path.addAll(context.base.getPathInternal());
					if (context.path.size() >= 1)
						context.path.remove(context.path.size() - 1);
					context.setState(ParserStates.PATH_STATE);
					context.setPointer(context.pointer - 1);
				}
		}
	}
}
