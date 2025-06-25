package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.service.ProxyService;
import com.bea.wli.sb.services.dispatcher.callback.CallbackContext;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.bea.wli.sb.services.dispatcher.message.FaultMessage;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Segment;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.labs.osb.transports.NRUtils;

@Weave(originalName = "com.bea.wli.sb.transports.TransportDispatcherClient")
public abstract class TransportDispatcherClient_Instrumentation {
	
	@Trace
	public void dispatch(ProxyService service, InboundTransportMessageContext_Instrumentation ctx, TransportOptions options) {
		Weaver.callOriginal();
	}
	
	@SuppressWarnings("unused")
	private DispatchCallback createDispatchCallback(InboundTransportMessageContext_Instrumentation tpCtx, ProxyService service)
	{
		String uri = null;
		URI serviceURI = null;
		if(service != null) {
			serviceURI = service.getServiceURI();
			if(serviceURI != null) {
				uri = serviceURI.getPath();
			}
		}
		if(uri == null) {
			serviceURI = tpCtx.getURI();
			if(serviceURI != null) {
				uri = serviceURI.getPath();
			}
		}
		if(serviceURI != null) {
			String host = serviceURI.getHost();
			String schema = serviceURI.getScheme();
			boolean update = false;
			if(schema == null || schema.isEmpty()) {
				schema = "osb";
				update = true;
			}
			if(host == null || host.isEmpty()) {
				host = "osbcluster";
				update = true;
			}
			if(update) {
				String uriString = schema + "://" + host + "/" + uri;
				serviceURI = URI.create(uriString);
			}
		}
		ExternalParameters params = null;
		if(uri != null) {
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "OSB", new String[] {uri});
		}
		if(serviceURI != null) {
			params = GenericParameters.library("OSBDispatch").uri(serviceURI).procedure("dispatch").build();
		}
		
		DispatchCallback callback = Weaver.callOriginal();
		if(CallbackAdapter_Instrumentation.class.isInstance(callback)) {
			CallbackAdapter_Instrumentation adapter = (CallbackAdapter_Instrumentation)callback;
			Segment segment = NewRelic.getAgent().getTransaction().startSegment("TransportDispatch");
			if(params != null) {
				NewRelic.getAgent().getLogger().log(Level.FINE,  "Using URI {0} and procedure {1} to report segment as external", ((GenericParameters)params).getUri(),  ((GenericParameters)params).getProcedure());
				segment.reportAsExternal(params);
			}
			adapter.startTime = System.currentTimeMillis();
			adapter.segment = segment;
			NewRelic.getAgent().getLogger().log(Level.FINE, new Exception("call to TransportDispatcherClient.createDispatchCallback"), "Set startTime to {0} and segment to {1}", adapter.startTime, adapter.segment);
		}
		return callback;
	}

	
	
	@Weave(originalName = "com.bea.wli.sb.transports.TransportDispatcherClient$CallbackAdapter")
	private static class CallbackAdapter_Instrumentation {

		@NewField
		protected long startTime;
		
		@NewField
		protected Segment segment = null;

		private final ProxyService _service = Weaver.callOriginal();
		
		@SuppressWarnings("unused")
		public CallbackAdapter_Instrumentation(InboundTransportMessageContext delegate, ProxyService service) {
			
		}
		
		@SuppressWarnings("unused")
		public void handleFault(CallbackContext<FaultMessage> context) {
			if(segment != null) {
				segment.end();
				segment = null;
			}
			FaultMessage faultMsg = context.getResponse();
			if(faultMsg != null) {
				NewRelic.noticeError("FaultMessage: Error Code: " + faultMsg.getErrorCode() + ", Error Message: " + faultMsg.getErrorMessage());
			}
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
			}
		}

		@Trace(async=true)
		public void handleResponse(CallbackContext<ResponseMessage> context) {
			if(segment != null) {
				segment.end();
				segment = null;
			}
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
			}

		}
	}

}
