package com.nr.instrumentation.osb.http;

import javax.servlet.http.HttpServletRequest;

import weblogic.servlet.FutureServletResponse;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave module for HttpTransportServlet
 *
 * The difference between this and HttpTransportServletBase_instrumentation is in the type of the response. This
 * class uses the weblogic FutureServletResponse, not HttpServletResponse.
 *
 * @see  com.bea.wli.sb.transports.http.wls.HttpTransportServlet
 * @see  com.nr.instrumentation.osb.http.HttpTransportServletBase_instrumentation
 */
@Weave(originalName="com.bea.wli.sb.transports.http.wls.HttpTransportServlet")
public abstract class HttpTransportServlet_instrumentation {

	private Ref _serviceRef = Weaver.callOriginal();

	/**
	 * service intercepts the service call from the original and adds transaction data if we have the _serviceRef
	 *
	 * Sets Metric name, Transaction Name
	 *
	 * NR Trace: No options
	 *
	 * @param request   Unused, passed to original
	 * @param response  Unused, passed to original
	 */
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
