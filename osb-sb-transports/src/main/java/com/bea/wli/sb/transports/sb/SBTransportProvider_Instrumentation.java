package com.bea.wli.sb.transports.sb;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.osb.sb.SBUtils;
import com.newrelic.instrumentation.osb.sb.NRSendListener;

@Weave(originalName = "com.bea.wli.sb.transports.sb.SBTransportProvider")
public class SBTransportProvider_Instrumentation {

    @Trace
    public void sendMessageAsync(TransportSender sender, TransportSendListener listener, TransportOptions options) {
        NRSendListener wrapper = SBUtils.getNRSendListener(listener,options.getURI(),"sendMessageAsync");
        if(wrapper != null) {
            listener = wrapper;
        }
        Weaver.callOriginal();
    }
}
