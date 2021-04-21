package com.agent.instrumentation.oraclejms;

import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

/**
 * OutboundWrapper is an implementation of the OutboundHeaders class from the New Relic API, created specifically
 * for tracing Oracle JMS Messages
 */
public class OutboundWrapper implements OutboundHeaders {

	/**
	 * Message that we are tracing
	 */
	private Message message;

	/**
	 * Set private attr message to msg
	 *
	 * @param msg  Message to set message to
	 */
	public OutboundWrapper(Message msg) {
		message = msg;
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
	 * setHeader sets the value of the requested message header, or logs an error on failure
	 *
	 * @param name   Name of the message property to set
	 * @param value  Value to set the message property
	 */
	@Override
	public void setHeader(String name, String value) {
		try {
			message.setStringProperty(name, value);
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Error setting string property {0} to {1} on JMS message", new Object[] {name,value});
		}
	}

}
