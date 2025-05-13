package com.bea.wli.sb.transports;

import java.net.URI;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.osb.transports.OutboundTransportHeaders;

@Weave(originalName = "com.bea.wli.sb.transports.TransportManagerImpl")
public abstract class TransportManagerImpl_Instrumentation {
	
	@Trace(dispatcher = true)
	public void receiveMessage(InboundTransportMessageContext_Instrumentation inboundCtx, TransportOptions transportOptions) {
		URI uri = inboundCtx.getURI();
		TransportEndPoint endpoint = inboundCtx.getEndPoint();
		Ref serviceRef = endpoint.getServiceRef();
		String serviceName = serviceRef.getFullName();
		
		// Name the traced method something like Custom/OSB/MQInboundMessageContext/receiveMessage
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom", "OSB", serviceName, "receiveMessage");
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "OSB", new String[] {"Custom", "TransportManager", inboundCtx.getClass().getSimpleName(),uri.getPath()});
		RequestHeaders requestHeaders = inboundCtx.getRequestMetaData().getHeaders();
		if(requestHeaders != null) {
			OutboundTransportHeaders wrapper = new OutboundTransportHeaders(requestHeaders);
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, wrapper);
		}
		
		Weaver.callOriginal();
	}
}
