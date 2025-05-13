package com.nr.instrumentation.osb.pipeline.dispatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

public class InboundHeaders implements Headers {
	
	private RequestHeaders headers;
	
	public InboundHeaders(RequestHeaders rh) {
		headers = rh;
	}

	@Override
	public void addHeader(String name, String value) {
		headers.setHeader(name, value);
	}

	@Override
	public boolean containsHeader(String name) {
		return headers.containsHeader(name);
	}

	@Override
	public String getHeader(String name) {
		return headers.getHeader(name).toString();
	}

	@Override
	public Collection<String> getHeaderNames() {
		Set<String> names = new HashSet<String>();
		
		headers.getHeaderNames().forEachRemaining(names::add);
		
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
		headers.setHeader(name, value);
	}

}
