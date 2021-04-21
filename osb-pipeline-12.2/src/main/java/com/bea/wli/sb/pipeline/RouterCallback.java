package com.bea.wli.sb.pipeline;

import java.util.HashMap;
import java.util.Map;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.services.dispatcher.callback.CallbackContext;
import com.bea.wli.sb.services.dispatcher.message.FaultMessage;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.NRUtils;

@Weave
public class RouterCallback  {

	@NewField
	protected long startTime = 0L;
	
	private final RouterContext _context = Weaver.callOriginal();

	public RouterCallback(RouterContext context) {
		startTime = System.currentTimeMillis();
	}
	
	public void enableCallback() {
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public void handleFault(CallbackContext<FaultMessage> callbackContext) {
		
		Weaver.callOriginal();		
		if(startTime > 0L) {
			long endTime = System.currentTimeMillis();
			long responseTime = endTime - startTime;
			String name = null;
			if(_context != null) {
				Ref ref = _context.getRef();
				if(ref != null) {
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
	
	public void handleResponse(CallbackContext<ResponseMessage> callbackContext) {
		Weaver.callOriginal();
		if(startTime > 0L) {
			long endTime = System.currentTimeMillis();
			long responseTime = endTime - startTime;
			String name = null;
			if(_context != null) {
				Ref ref = _context.getRef();
				if(ref != null) {
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
