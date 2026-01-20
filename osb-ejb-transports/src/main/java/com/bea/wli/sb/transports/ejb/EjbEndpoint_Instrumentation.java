package com.bea.wli.sb.transports.ejb;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.osb.ejb.EBJUtils;
import com.newrelic.instrumentation.osb.ejb.NRSendListener;

@Weave(originalName = "com.bea.wli.sb.transports.ejb.EjbEndpoint")
public class EjbEndpoint_Instrumentation {

    @Trace
    public void sendMessageAsync(TransportSender sender, TransportSendListener listener, TransportOptions options) {
        NRSendListener wrapper = EBJUtils.getNRSendListener(listener,options.getURI(),"sendMessageAsync");
        if(wrapper != null) {
            listener = wrapper;
        }
        Weaver.callOriginal();
    }
}
