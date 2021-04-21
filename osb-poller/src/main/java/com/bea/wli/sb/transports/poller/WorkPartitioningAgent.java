package com.bea.wli.sb.transports.poller;

import java.util.List;

import com.bea.wli.config.Ref;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.newrelic.api.agent.NewRelic;

@Weave(type=MatchType.Interface)
public abstract class WorkPartitioningAgent {
	
	public abstract Ref getServiceRef();

	@Trace(dispatcher=true)
	public List<PublishedTask> execute() {
		Ref serviceRef = getServiceRef();
		if(serviceRef != null) {
			String fullName = serviceRef.getFullName();
			if(fullName != null && !fullName.isEmpty()) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Poller", new String[] {fullName});
			}
		}
		return Weaver.callOriginal();
	}
}
