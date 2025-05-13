package com.bea.wli.sb.pipeline;

import java.util.HashMap;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.pipeline.components.RuntimeComponent;
import com.bea.wli.sb.services.dispatcher.DispatchOptions;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.NRUtils;

@Weave
public abstract class PipelineContextImpl {

	public abstract String getPipelineName();
	public abstract String getNodeName();
	
	@Trace
	public boolean dispatch(MessageContext context, DispatchCallback callback, DispatchOptions options) {

		HashMap<String,Object> attributes = new HashMap<String, Object>();
		NRUtils.addAttribute(attributes, "PipelineName", getPipelineName());
		NRUtils.addAttribute(attributes, "NodeName", getNodeName());
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatch"});
		return Weaver.callOriginal();
	}

	@Trace
	public boolean dispatchSync(MessageContext context, PipelineContext.SyncDispatchResponseHandler handler) {
		HashMap<String,Object> attributes = new HashMap<String, Object>();
		NRUtils.addAttribute(attributes, "PipelineName", getPipelineName());
		NRUtils.addAttribute(attributes, "NodeName", getNodeName());
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatchSync"});
		return Weaver.callOriginal();
	}

	@Trace
	public boolean execute(RuntimeComponent comp, MessageContext context) {
		HashMap<String,Object> attributes = new HashMap<String, Object>();
		NRUtils.addAttribute(attributes, "PipelineName", getPipelineName());
		NRUtils.addAttribute(attributes, "NodeName", getNodeName());
		if(comp != null) {
			NRUtils.addAttribute(attributes, "RuntimeComponent", comp.getClass().getSimpleName());
		}
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","OSB","PipelineContext","execute"});
		return Weaver.callOriginal();
	}
}
