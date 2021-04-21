package com.bea.wli.sb.transports;

import java.net.URI;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.InboundWrapper;

@Weave(type = MatchType.Interface)
public abstract class TransportManager {
	
	@Trace(dispatcher = true)
	public void receiveMessage(InboundTransportMessageContext inboundCtx, TransportOptions transportOptions) throws TransportException {
		URI uri = inboundCtx.getURI();
		TransportEndPoint endpoint = inboundCtx.getEndPoint();
		Ref serviceRef = endpoint.getServiceRef();
		String serviceName = serviceRef.getFullName();
		
		// Name the traced method something like Custom/OSB/MQInboundMessageContext/receiveMessage
//		NewRelic.getAgent().getTracedMethod().setMetricName("Custom", "TransportManager", inboundCtx.getClass().getSimpleName(),uri.getPath(), "receiveMessage");
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom", "OSB", serviceName, "receiveMessage");
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "OSB", new String[] {"Custom", "TransportManager", inboundCtx.getClass().getSimpleName(),uri.getPath()});
		// Capture incoming headers for CAT purposes
		RequestHeaders requestHeaders = inboundCtx.getRequestMetaData().getHeaders();
		HttpParameters params = HttpParameters.library("OSB-Transport").uri(uri).procedure("recieveMessage").inboundHeaders(new InboundWrapper(requestHeaders)).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		if(inboundCtx.token == null) {
			inboundCtx.token = NewRelic.getAgent().getTransaction().getToken();
		} else {
			inboundCtx.token.link();
		}
		Weaver.callOriginal();
	}
}
