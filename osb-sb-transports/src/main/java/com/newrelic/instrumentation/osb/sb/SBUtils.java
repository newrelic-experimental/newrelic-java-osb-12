package com.newrelic.instrumentation.osb.sb;

import com.bea.wli.sb.transports.TransportSendListener;
import com.newrelic.api.agent.ExternalParameters;
import com.newrelic.api.agent.GenericParameters;

import java.net.URI;

public class SBUtils {

    public static NRSendListener getNRSendListener(TransportSendListener listener, URI uri, String procedure) {
        if(listener instanceof NRSendListener) {
            return null;
        }
        ExternalParameters externalParameters = null;
        if(uri != null) {
            externalParameters = GenericParameters.library("OSB-JEJB").uri(uri).procedure(procedure).build();
        }
        return new NRSendListener(listener, externalParameters);
    }
}
