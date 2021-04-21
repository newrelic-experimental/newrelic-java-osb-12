package com.bea.wli.sb.transports.mq;

import java.util.logging.Level;

import com.bea.wli.sb.transports.OutboundTransportMessageContext;
import com.bea.wli.sb.transports.TransportException;
import com.bea.wli.sb.transports.TransportSendListener;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.mq.MQHelper;

/**
 * Weave module for MQOutboundMessageContext
 *
 * @see  com.bea.wli.sb.transports.mq.MQOutboundMessageContext
 * @see  com.nr.instrumentation.mq.MQHelper
 */
@Weave
public abstract class MQOutboundMessageContext implements OutboundTransportMessageContext {

    /**
     * Trace send and add Message/MQ/Produce metric, log on failure.
     *
     * NR Trace:  Start of a transaction
     *
     * @param listener             Unused
     * @throws TransportException  Unused
     */
    @Trace(dispatcher = true)
    public void send(final TransportSendListener listener) throws TransportException {
        try {
            NewRelic.getAgent().getTracedMethod().setMetricName("Message", "MQ", "Produce");
            MQHelper.processSendMessage(this, NewRelic.getAgent().getTracedMethod());
        } catch (Exception e) {
            NewRelic.getAgent().getLogger().log(Level.FINE, e, "Unable to set metadata on outgoing MQ message");
        }

        Weaver.callOriginal();
    }

}