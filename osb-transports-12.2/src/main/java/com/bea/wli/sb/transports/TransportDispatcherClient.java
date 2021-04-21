package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.service.ProxyService;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.NRUtils;
import com.bea.wli.sb.services.dispatcher.callback.CallbackContext;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;

@Weave
public abstract class TransportDispatcherClient {
	
	@Trace
	public void dispatch(ProxyService service, InboundTransportMessageContext ctx, TransportOptions options) {
		try {
			// Set the transaction name to something like "Message/incoming.host.com/Consume"
			if(ctx.token == null) {
				ctx.token = NewRelic.getAgent().getTransaction().getToken();
			} else {
				ctx.token.link();
			}
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
				ExternalParameters params = GenericParameters.library("OSBDispatch").uri(serviceURI).procedure("dispatch").build();
				NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
			}
		} catch (Exception e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error setting dispatch transaction name");
		}
		Weaver.callOriginal();
	}
	
	@SuppressWarnings("unused")
	private DispatchCallback createDispatchCallback(InboundTransportMessageContext tpCtx, ProxyService service)
	{
		DispatchCallback callback = Weaver.callOriginal();
		if(CallbackAdapter.class.isInstance(callback)) {
			CallbackAdapter adapter = (CallbackAdapter)callback;
			adapter.startTime = System.currentTimeMillis();
		}
		return callback;
	}

	
	
	@Weave
	private static class CallbackAdapter {

		@NewField
		protected long startTime;

		private final ProxyService _service = Weaver.callOriginal();

		@Trace(async=true)
		public void handleResponse(CallbackContext<ResponseMessage> context) {
			Weaver.callOriginal();
			if(startTime > 0L) {
				long endTime = System.currentTimeMillis();
				long responseTime = endTime - startTime;
				String name = null;
				if(_service != null) {
					Ref ref = _service.getInvokeRef();
					if(ref == null) {
						ref = _service.getRef();
					}
					if(ref == null) {
						ref = _service.getServiceProvider();
					}
					if(ref == null) {
						ref = _service.getWsdlRef();
					}
					if (ref != null) {
						name = ref.getFullName();
					}
				}

				if(name == null) {
					name = "UnknownProxy";
				}
				NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric("Custom/OSB/ProxyService/"+name, responseTime);
				Map<String, Object> eventMap = new HashMap<String, Object>();
				eventMap.put("Name", name);
				eventMap.put("Response Time", responseTime);
				eventMap.put("Managed Server", NRUtils.getManagedServer());
				NewRelic.getAgent().getInsights().recordCustomEvent("ProxyService", eventMap);
				NewRelic.getAgent().getLogger().log(Level.FINE, "Recorded ProxyService Event and Metric for {0}", new Object[]{name});
			}

		}
	}

}
