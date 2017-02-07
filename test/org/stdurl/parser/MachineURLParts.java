package org.stdurl.parser;

import org.stdurl.host.Host;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MachineURLParts {
	public String scheme;
	public String username;
	public String password;
	public Host host;
	public int port;
	public boolean cannotBeABaseURL;
	public List<String> path;
	public String query;
	public String fragment;

	public MachineURLParts(
			String scheme, String username, String password, Host host, int port,
			boolean cannotBeABaseURL, List<String> path, String query,
			String fragment) {
		this.scheme = scheme;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.cannotBeABaseURL = cannotBeABaseURL;
		this.path = path;
		if (path == null)
			this.path = new ArrayList<>(); // path is always non-null
		this.query = query;
		this.fragment = fragment;
	}

	public MachineURLParts() {
		// default values from URL.java
		this("", "", "", null, -1, false, null, null, null);
	}
}
