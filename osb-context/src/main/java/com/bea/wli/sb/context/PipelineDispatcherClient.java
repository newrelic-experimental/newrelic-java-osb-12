package com.bea.wli.sb.context;

import java.net.URI;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.DispatchContext.SourceMetadata;
import com.bea.wli.sb.services.dispatcher.DispatchOptions;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.OutboundWrapper;

/**
 * Weave implementation for PipelineDispatcherClient
 *
 * @see  com.bea.wli.sb.context.PipelineDispatcherClient
 */
@Weave
public abstract class PipelineDispatcherClient {

	private final OutboundEndpoint _endpoint = Weaver.callOriginal();

	/**
	 * Gathers information and adds it onto the callback (which has been extended by the Weaver)
	 *
	 * NR Trace: Leaf (account for all subsequent time in this method)
	 *
	 * @param inboundContext  Used to gather URI if _endpoint is null or has no transport info
	 * @param options         Unused, passed to original call
	 * @param callback        Used to hold NR transaction token
	 *
	 * @see  com.bea.wli.sb.services.dispatcher.callback.DispatchCallback
	 * @see  com.nr.instrumentation.context.OutboundWrapper
	 */
	@Trace(leaf=true)
	public void dispatch(DispatchContext inboundContext, DispatchOptions options, DispatchCallback callback) {
		URI uri = null;

		// Make sure the trace has a token
		if(callback.token == null) {
			callback.token = NewRelic.getAgent().getTransaction().getToken();
		}

		// Prefer _endpoint for URI
		if(_endpoint != null && _endpoint.getTransportInfo() != null) {
			String tmp = _endpoint.getTransportInfo().getURI();
			if(tmp != null && !tmp.isEmpty()) {
				uri = URI.create(_endpoint.getTransportInfo().getURI());
			}
		}

		// If URI not found in _endpoint, try inboundContext
		if(uri == null && inboundContext != null && inboundContext.getSourceMetadata() != null) {
			SourceMetadata source = inboundContext.getSourceMetadata();
			if(source != null) {
				uri = inboundContext.getSourceMetadata().getURI();
			}
		}

		// Mark trace as external with parameters
		if(uri != null) {
			ExternalParameters params = HttpParameters.library("OSB").uri(uri).procedure("dispatch").noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}

		// Wrap NR headers onto outbound request
		if (inboundContext != null) {
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new OutboundWrapper(inboundContext.getRequest()));
		}

		// This could be null...
		callback.nr_uri = uri;

		// Make sure we do what we were initially supposed to do...
		Weaver.callOriginal();
	}
}