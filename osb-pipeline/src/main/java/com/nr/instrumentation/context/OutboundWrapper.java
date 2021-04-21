package com.nr.instrumentation.context;

import java.util.logging.Level;

import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.message.RequestMessage;
import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {

	private RequestMessage request;
	
	public OutboundWrapper(RequestMessage req) {
		request = req;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		try {
			RequestHeaders headers = request.getMetaData().getHeaders();
			headers.setHeader(name, value);
		} catch (DispatchException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error setting header on request message: {0} : {1}", new Object[] {name,value});
		}
	}

}
