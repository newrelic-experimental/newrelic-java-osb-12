package com.bea.wli.sb.transports;

import java.util.HashMap;
import java.util.Map;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface, originalName = "com.bea.wli.sb.transports.TransportSendListener")
public abstract class TransportSendListener_Instrumentation {

	@NewField
	Token token = null;
	
	@Trace(async=true)
	public void onReceiveResponse(OutboundTransportMessageContext outboundTransportMessageContext) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}
	
	@Trace(async=true)
	public void onError(OutboundTransportMessageContext outboundTransportMessageContext, String paramString1, String paramString2) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("Error Code", paramString1);
		params.put("Error Message", paramString2);
		NewRelic.noticeError("Error received calling "+outboundTransportMessageContext.getURI().toASCIIString(), params);
		Weaver.callOriginal();
	}

}
