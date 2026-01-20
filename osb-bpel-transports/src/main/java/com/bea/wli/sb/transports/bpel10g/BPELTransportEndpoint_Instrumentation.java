package com.bea.wli.sb.transports.bpel10g;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.bea.wli.sb.transports.TransportSender;
import com.bea.wli.sb.transports.bpel10g.model.URIObject;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.instrumentation.osb.bpel.transports.BPELUtils;
import com.newrelic.instrumentation.osb.bpel.transports.NRBPELRunnable;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Weave(originalName = "com.bea.wli.sb.transports.bpel10g.BPELTransportEndpoint")
public class BPELTransportEndpoint_Instrumentation {

    public Runnable sendMessageAsync(TransportSender sender, final TransportSendListener listener, TransportOptions options, URIObject uriObj) {
        Map<String,Object> attributes = new HashMap<>();
        BPELUtils.addURIObject(attributes, uriObj);
        URI uri = options.getURI();
        if (options != null) {
            String mode = options.getMode() != null ? options.getMode().name() : null;
            if (mode != null) {
                attributes.put("Mode", mode);
            }
            String operation = options.getOperationName();
            if (operation != null) {
                attributes.put("Operation", operation);
            }
            attributes.put("URI", uri.toASCIIString());
        }
        GenericParameters genericParameters = GenericParameters.library("OSB-BPEL").uri(uri).procedure("sendMessageAsync").build();
        Runnable actual = Weaver.callOriginal();

        return new NRBPELRunnable(actual, genericParameters);
    }
}
