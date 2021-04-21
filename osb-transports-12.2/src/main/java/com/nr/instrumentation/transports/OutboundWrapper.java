package com.nr.instrumentation.transports;

import com.bea.wli.sb.transports.RequestHeaders;
import com.bea.wli.sb.transports.RequestMetaData;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {

	private RequestMetaData<RequestHeaders> metaData;
	
	public OutboundWrapper(RequestMetaData<RequestHeaders> md) {
		metaData = md;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		RequestHeaders headers = metaData.getHeaders();
		headers.setHeader(name, value);
		metaData.setHeaders(headers);
	}

}
