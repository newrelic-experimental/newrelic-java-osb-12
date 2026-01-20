package com.bea.wli.sb.services.dispatcher.callback;

import java.net.URI;
import java.util.logging.Level;

import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.message.FaultMessage;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;
import com.bea.wli.sb.transports.ResponseHeaders;
import com.bea.wli.sb.transports.ResponseMetaData;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.InboundWrapper;

@Weave(type=MatchType.Interface)
public abstract class DispatchCallback {

	@NewField
	public Token token = null;

	@Trace(dispatcher=true)
	public void handleFault(CallbackContext<FaultMessage> paramCallbackContext) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		FaultMessage m = paramCallbackContext.getResponse();
		NewRelic.noticeError(m.getErrorMessage());
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void handleResponse(CallbackContext<ResponseMessage> callbackContext) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
//		try {
//			if(callbackContext != null) {
//				CallbackOptions options = callbackContext.getOptions();
//				URI uri = null;
//				if(options != null) {
//					uri = options.getURI();
//				}
//				ResponseMessage responseMsg = callbackContext.getResponse();
//				if(responseMsg != null) {
//					ResponseMetaData<?> response = responseMsg.getMetaData();
//					if (response != null) {
//						ResponseHeaders headers = response.getHeaders();
//						if (headers != null) {
//							InboundWrapper inboundHeaders = new InboundWrapper(headers);
//							ExternalParameters params = HttpParameters.library("OSB").uri(uri).procedure("handleResponse").inboundHeaders(inboundHeaders).build();
//							NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
//						} else {
//							if(uri != null) {
//								ExternalParameters params = HttpParameters.library("OSB").uri(uri).procedure("handleResponse").noInboundHeaders().build();
//								NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
//							}
//						}
//					}
//				}
//			}
//		} catch (DispatchException e) {
//			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Unable to get Response Headers in {0}",new Object[] {getClass().getName()});
//		}
	}
}
