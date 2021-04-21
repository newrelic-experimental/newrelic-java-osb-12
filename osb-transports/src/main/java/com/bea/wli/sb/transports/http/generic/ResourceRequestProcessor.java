package com.bea.wli.sb.transports.http.generic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weblogic.security.acl.internal.AuthenticatedSubject;

import com.bea.wli.sb.transports.TransportOptions;
import com.bea.wli.sb.transports.http.HttpInboundMessageContext;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class ResourceRequestProcessor {

	@Trace
	public boolean process(HttpServletRequest request, HttpServletResponse response, AuthenticatedSubject subject, HttpInboundMessageContext ctx, TransportOptions options) {
		return Weaver.callOriginal();
	}
}
