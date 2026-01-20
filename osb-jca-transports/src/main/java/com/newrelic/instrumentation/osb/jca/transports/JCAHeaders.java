package com.newrelic.instrumentation.osb.jca.transports;

import com.bea.wli.sb.transports.RequestHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;

import java.util.*;

public class JCAHeaders implements Headers {

    private final RequestHeaders requestHeaders;

    public JCAHeaders(RequestHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    @Override
    public HeaderType getHeaderType() {
        return HeaderType.MESSAGE;
    }

    @Override
    public String getHeader(String name) {
        Object header = requestHeaders.getHeader(name);
        if(header == null) {
            return null;
        }
        return header.toString();
    }

    @Override
    public Collection<String> getHeaders(String name) {
        List<String> headers = new ArrayList<>();
        String header = getHeader(name);
        if(header != null) {
            headers.add(header);
        }
        return headers;
    }

    @Override
    public void setHeader(String name, String value) {
        if(requestHeaders != null) {
            requestHeaders.setHeader(name, value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        if(requestHeaders != null) {
            requestHeaders.setHeader(name, value);
        }
    }

    @Override
    public Collection<String> getHeaderNames() {
        if (requestHeaders != null) {
            List<String> headers = new ArrayList<>();
            Iterator<String> namesIterator = requestHeaders.getHeaderNames();
            while (namesIterator.hasNext()) {
                headers.add(namesIterator.next());
            }
            return headers;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean containsHeader(String name) {
        return getHeaderNames().contains(name);
    }
}
