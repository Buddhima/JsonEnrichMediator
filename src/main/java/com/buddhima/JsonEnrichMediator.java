package com.buddhima;

import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;

/**
 * Syntax for JsonEnrichMediator
 *
 * <enrich>
 *  <source [clone=true|false] [type=custom|payload|property|inline] JSONPath="" property="" key="" />
 *  <target [type=custom|payload|property] [action=set|add|put] JSONPath="" property="" />
 * </enrich>
 *
 */
public class JsonEnrichMediator extends AbstractMediator  implements ManagedLifecycle {

	// constants
	public static final int CUSTOM = 0;
	public static final int PAYLOAD = 1;
	public static final int PROPERTY = 2;
	public static final int INLINE = 3;

	private JsonSource source = null;
	private JsonTarget target = null;

	public boolean mediate(MessageContext context) {

		SynapseLog synLog = getLog(context);

		try {

			Object sourceNode;

			sourceNode = source.evaluate(context, synLog);
			if (sourceNode == null) {
				handleException("Failed to get the source for Enriching : ", context);
			} else {
				target.insert(context, sourceNode, synLog);
			}
		} catch (Exception e) {
			handleException("Failed to get the source for Enriching", e, context);
		}

		return true;
	}

	/**
	 * Finds the integer value of type
	 *
	 * @param type String value of type
	 *
	 * @return Integer value of type
	 */
	static int findType(String type) {
		if (type.equalsIgnoreCase("custom")) {
			return JsonEnrichMediator.CUSTOM;
		} else if (type.equalsIgnoreCase("payload")) {
			return JsonEnrichMediator.PAYLOAD;
		} else if (type.equalsIgnoreCase("property")) {
			return JsonEnrichMediator.PROPERTY;
		} else if (type.equalsIgnoreCase("inline")) {
			return JsonEnrichMediator.INLINE;
		} else {
			return -1;
		}
	}

	/**
	 * Finds the string value of type
	 *
	 * @param typeNo Integer value of type
	 *
	 * @return String value of type
	 */
	static String findTypeString(int typeNo) {
		String typeString = "";
		switch (typeNo) {
			case JsonEnrichMediator.CUSTOM: {
				typeString = "custom";
				break;
			}
			case JsonEnrichMediator.PAYLOAD: {
				typeString = "payload";
				break;
			}
			case JsonEnrichMediator.PROPERTY: {
				typeString = "property";
				break;
			}
			case JsonEnrichMediator.INLINE: {
				typeString = "inline";
			}
		}

		return typeString;
	}


	/**
	 * Getters and Setters
	 */

	public JsonSource getSource() {
		return source;
	}

	public void setSource(JsonSource source) {
		this.source = source;
	}

	public JsonTarget getTarget() {
		return target;
	}

	public void setTarget(JsonTarget target) {
		this.target = target;
	}

	public void init(SynapseEnvironment synapseEnvironment) {

	}

	public void destroy() {

	}
}
