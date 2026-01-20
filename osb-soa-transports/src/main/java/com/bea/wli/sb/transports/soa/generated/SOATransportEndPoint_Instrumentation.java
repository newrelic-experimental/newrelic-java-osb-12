package com.bea.wli.sb.transports.soa.generated;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.osb.soa.NRSendListener;
import com.newrelic.instrumentation.osb.soa.SOAUtils;

@Weave(originalName = "com.bea.wli.sb.transports.soa.generated.SOATransportEndPoint")
public class SOATransportEndPoint_Instrumentation {

    @Trace
    public void sendMessageAsync(TransportSender sender, TransportSendListener listener, TransportOptions options) {
        NRSendListener wrapper = SOAUtils.getNRSendListener(listener,options.getURI(),"sendMessageAsync");
        if(wrapper != null) {
            listener = wrapper;
        }
        Weaver.callOriginal();

    }
}
