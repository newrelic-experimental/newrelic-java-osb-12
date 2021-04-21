package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.logging.Level;

import com.bea.wli.sb.service.ProxyService;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.api.agent.NewRelic;
import com.nr.instrumentation.transports.InboundRequestWrapper;

@Weave
public abstract class TransportDispatcherClient {

	@Trace
	public void dispatch(ProxyService service, InboundTransportMessageContext ctx, TransportOptions options) {
		try {
			// Set the transaction name to something like "Message/incoming.host.com/Consume"
			RequestHeaders headers = ctx.getRequestMetaData().getHeaders();
			
			String uri = null;
			URI serviceURI = null;
			if(service != null) {
				serviceURI = service.getServiceURI();
				if(serviceURI != null) {
					uri = serviceURI.getPath();
				}
			}
			if(uri == null) {
				serviceURI = ctx.getURI();
				if(serviceURI != null) {
					uri = serviceURI.getPath();
				}
			}
			if(uri != null) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "OSB", new String[] {uri});
				InboundRequestWrapper wrapper = new InboundRequestWrapper(headers);
				ExternalParameters params = HttpParameters.library("OSB").uri(serviceURI).procedure("dispatch").inboundHeaders(wrapper).build();
				NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
			}
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error setting dispatch transaction name");
		}
		Weaver.callOriginal();
	}
}
