package com.nr.instrumentation.transports;

import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundRequestWrapper implements InboundHeaders {

	private RequestHeaders headers;
	
	public InboundRequestWrapper(RequestHeaders r) {
		headers = r;
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
