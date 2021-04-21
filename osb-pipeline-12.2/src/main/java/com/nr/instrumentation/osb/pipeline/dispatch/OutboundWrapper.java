package com.nr.instrumentation.osb.pipeline.dispatch;

import com.bea.wli.sb.transports.RequestHeaders;
import com.bea.wli.sb.transports.RequestMetaData;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {
	
	private RequestMetaData<RequestHeaders>  meta_data;

	public OutboundWrapper(RequestMetaData<RequestHeaders> metaData) {
		meta_data = metaData;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		if(meta_data != null) {
			RequestHeaders headers = meta_data.getHeaders();
			headers.setHeader(name, value);
		}

	}

}
