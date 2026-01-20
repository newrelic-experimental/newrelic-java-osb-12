package com.newrelic.instrumentation.osb.bpel.transports;

import com.bea.wli.sb.transports.bpel10g.model.URIObject;

import java.util.Map;

public class BPELUtils {

    public static void addAttribute(Map<String, Object> attributes, String key, Object value) {
        if(value != null && attributes != null && key != null && !key.isEmpty()) {
            attributes.put(key, value);
        }
    }

    public static void addURIObject(Map<String, Object> attributes, URIObject uriObj) {
        if(uriObj != null) {
            addAttribute(attributes, "Authority", uriObj.getAuthority());
            addAttribute(attributes, "Domain", uriObj.getDomain());
            addAttribute(attributes, "PartnerLink", uriObj.getPartnerLink());
            addAttribute(attributes, "PartnerRole", uriObj.getPartnerRole());
            addAttribute(attributes, "Process", uriObj.getProcess());
            addAttribute(attributes, "Protocol", uriObj.getProtocol());
            addAttribute(attributes, "ProviderPath", uriObj.getProviderPath());
        }
    }
}
