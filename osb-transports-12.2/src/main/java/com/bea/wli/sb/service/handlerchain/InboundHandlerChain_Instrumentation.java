package com.bea.wli.sb.service.handlerchain;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface, originalName = "com.bea.wli.sb.service.handlerchain.InboundHandlerChain")
public class InboundHandlerChain_Instrumentation {

    public void dispatch(DispatchContext ctx) {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","InboundHandlerChain",getClass().getSimpleName(),"dispatch");
        Weaver.callOriginal();
    }
}
