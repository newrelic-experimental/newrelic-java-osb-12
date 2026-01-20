package com.bea.wli.sb.pipeline.components;

import java.util.HashMap;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.pipeline.InternalPipelineContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.context.NRUtils;

@Weave(originalName = "com.bea.wli.sb.pipeline.components.Router")
public class Router_Instrumentation {

	private final String _name = Weaver.callOriginal();
	private final Node _flow = Weaver.callOriginal();

	@Trace
	public boolean processMessage(MessageContext context, InternalPipelineContext pipelineContext) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRUtils.addMessageContext(attributes, context);
		NRUtils.addPipelineContext(attributes, pipelineContext);
		NRUtils.addAttribute(attributes, "Router-Name", _name);
		if(_flow != null) {
			NRUtils.addAttribute(attributes, "Router-Node", _flow.getName());
		}
		
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		return Weaver.callOriginal();
		
	}
}
