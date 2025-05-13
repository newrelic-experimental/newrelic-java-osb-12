package com.bea.wli.sb.pipeline;

import java.util.HashMap;

import com.bea.wli.sb.context.MessageContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.NRUtils;

@Weave(originalName = "com.bea.wli.sb.pipeline.RuntimeStep", type = MatchType.Interface)
public abstract class RuntimeStep_Instrumentation {

	@Trace(dispatcher = true)
	public boolean processMessage(MessageContext var1, PipelineContext var2) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRUtils.addMessageContext(attributes, var1);
		NRUtils.addPipelineContext(attributes, var2);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		traced.setMetricName("Custom","SOA","Pipeline","RuntimeStep",getClass().getSimpleName(),"processMessage");
		return Weaver.callOriginal();
	}
}
