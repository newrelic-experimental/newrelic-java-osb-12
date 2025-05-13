package com.bea.wli.sb.context;

import java.net.URI;
import java.util.Locale;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.DispatchOptions;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.OutboundWrapper;
import com.newrelic.api.agent.NewRelic;

@Weave
public abstract class PipelineDispatcherClient {

	private final OutboundEndpoint _endpoint = Weaver.callOriginal();
	
	@Trace
	public void dispatch(DispatchContext inboundContext, DispatchOptions options, DispatchCallback callback) {
		URI uri = null;

//		if(callback.token == null) {
//			callback.token = NewRelic.getAgent().getTransaction().getToken();
//		} else {
//			callback.token.link();
//		}

		if(_endpoint != null) {
			OutboundTransportInfo transportInfo = _endpoint.getTransportInfo();
			String uriString = transportInfo.getURI();
			if(!uriString.toLowerCase(Locale.ENGLISH).contains("unknown")) {
				uri = URI.create(uriString);
			}
		}

		if(uri == null && inboundContext != null && inboundContext.getSourceMetadata() != null) {
			uri = inboundContext.getSourceMetadata().getURI();
		}

		ExternalParameters params = HttpParameters.library("OSB").uri(uri).procedure("dispatch").noInboundHeaders().build(); 
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);

		if (inboundContext != null) {
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(inboundContext.getRequest()));
		}

		Weaver.callOriginal();
	}
}
