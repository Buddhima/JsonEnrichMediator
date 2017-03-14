package com.buddhima;

import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.mediators.AbstractMediator;

public class JsonEnrichMediator extends AbstractMediator {

	// constants
	public static final int CUSTOM = 0;
	public static final int PAYLOAD = 1;
	public static final int PROPERTY = 2;
	public static final int INLINE = 3;

	private String sourceClone;
	private String sourceType;
	private String sourceJSONPath;
	private String sourceProperty;
	private String sourceInlineJSONNode;

	private String targetAction;
	private String targetJSONPath;
	private String targetProperty;
	private String targetType;

	private JsonSource source = null;
	private JsonTarget target = null;

	public boolean mediate(MessageContext context) {

		SynapseLog synLog = getLog(context);

		try {

			initialize();

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

	private void initialize() {
		try {
			if (source == null) {
				source = new JsonSource(sourceJSONPath, sourceProperty, sourceType, sourceClone, sourceInlineJSONNode);
			}
			if (target == null) {
				target = new JsonTarget(targetJSONPath, targetProperty, targetType, targetAction);
			}
		} catch (Exception ex) {
			log.error("Failed to initialize source and/or target");
			throw new SynapseException("Failed to initialize source and/or target", ex);
		}
	}

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
	 * Getters and Setters
	 */
	public String getSourceClone() {
		return sourceClone;
	}

	public void setSourceClone(String sourceClone) {
		this.sourceClone = sourceClone;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSourceJSONPath() {
		return sourceJSONPath;
	}

	public void setSourceJSONPath(String sourceJSONPath) {
		this.sourceJSONPath = sourceJSONPath;
	}

	public String getSourceProperty() {
		return sourceProperty;
	}

	public void setSourceProperty(String sourceProperty) {
		this.sourceProperty = sourceProperty;
	}

	public String getSourceInlineJSONNode() {
		return sourceInlineJSONNode;
	}

	public void setSourceInlineJSONNode(String sourceInlineJSONNode) {
		this.sourceInlineJSONNode = sourceInlineJSONNode;
	}

	public String getTargetAction() {
		return targetAction;
	}

	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}

	public String getTargetJSONPath() {
		return targetJSONPath;
	}

	public void setTargetJSONPath(String targetJSONPath) {
		this.targetJSONPath = targetJSONPath;
	}

	public String getTargetProperty() {
		return targetProperty;
	}

	public void setTargetProperty(String targetProperty) {
		this.targetProperty = targetProperty;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
}
