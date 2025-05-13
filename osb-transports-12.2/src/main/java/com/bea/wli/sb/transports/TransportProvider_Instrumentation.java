package com.bea.wli.sb.transports;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.osb.transports.OutboundTransportHeaders;

@Weave(type=MatchType.Interface, originalName = "com.bea.wli.sb.transports.TransportProvider")
public abstract class TransportProvider_Instrumentation {

	@SuppressWarnings("unchecked")
	@Trace(leaf = true)
	public void sendMessageAsync(TransportSender transportSender, TransportSendListener_Instrumentation transportSendListener, TransportOptions transportOptions) {
		
		try {
			RequestMetaData<RequestHeaders> requestMeta = (RequestMetaData<RequestHeaders>)transportSender.getMetaData();
			RequestHeaders headers = requestMeta.getHeaders();
			OutboundTransportHeaders wrapper = new OutboundTransportHeaders(headers);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(wrapper);
			requestMeta.setHeaders(headers);
		} catch (TransportException e) {
		}
		if(transportSendListener.token == null) {
			transportSendListener.token = NewRelic.getAgent().getTransaction().getToken();
		}
		Weaver.callOriginal();
	}
}
