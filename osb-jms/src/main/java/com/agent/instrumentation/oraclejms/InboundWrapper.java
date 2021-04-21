package com.agent.instrumentation.oraclejms;

import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.NewRelic;

/**
 * InboundWrapper is an implementation of the InboundHeaders class from the New Relic API, created specifically
 * for tracing Oracle JMS Messages
 */
public class InboundWrapper implements InboundHeaders {
	/**
	 * Message that we are tracing
	 */
	private Message message;

	/**
	 * Set private attr message to msg
	 *
	 * @param msg  Message to set message to
	 */
	public InboundWrapper(Message msg) {
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
	 * getHeader returns the value of the requested message header, or null
	 *
	 * @param name  Name of the message property to return
	 * @return      Value of the property, or null on error
	 */
	@Override
	public String getHeader(String name) {
		try {
			return message.getStringProperty(name);
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Error getting string property {0} from JMS message", new Object[] {name});
			return null;
		}
	}

}
