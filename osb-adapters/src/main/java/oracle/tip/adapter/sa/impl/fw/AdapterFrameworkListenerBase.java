package oracle.tip.adapter.sa.impl.fw;

import javax.resource.cci.Record;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave implementation for AdapterFrameworkListenerBase
 *
 * @see  oracle.tip.adapter.sa.impl.fw.AdapterFrameworkListenerBase
 */
@Weave(type=MatchType.BaseClass)
public abstract class AdapterFrameworkListenerBase {
	public abstract String getServiceName();

	public abstract String getOperationName();

	public abstract String getAdapterName();

	/**
	 * Grab the trace method and set the metric name to reflect the Adapter, Service, and Operation Name, if detected.
	 *
	 * Metric name Format: `Custom/OSB/Adapter/{Adapter_Name}/onMessage/{Service_Name}/{Operation_Name}`
	 *
	 * Service Name / Operation Name are not checked against null, so those could be empty in the metric name.
	 *
	 * @param record  Record
	 * @return        Original call result
	 */
	@Trace(dispatcher=true)
	public Record onMessage(Record record) {
		String adapterName = getAdapterName();
		if(adapterName == null) {
			adapterName = "Unknown_Adapter";
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","OSB","Adapter",adapterName,"onMessage",getServiceName(),getOperationName()});
		return Weaver.callOriginal();
	}
}
