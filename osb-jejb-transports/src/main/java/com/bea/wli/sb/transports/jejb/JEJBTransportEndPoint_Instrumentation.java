package com.bea.wli.sb.transports.jejb;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.osb.jejb.EBJUtils;
import com.newrelic.instrumentation.osb.jejb.NRSendListener;

@Weave(originalName = "com.bea.wli.sb.transports.jejb.JEJBTransportEndPoint")
public class JEJBTransportEndPoint_Instrumentation {

    public void send(TransportSender sender, TransportSendListener listener, TransportOptions options) {
        NRSendListener wrapper = EBJUtils.getNRSendListener(listener,options.getURI(),"sendMessageAsync");
        if(wrapper != null) {
            listener = wrapper;
        }
        Weaver.callOriginal();

    }
}
