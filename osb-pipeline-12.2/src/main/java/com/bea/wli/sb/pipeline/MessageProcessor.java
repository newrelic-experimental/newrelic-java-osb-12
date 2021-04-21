package com.bea.wli.sb.pipeline;

import java.util.logging.Level;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class MessageProcessor {

	@Trace(dispatcher=true)
	public static void processRequest(RouterContext routerContext) {
		Logger logger = NewRelic.getAgent().getLogger();
		DispatchContext dispatchCtx = routerContext.getInboundDispatchContext();
		if(dispatchCtx != null) {
			DispatchCallback callback = dispatchCtx.getCallback();
			if(callback != null) {
				if(callback.token == null) {
					callback.token = NewRelic.getAgent().getTransaction().getToken();
				} 					
				callback.token.link();
				
			}
		}
		MessageContext messageCtx = routerContext != null ? routerContext.getMessageContext() : null;
		try {
			if (messageCtx != null) {

				NewRelic.addCustomParameter("Message ID", messageCtx.getMessageId());
			}
		} catch (Exception e) {
			logger.log(Level.FINE, e, "Error Message Context Inbound and Outbound Names");
		}
		Weaver.callOriginal();
	}
}
