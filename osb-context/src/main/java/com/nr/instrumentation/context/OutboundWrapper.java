package com.nr.instrumentation.context;

import java.util.logging.Level;

import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.message.RequestMessage;
import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

/**
 * OutboundWrapper is an implementation of the OutboundHeaders class from the New Relic API
 */
public class OutboundWrapper implements OutboundHeaders {

	/**
	 * Request message we are tracing
	 *
	 * @see  com.bea.wli.sb.services.dispatcher.message.RequestMessage
	 */
	private RequestMessage request;

	/**
	 * Constructor
	 *
	 * @param req  RequestMessage to set on new instance
	 */
	public OutboundWrapper(RequestMessage req) {
		request = req;
	}

	/**
	 * Override getHeaderType and always return MESSAGE
	 *
	 * @return HeaderType.MESSAGE (constant)
	 *
	 * @see  com.newrelic.api.agent.HeaderType
	 */
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	/**
	 * setHeader atempts to set the requested property on the request, logs error on failure.
	 *
	 * @param name   Name of property to set
	 * @param value  Value of property to set
	 */
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
