package com.bea.wli.sb.service.handlerchain;

import com.bea.wli.sb.services.dispatcher.DispatchContext;
import com.bea.wli.sb.services.dispatcher.DispatchException;
import com.bea.wli.sb.services.dispatcher.callback.CallbackContext;
import com.bea.wli.sb.services.dispatcher.message.FaultMessage;
import com.bea.wli.sb.services.dispatcher.message.ResponseMessage;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type = MatchType.Interface, originalName = "com.bea.wli.sb.service.handlerchain.Handler")
public class Handler_Instrumentation {

    @Trace
    public void dispatch(DispatchContext ctx) {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Handler",getClass().getSimpleName(),"dispatch");
        Weaver.callOriginal();
    }

    @Trace
    public void handleResponse(DispatchContext ctx, CallbackContext<ResponseMessage> callbackContext) {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Handler",getClass().getSimpleName(),"handleResponse");
        Weaver.callOriginal();
    }

    @Trace
    public void handleFault(DispatchContext ctx, CallbackContext<FaultMessage> callbackContext) {
        NewRelic.getAgent().getTracedMethod().setMetricName("Custom","Handler",getClass().getSimpleName(),"handleFault");
        Weaver.callOriginal();
    }

}
