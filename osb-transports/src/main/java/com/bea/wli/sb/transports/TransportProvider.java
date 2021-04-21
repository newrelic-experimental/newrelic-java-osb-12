package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.logging.Level;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.InboundWrapper;
import com.nr.instrumentation.transports.OutboundWrapper;

@Weave(type=MatchType.Interface)
public abstract class TransportProvider {

	@SuppressWarnings("unchecked")
	@Trace(leaf=true)
	public void sendMessageAsync(TransportSender transportSender, TransportSendListener transportSendListener, TransportOptions transportOptions) throws TransportException {
		RequestMetaData<RequestHeaders> requestMeta = null;
		try {
			requestMeta = (RequestMetaData<RequestHeaders>)transportSender.getMetaData();
		} catch (TransportException e) {
			NewRelic.getAgent().getLogger().log(Level.FINER,e, "Error getting request metadata", new Object[0]);
		}
		
		if (requestMeta != null) {
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(requestMeta));
		}
		if(transportSendListener.token == null) {
			transportSendListener.token = NewRelic.getAgent().getTransaction().getToken();
		} else {
			transportSendListener.token.link();
		}
		if(transportSendListener.startTime == 0L) {
			transportSendListener.startTime = System.currentTimeMillis();
		}
		if(ServiceTransportSender.class.isInstance(transportSender)) {
			ServiceTransportSender serviceSender = (ServiceTransportSender)transportSender;
			TransportEndPoint endPoint = serviceSender.getEndPoint();
			if(endPoint != null) {
				Ref ref = endPoint.getServiceRef();
				String serviceName = ref.getFullName();
				transportSendListener.serviceName = serviceName;
			}
		}
		URI uri = transportOptions == null ? null : transportOptions.getURI();
		if (uri != null) {
			
			RequestHeaders h = requestMeta.getHeaders();
			ExternalParameters params = HttpParameters.library("HTTPTransport").uri(uri).procedure("sendMessageAsync").inboundHeaders(new InboundWrapper(h)).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		Weaver.callOriginal();
	}
}
