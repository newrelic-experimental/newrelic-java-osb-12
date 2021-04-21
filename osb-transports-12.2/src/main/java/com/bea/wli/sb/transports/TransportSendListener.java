package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.InboundResponseWrapper;

@Weave(type=MatchType.Interface)
public abstract class TransportSendListener {

	@NewField
	Token token = null;
	
	@Trace(async=true)
	public void onReceiveResponse(OutboundTransportMessageContext outboundTransportMessageContext) {
		try {
			ResponseHeaders headers = outboundTransportMessageContext.getResponseMetaData().getHeaders();
			
			URI uri = outboundTransportMessageContext.getURI();
			HttpParameters params = HttpParameters.library("OSB-Transport").uri(uri).procedure("onReceiveResponse").inboundHeaders(new InboundResponseWrapper(headers)).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		} catch (TransportException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error getting inbound headers", new Object[] {});
		}
		Weaver.callOriginal();
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
	}
	
	@Trace(async=true)
	public void onError(OutboundTransportMessageContext outboundTransportMessageContext, String paramString1, String paramString2) {
		try {
			ResponseHeaders headers = outboundTransportMessageContext.getResponseMetaData().getHeaders();
			URI uri = outboundTransportMessageContext.getURI();
			HttpParameters params = HttpParameters.library("OSB-Transport").uri(uri).procedure("onReceiveResponse").inboundHeaders(new InboundResponseWrapper(headers)).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		} catch (TransportException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error getting inbound headers", new Object[] {});
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("Error Code", paramString1);
		params.put("Error Message", paramString2);
		NewRelic.noticeError("Error received calling "+outboundTransportMessageContext.getURI().toASCIIString(), params);
		Weaver.callOriginal();
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
	}

}
