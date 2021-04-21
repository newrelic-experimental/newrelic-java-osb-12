package com.bea.wli.sb.pipeline.components;

import com.bea.wli.sb.context.MessageContext;
import com.bea.wli.sb.pipeline.InternalPipelineContext;
import com.bea.wli.sb.pipeline.PipelineException;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class RuntimeComponent {

	public abstract String getName();
	
	@Trace(dispatcher=true)
	public boolean processMessage(MessageContext paramMessageContext, InternalPipelineContext paramInternalPipelineContext) throws PipelineException {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","RuntimeComponent",getName()});
		String pipelineName = paramInternalPipelineContext.getPipelineName();
		if(pipelineName == null) {
			pipelineName = "UnknownPipeline";
		}
		String stageName = paramInternalPipelineContext.getStageName();
		if(stageName == null) {
			stageName = "UnknownStage";
		}
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "OSB", new String[] {"RuntimeComponent",getName(),pipelineName,stageName});
		return Weaver.callOriginal();
	}
}