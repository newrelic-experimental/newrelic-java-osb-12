package com.bea.wli.sb.context;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.DispatchOptions;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.OutboundWrapper;

@Weave
public abstract class PipelineDispatcherClient {

	@Trace
	public void dispatch(DispatchContext inboundContext, DispatchOptions options, DispatchCallback callback) {
//		if(callback.token == null) {
//			callback.token = NewRelic.getAgent().getTransaction().getToken();
//		} else {
//			callback.token.link();
//		}
//		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(inboundContext.getRequest()));
		Weaver.callOriginal();
	}
}
