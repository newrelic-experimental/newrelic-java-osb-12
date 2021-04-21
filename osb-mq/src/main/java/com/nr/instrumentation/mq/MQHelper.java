package com.nr.instrumentation.mq;

import java.util.logging.Level;

import com.bea.wli.sb.transports.mq.MQOutboundMessageContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TracedMethod;

/**
 * Helper class for weave module
 */
public class MQHelper {

    /**
     * Helper for tracing outbound messages through their context.
     *
     * NR Metrics:  Adds two rollup metrics

     * NR Trace:    Wraps outbound message headers
     *
     * @param ctx     Context to add outbound headers onto
     * @param tracer  Tracer that we add rollup onto
     *
     * @see  com.nr.instrumentation.mq.OutboundWrapper
     */
    public static void processSendMessage(MQOutboundMessageContext ctx, TracedMethod tracer) {
        if (tracer == null) {
            NewRelic.getAgent().getLogger().log(Level.FINER, "processSendMessage(): no tracer");
        } else {
            // Add External metrics for this outgoing MQ message
            tracer.addRollupMetricName("External/all");
            tracer.addRollupMetricName("External", "allOther");

            try {
                // Set outgoing CAT headers for this MQ message
                NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(ctx));
            } catch (Exception e) {
                NewRelic.getAgent().getLogger().log(Level.FINE, e, "Unable to set outbound CAT headers on MQ message");
            }
        }
    }

}
