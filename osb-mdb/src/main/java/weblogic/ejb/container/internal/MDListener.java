package weblogic.ejb.container.internal;

import javax.jms.Message;

import weblogic.ejb.container.interfaces.MessageDrivenBeanInfo;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

/**
 * Weave module for MDListener
 *
 * @see  weblogic.ejb.container.internal.MDListener
 */
@Weave
final class MDListener {

	private final MessageDrivenBeanInfo info = Weaver.callOriginal();

	/**
	 * transactionalOnMessage wrapper, starting a NR transaction and setting the transaction name.
	 *
	 * NR Trace:  Start of a transaction
	 *
	 * @param message   Unused, passed to original
	 * @param doCommit  Unused, passed to original
	 * @return          Original call
	 */
	@Trace(dispatcher=true)
	boolean transactionalOnMessage(Message message, boolean doCommit) {
		if(info != null) {
			NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true,"Message", info.getDestinationName(), "Consume");
		}
		return Weaver.callOriginal();
	}
}
