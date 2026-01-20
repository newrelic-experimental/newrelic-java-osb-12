package com.nr.instrumentation.context;

import java.util.Map;

import com.bea.wli.config.Ref;
import com.bea.wli.sb.context.InboundEndpoint;
import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.context.OutboundEndpoint;
import com.bea.wli.sb.context.ServiceInfo;
import com.bea.wli.sb.pipeline.PipelineContext;
import com.bea.wli.sb.pipeline.PipelineException;

public class NRUtils {

	
	private static String managedServer = null;
	
	public static String getManagedServer() {
		if(managedServer == null) {
			managedServer = System.getProperty("weblogic.Name", "UnknownServer");
		}
		return managedServer;
	}
	
	public static void addAttribute(Map<String, Object> attributes, String key, Object value) {
		if(value != null && attributes != null && key != null && !key.isEmpty()) {
			attributes.put(key, value);
		}
	}
	
	
	public static void addPipelineContext(Map<String, Object> attributes, PipelineContext context) {
		if(context != null) {
			addAttribute(attributes,"Pipeline-InstanceId", context.getInstanceId());
			addAttribute(attributes, "PipelineName", context.getPipelineName());
			addAttribute(attributes, "Pipeline-NodeName", context.getNodeName());
			addAttribute(attributes, "Pipeline-StageName", context.getStageName());
			Ref pipelineRef = context.getPipelineRef();
			if(pipelineRef != null) {
				addAttribute(attributes, "PipelineRef", pipelineRef.toString());

			}
		}
	}
	
	public static void addMessageContext(Map<String, Object> attributes, MessageContext context) {
		if(context != null) {
			addAttribute(attributes, "MessageId", context.getMessageId());
			try {
				addAttribute(attributes, "Operation", context.getOperation());
			} catch (PipelineException e) {
			}
			try {
				InboundEndpoint inbound = context.getInbound();
				addInboundEndpoint(attributes, inbound);
			} catch (PipelineException e) {
			}
			try {
				OutboundEndpoint outbound = context.getOutbound();
				addOutboundEndpoint(attributes, outbound);
			} catch (PipelineException e) {
			}
		}
	}
	
	public static void addInboundEndpoint(Map<String, Object> attributes, InboundEndpoint endpoint) {
		if(endpoint != null) {
			ServiceInfo serviceInfo = endpoint.getServiceInfo();
			Ref ref = serviceInfo.getServiceProvider();
			if(ref != null) {
				addAttribute(attributes, "InboundEndpoint-ServiceProvider", ref.toString());
			}
			addAttribute(attributes, "InboundEndpoint-Service-Operation",serviceInfo.getOperation());
		}
	}

	public static void addOutboundEndpoint(Map<String, Object> attributes, OutboundEndpoint endpoint) {
		if(endpoint != null) {
			ServiceInfo serviceInfo = endpoint.getServiceInfo();
			Ref ref = serviceInfo.getServiceProvider();
			if(ref != null) {
				addAttribute(attributes, "OutboundEndpoint-ServiceProvider", ref.toString());
			}
			addAttribute(attributes, "OutboundEndpoint-Service-Operation",serviceInfo.getOperation());
		}
	}

}
