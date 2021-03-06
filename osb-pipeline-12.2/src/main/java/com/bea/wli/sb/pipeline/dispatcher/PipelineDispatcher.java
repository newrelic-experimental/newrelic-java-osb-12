package com.bea.wli.sb.pipeline.dispatcher;

import java.util.logging.Level;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.bea.wli.sb.services.dispatcher.message.RequestMessage;
import com.bea.wli.sb.transports.RequestHeaders;
import com.bea.wli.sb.transports.RequestMetaData;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.osb.pipeline.dispatch.OutboundWrapper;

@Weave
public abstract class PipelineDispatcher {

	@SuppressWarnings("unchecked")
	@Trace(dispatcher=true)
	public void dispatch(DispatchContext context) {
		DispatchCallback callback = context.getCallback();
		if(callback != null) {
			if(callback.token == null) {
				callback.token = NewRelic.getAgent().getTransaction().getToken();
			} else {
				callback.token.link();
			}
		}
		RequestMessage request = context.getRequest();
		String msgId = request.getMessageID();
		NewRelic.getAgent().getLogger().log(Level.FINE, "PipelineDispatcher - sourceRef name - {0}", new Object[] {context.getSourceMetadata().getRef().getFullName()});
		NewRelic.addCustomParameter("PipelineDispatcher Message ID", msgId);
		try {
			RequestMetaData<RequestHeaders> metaData = request.getMetaData();
			OutboundWrapper outWrapper = new OutboundWrapper(metaData);
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(outWrapper);
		} catch (DispatchException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error processing headers");
		}
		Weaver.callOriginal();
	}
}
