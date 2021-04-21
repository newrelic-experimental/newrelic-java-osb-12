package com.bea.wli.sb.services.dispatcher.callback;

import java.net.URI;
import java.util.logging.Level;

import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;
import com.bea.wli.sb.transports.ResponseHeaders;
import com.bea.wli.sb.transports.ResponseMetaData;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.InboundWrapper;

/**
 * Weave the DispatchCallback to hook and extend it
 */
@Weave(type=MatchType.Interface)
public abstract class DispatchCallback {

	/**
	 * NR Trace token field added to class
	 */
	@NewField
	public Token token = null;

	/**
	 * NR URI field added to class
	 */
	@NewField
	public URI nr_uri;

	/**
	 * handleResponse weave to start a new external transaction and set params on it
	 *
	 * NR Trace: Dispatcher (Start a new transaction here)
	 *
	 * @param callbackContext  Parsed to gather params for transaction
	 *
	 * @see  com.nr.instrumentation.context.InboundWrapper
	 */
	@Trace(dispatcher=true)
	public void handleResponse(CallbackContext<ResponseMessage> callbackContext) {
		// If we have an existing token, link this work back and expire the token
		if(token != null) {
			token.linkAndExpire();
		}

		// To the actual work
		Weaver.callOriginal();

		// Work our way down the chain until we get the headers we are looking for
		try {
			if(callbackContext != null) {
				ResponseMessage responseMsg = callbackContext.getResponse();
				if(responseMsg != null) {
					ResponseMetaData<?> response = responseMsg.getMetaData();
					if (response != null) {
						ResponseHeaders headers = response.getHeaders();
						if (headers != null) {
							HttpParameters params = HttpParameters.library("OSB").uri(nr_uri).procedure("handleResponse").inboundHeaders(new InboundWrapper(headers)).build();
							NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
						}
					}
					
				}
			}

			// Clean up the nr_uri (BUG: Will not happen on exception, but the token would of expired?)
			if(nr_uri != null) {
				nr_uri = null;
			}
		} catch (DispatchException e) {
			NewRelic.getAgent().getLogger().log(Level.FINER, e, "Unable to get Response Headers in {0}",new Object[] {getClass().getName()});
		}
	}
}
