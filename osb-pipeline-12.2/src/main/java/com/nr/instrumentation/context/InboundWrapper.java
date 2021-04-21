package com.nr.instrumentation.context;

import java.util.logging.Level;

import com.bea.wli.sb.transports.ResponseHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.NewRelic;

public class InboundWrapper implements InboundHeaders {

	private ResponseHeaders headers;
	
	public InboundWrapper(ResponseHeaders h) {
		headers = h;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		try {
			return (String) headers.getHeader(name);
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error retrieving headers", new Object[] {});
			return null;
		}
	}

}
