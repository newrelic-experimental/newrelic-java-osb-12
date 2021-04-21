package oracle.tip.adapter.fw.jca.messageinflow;

import javax.resource.cci.Record;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave module for MessageEndpointImpl
 *
 * @see  oracle.tip.adapter.fw.jca.messageinflow.MessageEndpointImpl
 */
@Weave
public abstract class MessageEndpointImpl {

	/**
	 * onMessage wraps the original onMessage call and adds the message Record Name as an attribute.
	 *
	 * NR Trace:  Start of a transaction
	 *
	 * @param message  Parsed to get the Record Name
	 * @return         Original call
	 */
	@Trace(dispatcher=true)
	public Record onMessage(Record message) {
		NewRelic.addCustomParameter("Record Name", message.getRecordName());
		return Weaver.callOriginal();
	}
}
