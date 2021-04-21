package com.bea.wli.sb.transports;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.OutboundWrapper;

@Weave(type=MatchType.Interface)
public abstract class TransportProvider {

	@SuppressWarnings("unchecked")
	@Trace
	public void sendMessageAsync(TransportSender transportSender, TransportSendListener transportSendListener, TransportOptions transportOptions) throws TransportException {
		RequestMetaData<RequestHeaders> requestMeta = (RequestMetaData<RequestHeaders>)transportSender.getMetaData();
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(requestMeta));
		if(transportSendListener.token == null) {
			transportSendListener.token = NewRelic.getAgent().getTransaction().getToken();
		} else {
			transportSendListener.token.link();
		}
		Weaver.callOriginal();
	}
}
