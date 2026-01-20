package com.newrelic.instrumentation.osb.jejb;

import com.bea.wli.sb.transports.OutboundTransportMessageContext;
import com.bea.wli.sb.transports.TransportSendListener;
import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;

public class NRSendListener implements TransportSendListener {

    private TransportSendListener delegate;
    private Token token;
    private ExternalParameters externalParameters;
    private static boolean isTransformed = false;

    public NRSendListener(TransportSendListener delegate, ExternalParameters externalParameters) {
        this.delegate = delegate;
        this.externalParameters = externalParameters;
        Token t = NewRelic.getAgent().getTransaction().getToken();
        if(t != null) {
            if(t.isActive()) {
                token = t;
            } else {
                t.expire();
                t = null;
            }
        }
        if (!isTransformed) {
            AgentBridge.instrumentation.retransformUninstrumentedClass(getClass());
            isTransformed = true;
        }
    }


    @Override
    @Trace(async=true)
    public void onReceiveResponse(OutboundTransportMessageContext outboundTransportMessageContext) {
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        if(externalParameters != null) {
            NewRelic.getAgent().getTracedMethod().reportAsExternal(externalParameters);
            externalParameters = null;
        }
        delegate.onReceiveResponse(outboundTransportMessageContext);
    }

    @Override
    @Trace(async=true)
    public void onError(OutboundTransportMessageContext outboundTransportMessageContext, String s, String s1) {
        if(token != null) {
            token.linkAndExpire();
            token = null;
        }
        if(externalParameters != null) {
            NewRelic.getAgent().getTracedMethod().reportAsExternal(externalParameters);
            externalParameters = null;
        }
        delegate.onError(outboundTransportMessageContext, s, s1);
    }}
