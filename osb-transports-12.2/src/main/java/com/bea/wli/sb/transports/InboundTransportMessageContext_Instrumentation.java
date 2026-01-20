package com.bea.wli.sb.transports;

import java.net.URI;

import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface, originalName = "com.bea.wli.sb.transports.InboundTransportMessageContext")
public abstract class InboundTransportMessageContext_Instrumentation {
	public abstract URI getURI();
	@SuppressWarnings("rawtypes")
	public abstract RequestMetaData getRequestMetaData();
	public abstract TransportEndPoint getEndPoint();
	
	@NewField
	public Token token = null;
}
