package com.nr.instrumentation.transports;

import com.bea.wli.sb.transports.ResponseHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundResponseWrapper implements InboundHeaders {

	private ResponseHeaders headers;
	
	public InboundResponseWrapper( ResponseHeaders h) {
		headers = h;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return (String) headers.getHeader(name);
	}

}
