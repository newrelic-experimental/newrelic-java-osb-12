package com.bea.wli.sb.pipeline.components;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.pipeline.InternalPipelineContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "com.bea.wli.sb.pipeline.components.Node", type = MatchType.BaseClass)
public abstract class Node_Instrumentation {

	public abstract String getName();

	@Trace
	protected Node.NodeRuntime doRequest(MessageContext context, InternalPipelineContext pipelineContext) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.setMetricName("Custom","OSB","Node",getClass().getSimpleName(),"doRequest");
		return Weaver.callOriginal();
	}

	@Trace
	protected Node.NodeRuntime doResponse(MessageContext context, InternalPipelineContext pipelineContext) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.setMetricName("Custom","OSB","Node",getClass().getSimpleName(),"doResponse");
		return Weaver.callOriginal();
	}

}
