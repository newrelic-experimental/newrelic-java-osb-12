package com.bea.wli.sb.pipeline;

import java.util.logging.Level;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.pipeline.components.RuntimeComponent;
import com.bea.wli.sb.services.dispatcher.DispatchOptions;
import com.bea.wli.sb.services.dispatcher.callback.DispatchCallback;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class PipelineContextImpl {

	public abstract String getPipelineName();
	public abstract String getNodeName();
	
	@Trace(dispatcher=true)
	public boolean dispatch(MessageContext context, DispatchCallback callback, DispatchOptions options) {
		
//		if(callback != null) {
//			if(callback.token == null) {
//				callback.token = NewRelic.getAgent().getTransaction().getToken();
//			} else {
//				callback.token.link();
//			}
//		}
		String pipelineName = getPipelineName();
		if(pipelineName != null) {
			NewRelic.addCustomParameter("Pipeline", pipelineName);
		}
		String nodeName = getNodeName();
		
		NewRelic.getAgent().getLogger().log(Level.FINE, "dispatch - Pipeline - {0}, Node - {1}", new Object[] {pipelineName,getNodeName()});
		if(nodeName != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatch",nodeName});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatch","Unnamed Node"});
		}
		NewRelic.getAgent().getTracedMethod().addRollupMetricName(new String[] {"Custom","OSB","PipelineContext","dispatch"});
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public boolean dispatchSync(MessageContext context, PipelineContext.SyncDispatchResponseHandler handler) {
		String pipelineName = getPipelineName();
		if(pipelineName != null) {
			NewRelic.addCustomParameter("Pipeline", pipelineName);
		}
		String nodeName = getNodeName();
		NewRelic.getAgent().getLogger().log(Level.FINE, "dispatchSync - Pipeline - {0}, Node - {1}", new Object[] {pipelineName,getNodeName()});
		if(nodeName != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatchSync",nodeName});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","dispatchSync","Unnamed Node"});
		}
		NewRelic.getAgent().getTracedMethod().addRollupMetricName(new String[] {"Custom","OSB","PipelineContext","dispatchSync"});
		return Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public boolean execute(RuntimeComponent comp, MessageContext context) {
		String pipelineName = getPipelineName();
		if(pipelineName != null) {
			NewRelic.addCustomParameter("Pipeline", pipelineName);
		}
		String nodeName = getNodeName();
		String compName = comp.getName();
		if(compName == null) {
			compName = "Unnamed RuntimeComponent";
		}
		NewRelic.getAgent().getLogger().log(Level.FINE, "execute - Pipeline - {0}, Node - {1}, runtime - {2}", new Object[] {pipelineName,getNodeName(),compName});
		if(nodeName != null) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","execute",nodeName,compName});
			NewRelic.getAgent().getTracedMethod().addRollupMetricName(new String[] {"Custom","OSB","PipelineContext","execute",nodeName});
		} else {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","PipelineContext","execute","Unnamed Node",compName});
			NewRelic.getAgent().getTracedMethod().addRollupMetricName(new String[] {"Custom","OSB","PipelineContext","execute","Unnamed Node"});
		}
		NewRelic.getAgent().getTracedMethod().addRollupMetricName(new String[] {"Custom","OSB","PipelineContext","execute"});
		return Weaver.callOriginal();
	}
}
