package com.nr.instrumentation.mq;

import java.util.logging.Level;

import com.bea.wli.sb.transports.mq.MQOutboundMessageContext;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

/**
 * Helper module for weave instrumentation
 *
 * @see  com.nr.instrumentation.mq.MQHelper
 */
public class OutboundWrapper implements OutboundHeaders {

	private final MQOutboundMessageContext ctx;

	/**
	 * Constructor
	 *
	 * @param ctx  Context for this to trace
	 */
	public OutboundWrapper(MQOutboundMessageContext ctx) {
		this.ctx = ctx;
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
	 * setHeader sets the value of the requested message context, or logs an error on failure
	 *
	 * @param name   Name of the message property to set
	 * @param value  Value to set the message property
	 */
	@Override
	public void setHeader(String name, String value) {
		try {
			ctx.getResponseMetaData().getHeaders().setHeader(name, value);
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error setting property ({0}) on MQ message.", name);
		}
	}

}