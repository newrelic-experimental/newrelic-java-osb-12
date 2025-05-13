package com.nr.instrumentation.osb.http;

import java.net.HttpURLConnection;
import java.net.URI;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportProvider;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave module for com.bea.wli.sb.transports.http.HttpOutboundMessageContext
 */
@Weave(originalName="com.bea.wli.sb.transports.http.HttpOutboundMessageContext",type=MatchType.BaseClass)
public abstract class HttpOutboundMessageContext_instrumentation {
	
	public abstract URI getURI();
	
	protected HttpURLConnection _urlConn = Weaver.callOriginal();
	

	/**
	 * send marks this transaction as external with params based on getURI().
	 *
	 * NR Trace: Leaf (account for all subsequent time in this method)
	 *
	 * @param listener  Unused, passed to original call
	 */
	@Trace(leaf=true)
	public void send(TransportSendListener listener) {
		if(_urlConn != null) {
			OSBHttpHeaders headers = new OSBHttpHeaders(_urlConn);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		}
		ExternalParameters params =  HttpParameters.library("HTTPOutbound").uri(getURI()).procedure("send").noInboundHeaders().build();
		Segment segment = NewRelic.getAgent().getTransaction().startSegment("HttpOutboundHttp");
		segment.reportAsExternal(params);
		
		Weaver.callOriginal();
	}
	
	@Weave(originalName="com.bea.wli.sb.transports.http.HttpOutboundMessageContext$AsyncPostHttpResponseWork")
	static class AsyncPostHttpResponseWork_Instrumentation {
		
		@NewField
		protected Segment segment = null;
		
		AsyncPostHttpResponseWork_Instrumentation(TransportSendListener listener) {
			
		}
		
		public void run() {
			if(segment != null) {
				segment.end();
				segment = null;
			}
			Weaver.callOriginal();
		}
	}
}
