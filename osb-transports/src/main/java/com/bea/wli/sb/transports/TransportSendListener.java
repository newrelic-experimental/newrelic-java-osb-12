package com.bea.wli.sb.transports;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.transports.InboundResponseWrapper;
import com.nr.instrumentation.transports.NRUtils;
import com.newrelic.api.agent.NewRelic;

@Weave(type=MatchType.Interface)
public abstract class TransportSendListener {

	@NewField
	Token token = null;

	@NewField
	public long startTime = 0L;
	
	@NewField
	public String serviceName = null;
	

	@SuppressWarnings("rawtypes")
	@Trace(dispatcher=true)
	public void onReceiveResponse(OutboundTransportMessageContext outboundTransportMessageContext) {
		ResponseMetaData responseMetadata = null;
		try {
			responseMetadata = outboundTransportMessageContext.getResponseMetaData();
			
			if (responseMetadata != null) {
				ResponseHeaders headers = responseMetadata.getHeaders();
				URI uri = outboundTransportMessageContext.getURI();
				if(uri == null) {
					uri = URI.create("http://UnknownHost");
				}
				TracedMethod traced = NewRelic.getAgent().getTracedMethod();
				HttpParameters params = HttpParameters.library("OSB").uri(uri).procedure("onReceiveResponse").inboundHeaders(new InboundResponseWrapper(headers)).build();
				traced.reportAsExternal(params);
			}
		} catch (TransportException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error getting inbound headers", new Object[] {});
		}
		Weaver.callOriginal();
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		if(startTime != 0L) {
			long endTime = System.currentTimeMillis();
			long responseTime = endTime - startTime;
			if(serviceName == null) {
				serviceName = "UnknownService";
			}
			String metricName = "Custom/OSB/BusinessService/" + serviceName;
			NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName, responseTime);
			Map<String, Object> eventMap = new HashMap<String, Object>();
			eventMap.put("Service", serviceName);
			eventMap.put("Managed Server", NRUtils.getManagedServer());
			eventMap.put("Response Time", responseTime);
			if (responseMetadata != null) {
				int responseCode = responseMetadata.getResponseCode();
				eventMap.put("Response Code", responseCode);
			}
			String uri = outboundTransportMessageContext.getURI() != null ? outboundTransportMessageContext.getURI().toASCIIString() : "Unknown";
			eventMap.put("URI", uri);
			if(outboundTransportMessageContext.getMessageId() != null) {
				eventMap.put("Message ID", outboundTransportMessageContext.getMessageId());
			}
			NewRelic.getAgent().getInsights().recordCustomEvent("BusinessService", eventMap);
			startTime = 0L;
			serviceName = null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void onError(OutboundTransportMessageContext outboundTransportMessageContext, String errorCode, String errorMessage) {
		try {
			ResponseMetaData responseMetadata = outboundTransportMessageContext.getResponseMetaData();
			
			if (responseMetadata != null) {
				ResponseHeaders headers = responseMetadata.getHeaders();
				URI uri = outboundTransportMessageContext.getURI();
				if(uri == null) {
					uri = URI.create("http://UnknownHost");
				}
				TracedMethod traced = NewRelic.getAgent().getTracedMethod();
				HttpParameters params = HttpParameters.library("OSB").uri(uri).procedure("onError").inboundHeaders(new InboundResponseWrapper(headers)).build();
				traced.reportAsExternal(params);
			}
		} catch (TransportException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error getting inbound headers", new Object[] {});
		}
		Weaver.callOriginal();
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("Service", serviceName);
		
		errorMap.put("Managed Server", NRUtils.getManagedServer());
		errorMap.put("Error Code", errorCode);
		String uri = outboundTransportMessageContext.getURI() != null ? outboundTransportMessageContext.getURI().toASCIIString() : "Unknown";
		errorMap.put("URI", uri);
		if(outboundTransportMessageContext.getMessageId() != null) {
			errorMap.put("Message ID", outboundTransportMessageContext.getMessageId());
		}
		NewRelic.noticeError(errorMessage, errorMap);
		if(startTime != 0L) {
			long endTime = System.currentTimeMillis();
			long responseTime = endTime - startTime;
			if(serviceName == null) {
				serviceName = "UnknownService";
			}
			String metricName = "Custom/OSB/BusinessService/" + serviceName;
			NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName, responseTime);
			Map<String,Object> eventMap = new HashMap<String, Object>(errorMap);
			eventMap.put("Response Time", responseTime);
			NewRelic.getAgent().getInsights().recordCustomEvent("BusinessService", eventMap);
			startTime = 0L;
			serviceName = null;
		}
	}

}
