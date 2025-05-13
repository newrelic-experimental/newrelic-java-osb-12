package com.newrelic.instrumentation.labs.osb.transports;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class OutboundTransportHeaders implements Headers {
	
	private RequestHeaders requestHeaders  = null;
	
	public OutboundTransportHeaders(RequestHeaders h) {
		requestHeaders = h;
	}

	@Override
	public void addHeader(String name, String value) {
		requestHeaders.setHeader(name, value);
	}

	@Override
	public boolean containsHeader(String name) {
		return requestHeaders.containsHeader(name);
	}

	@Override
	public String getHeader(String name) {
		Object value = requestHeaders.getHeader(name);
		return value != null ? value.toString() : null;
	}

	@Override
	public Collection<String> getHeaderNames() {
		Set<String> names = new HashSet<String>();
		Iterator<String> iterator = requestHeaders.getHeaderNames();
		while(iterator.hasNext()) {
			names.add(iterator.next());
		}
		return names;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		Set<String> values = new HashSet<String>();
		String value = getHeader(name);
		if(value != null && !value.isEmpty()) {
			values.add(value);
		}
		return values;
	}

	@Override
	public void setHeader(String name, String value) {
		requestHeaders.setHeader(name, value);
	}

}
