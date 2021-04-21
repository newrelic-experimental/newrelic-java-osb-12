package oracle.sdpinternal.messaging.jms;

import java.util.logging.Level;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import oracle.sdp.messaging.MessagingException;

import com.agent.instrumentation.oraclejms.InboundWrapper;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.MessageConsumeParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weaver;

public class LightweightJMSReceiver {

	/**
	 * Caputures the original message coming through and attempts to identify the destination type. If successful,
	 * reports to New Relic as an external trace.
	 *
	 * Supports destination Queues and Topics, named and temporary
	 *
	 * @param  b                   Unused, passed to original call
	 * @return                     Message from the original call
	 * @throws MessagingException  If original call throws exception
	 *
	 * @see  com.agent.instrumentation.oraclejms.InboundWrapper
	 */
    @Trace(dispatcher = true)
    public Message receive(final boolean b) throws MessagingException {
        Message message = Weaver.callOriginal();

        DestinationType dt = null;
        try {
			Destination dest = message.getJMSDestination();
			String destName = "Unknown";

			if(Queue.class.isInstance(dest)) {
				Queue q = (Queue)dest;
				if (q instanceof TemporaryQueue) {
					dt = DestinationType.TEMP_QUEUE;
					destName = "Temp";
				} else {
					dt = DestinationType.NAMED_TOPIC;
					destName = q.getQueueName();
				}
			} else if(Topic.class.isInstance(dest)) {
				Topic t = (Topic)dest;
				if(t instanceof TemporaryTopic) {
					dt = DestinationType.TEMP_TOPIC;
					destName = "Temp";
				} else {
					dt = DestinationType.NAMED_TOPIC;
					destName = t.getTopicName();
				}
			}

			if(dt != null) {
				InboundHeaders h = new InboundWrapper(message);
				MessageConsumeParameters params = null;

				params = MessageConsumeParameters.library("OSB-JMS").destinationType(dt).destinationName(destName).inboundHeaders(h).build();
				NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
			}
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Error getting destination type and name from {0}", message);
		}

        return message;
    }

}
