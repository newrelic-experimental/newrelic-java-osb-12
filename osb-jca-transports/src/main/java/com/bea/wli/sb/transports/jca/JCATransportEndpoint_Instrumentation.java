package com.bea.wli.sb.transports.jca;

import com.bea.wli.sb.transports.RequestHeaders;
import com.bea.wli.sb.transports.RequestMetaData;
import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.TransportSendListener;
import com.newrelic.api.agent.GenericParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.bea.wli.config.Ref;
import com.newrelic.instrumentation.osb.jca.transports.JCAHeaders;

import java.net.URI;
import java.util.HashMap;

@Weave(originalName = "com.bea.wli.sb.transports.jca.JCATransportEndpoint")
public class JCATransportEndpoint_Instrumentation {

    @Trace
    public void send(JCAOutboundMessageContext context, TransportSendListener listener) {
        if (context != null) {
            HashMap<String, Object> attributes = new HashMap<>();
            TransportOptions options = context.getOptions();
            if (options != null) {
                String mode = options.getMode() != null ? options.getMode().name() : null;
                if (mode != null) {
                    attributes.put("Mode", mode);
                }
                String operation = options.getOperationName();
                if (operation != null) {
                    attributes.put("Operation", operation);
                }
                URI uri = options.getURI();
                attributes.put("URI", uri.toASCIIString());
                Ref serviceAccount = context.getServiceAccount();
                if(serviceAccount != null) {
                    String fullName = serviceAccount.getFullName();
                    attributes.put("FullName", fullName);
                    String globalName = serviceAccount.getGlobalName();
                    attributes.put("GlobalName", globalName);
                    GenericParameters parameters = GenericParameters.library("OSB-JCA").uri(uri).procedure("send").build();
                    NewRelic.getAgent().getTracedMethod().reportAsExternal(parameters);
                }
            }
            RequestMetaData requestMetadata = context.getRequestMetaData();
            if(requestMetadata != null) {
                RequestHeaders headers = requestMetadata.getHeaders();
                if(headers != null) {
                    JCAHeaders nrheaders = new JCAHeaders(headers);
                    NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(nrheaders);
                }
            }
            String messageId = context.getMessageId();
            if(messageId != null) {
                attributes.put("MessageId", messageId);
            }
            NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
        }

        Weaver.callOriginal();
    }

    @Trace
    private void sendOneWay(JCAOutboundMessageContext context, TransportSendListener listener) {

        Weaver.callOriginal();
    }

    @Trace
    private void sendRequestResponse(JCAOutboundMessageContext context, TransportSendListener listener) {
        Weaver.callOriginal();
    }

    @Weave(originalName = "com.bea.wli.sb.transports.jca.JCATransportEndpoint.JCAResponseTask")
    private static class JCAResponseTask {

        @NewField
        Token   token;

        private final JCAOutboundMessageContext _context = Weaver.callOriginal();

        JCAResponseTask(JCAOutboundMessageContext context, TransportSendListener listener, String code, String msg) {
            Token t = NewRelic.getAgent().getTransaction().getToken();
            if(t != null) {
                if(!t.isActive()) {
                    t.expire();
                    t = null;
                } else {
                    token = t;
                }
            }
        }

        @Trace(async=true)
        public void run() {
            if(token != null) {
                token.linkAndExpire();
                token = null;
            }
            URI uri = _context.getURI();
            if(uri != null) {
                GenericParameters parameters = GenericParameters.library("OSB-JCA").uri(uri).procedure("send").build();
                NewRelic.getAgent().getTracedMethod().reportAsExternal(parameters);
            }
            Weaver.callOriginal();
        }

    }
}
