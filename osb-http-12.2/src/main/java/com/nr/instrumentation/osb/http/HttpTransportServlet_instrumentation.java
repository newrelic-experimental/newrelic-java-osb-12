package com.nr.instrumentation.osb.http;

import javax.servlet.http.HttpServletRequest;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

import weblogic.servlet.FutureServletResponse;

@Weave(originalName="com.bea.wli.sb.transports.http.wls.HttpTransportServlet")
public abstract class HttpTransportServlet_instrumentation {

	private Ref _serviceRef = Weaver.callOriginal();
	
	@Trace
	public void service(HttpServletRequest request, FutureServletResponse response) {
		if(_serviceRef != null) {
			String service = _serviceRef.getFullName();
			if(service != null && !service.isEmpty()) {
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","HttpTransportServlet",service});
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "OSBService", new String[] {service});
			}
		}
		Weaver.callOriginal();
	}
}
