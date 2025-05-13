package com.bea.wli.sb.service.handlerchain;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "com.bea.wli.sb.service.handlerchain.OutboundHandlerChain", type = MatchType.Interface)
public abstract class OutboundHandlerChain_Instrumentation {

	@Trace
	public void dispatch(final DispatchContext dispatchContext) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","OSB","OutboundHandlerChain",getClass().getSimpleName(),"dispatch");
		Weaver.callOriginal();
	}
}
