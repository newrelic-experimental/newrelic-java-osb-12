package com.bea.wli.sb.transports.http.generic;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.http.HttpInboundMessageContext;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.BaseClass)
public abstract class RequestHelperBase {

	@Trace
	protected void invokePipeline(HttpInboundMessageContext ctx, TransportOptions options) {
		Weaver.callOriginal();
	}
}
