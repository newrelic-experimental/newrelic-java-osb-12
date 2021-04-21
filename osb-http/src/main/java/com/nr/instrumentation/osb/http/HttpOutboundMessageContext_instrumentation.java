package com.nr.instrumentation.osb.http;

import java.net.URI;

import com.bea.wli.sb.transports.TransportSendListener;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave module for com.bea.wli.sb.transports.http.HttpOutboundMessageContext
 */
@Weave(originalName="com.bea.wli.sb.transports.http.HttpOutboundMessageContext",type=MatchType.BaseClass)
public abstract class HttpOutboundMessageContext_instrumentation {
	
	public abstract URI getURI();

	/**
	 * send marks this transaction as external with params based on getURI().
	 *
	 * NR Trace: Leaf (account for all subsequent time in this method)
	 *
	 * @param listener  Unused, passed to original call
	 */
	@Trace(leaf=true)
	public void send(TransportSendListener listener) {
		ExternalParameters params =  HttpParameters.library("HTTPOutbound").uri(getURI()).procedure("send").noInboundHeaders().build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		
		Weaver.callOriginal();
	}
}
