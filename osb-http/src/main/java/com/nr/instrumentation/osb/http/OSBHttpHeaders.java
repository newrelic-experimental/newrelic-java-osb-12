package com.nr.instrumentation.osb.http;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class OSBHttpHeaders implements Headers {
	
	private HttpURLConnection conn = null;
	
	public OSBHttpHeaders(HttpURLConnection c) {
		conn = c;
	}

	@Override
	public void addHeader(String name, String value) {
		conn.addRequestProperty(name, value);
	}

	@Override
	public boolean containsHeader(String name) {
		return getHeaderNames().contains(name);
	}

	@Override
	public String getHeader(String name) {
		return conn.getRequestProperty(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		
		Map<String, List<String>> requestProps = conn.getRequestProperties();
		if(requestProps != null) {
			return requestProps.keySet();
		}
		return Collections.emptySet();
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		Set<String> values = new HashSet<String>();
		String value = getHeader(name);
		if(value != null) {
			values.add(value);
		}
		return values;
	}

	@Override
	public void setHeader(String name, String value) {
		conn.addRequestProperty(name, value);
	}

}
