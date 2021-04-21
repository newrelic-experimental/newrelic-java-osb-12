package com.nr.instrumentation.osb.pipeline.dispatch;

import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {

	private RequestHeaders headers;
	
	public InboundWrapper(RequestHeaders h) {
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
