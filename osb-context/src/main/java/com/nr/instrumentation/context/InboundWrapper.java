package com.nr.instrumentation.context;

import java.util.logging.Level;

import com.bea.wli.sb.transports.ResponseHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.NewRelic;

/**
 * InboundWrapper is an implementation of the InboundHeaders class from the New Relic API
 */
public class InboundWrapper implements InboundHeaders {

	/**
	 * Response header we are tracing
	 *
	 * @see  com.bea.wli.sb.transports.ResponseHeaders
	 */
	private ResponseHeaders headers;

	/**
	 * Constructor
	 *
	 * @param h  Headers to set on new instance
	 */
	public InboundWrapper(ResponseHeaders h) {
		headers = h;
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
	 * getHeader returns the value of the requested header, or null
	 *
	 * @param name  Name of the message property to return
	 * @return      Value of the property, or null on error
	 */
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
