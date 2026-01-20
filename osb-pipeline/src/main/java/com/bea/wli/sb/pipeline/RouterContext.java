package com.bea.wli.sb.pipeline;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.api.agent.NewRelic;

@Weave
public abstract class RouterContext {

	public abstract DispatchContext getInboundDispatchContext();
	public abstract MessageContext getMessageContext();
	
	@Trace
	public void init(DispatchContext inboundContext) {
//		DispatchCallback callBack = inboundContext.getCallback();
//		if(callBack != null) {
//			if(callBack.token == null) {
//				callBack.token = NewRelic.getAgent().getTransaction().getToken();
//			}
//			callBack.token.link();
//		}
		Weaver.callOriginal();
	}
}
