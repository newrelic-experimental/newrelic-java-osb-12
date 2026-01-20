package com.bea.wli.sb.services.dispatcher;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.bea.wli.config.Ref;

@Weave(originalName = "com.bea.wli.sb.services.dispatcher.Dispatcher", type = MatchType.Interface)
public abstract class Dispatcher_Instrumentation {

	
	public abstract Ref getRef();
	
	@Trace(dispatcher = true)
	public void dispatch(DispatchContext ctx) {
		NewRelic.getAgent().getTracedMethod().setMetricName("Custom","OSB","Dispatcher",getClass().getSimpleName(),"dispatch");
		Ref ref = getRef();
		if(ref != null) {
			NewRelic.getAgent().getTracedMethod().addCustomAttribute("Ref", ref.toString());
		}
		Weaver.callOriginal();
	}
}
