package com.newrelic.instrumentation.osb.bpel.transports;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRBPELRunnable implements Runnable {

    private final Runnable delegate;
    private Token token;
    private static boolean isTransformed = false;
    private ExternalParameters externalParameters;

    public NRBPELRunnable(Runnable delegate, ExternalParameters externalParameters) {
        this.delegate = delegate;
        this.externalParameters = externalParameters;
        if(!isTransformed) {
            isTransformed = true;
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
        }
        Token t = NewRelic.getAgent().getTransaction().getToken();
        if(t != null) {
            if(t.isActive()) {
                token = t;
            } else  {
                t.expire();
                t = null;
            }
        }
    }

    @Trace(async=true)
    public void run() {
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        if(externalParameters != null) {
            NewRelic.getAgent().getTracedMethod().reportAsExternal(externalParameters);
            externalParameters = null;
        }
        delegate.run();
    }
}
