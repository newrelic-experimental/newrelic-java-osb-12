package oracle.sdpinternal.messaging.jms;

import java.util.logging.Level;

import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import oracle.sdpinternal.messaging.TransportException;

import com.agent.instrumentation.oraclejms.OutboundWrapper;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weaver;

public class LightweightJMSSender {

	/**
	 * Adds New Relic outbound request headers for tracing to a message, then passes message to origional call.
	 *
	 * If the destination is a temporary queue, it is assumed that the message is a RESPONSE.  If it is headed to
	 * a named queue, it's assumed to be an external REQUEST. The only difference to this is the log message generated.
	 *
	 * @param  message             Message that we inspect to get destination details
	 * @param  n                   Unused, passed to original call
	 * @param  n2                  Unused, passed to original call
	 * @param  n3                  Unused, passed to original call
	 * @throws TransportException  If original call throws exception
	 *
	 * @see  com.agent.instrumentation.oraclejms.OutboundWrapper
	 */
    @Trace
    public void send(final Message message, final int n, final int n2, final long n3) throws TransportException {
        if (message != null) {
			try {
				Destination dest = message.getJMSDestination();
				if (dest != null && (dest instanceof Queue || dest instanceof Topic)) {
					/**
					 * We're assuming that if something is being sent on a temp queue, it's likely a response to a
					 * request. If it is being sent on a non-temp queue, it's likely an external request.
					 */
					if (dest instanceof TemporaryQueue || dest instanceof TemporaryTopic) {
						NewRelic.getAgent().getLogger().log(Level.FINE, "Sending message on temporary queue: {0}", dest.toString());
					} else {
						NewRelic.getAgent().getLogger().log(Level.FINE, "Sending message on permanent queue: {0}", dest.toString());
					}

					NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(message));
				} else {
					NewRelic.getAgent().getLogger().log(Level.FINE, "Error processing JMS Message: Invalid Message Type: {0}", message.getClass().getName());
				}
			} catch (Exception e) {
				NewRelic.getAgent().getLogger().log(Level.FINEST, e, "LightweightJMSSender error");
			}
		}
		Weaver.callOriginal();
    }

}
