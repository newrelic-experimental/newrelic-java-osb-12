package com.bea.wli.sb.transports.mq;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.bea.wli.config.Ref;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.newrelic.api.agent.NewRelic;

/**
 * Weave module for MQUtil
 *
 * @see  com.bea.wli.sb.transports.mq.MQUtil
 */
@Weave
public abstract class MQUtil {

	/**
	 * Trace sendMessage, set metric name based on queue name.
	 *
	 * @param ref      Unused
	 * @param message  Unused
	 * @param queue    Parsed for queue.name, used in metric naming
	 * @param options  Unused
	 */
	@Trace
	public static void sendMessage(Ref ref, MQMessage message, MQQueue queue, int options)  {
		String queueName = queue.name;
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MQUtil","sendMessage",queueName});
		Weaver.callOriginal();
	}

	/**
	 * Trace sendMessage, set metric name based on queue name.
	 *
	 * @param message     Unused
	 * @param qManager    Unused
	 * @param qName       Parsed for metric naming segment
	 * @param putOptions  Unused
	 * @param remoteQM    Unused
	 */
	@Trace
	public static void sendMessage(MQMessage message, MQQueueManager qManager, String qName, int putOptions, String remoteQM) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","MQUtil","sendMessage",qName});
		Weaver.callOriginal();
	}

}
