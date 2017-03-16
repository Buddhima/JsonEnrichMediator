package com.buddhima;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;

public class JsonSource {

    private String jsonPath = null;
    private String property = null;
    private int sourceType = JsonEnrichMediator.CUSTOM;
    private boolean clone = true;
    private String inlineJSONNode = null;
    private String inlineKey = null;

    private Configuration configuration = Configuration.defaultConfiguration();

    public JsonSource() {}

    public JsonSource(String jsonPath, String property, String sourceType, String clone, String inlineJSONNode) throws Exception {
        this.jsonPath = jsonPath;
        this.property = property;
        this.sourceType = (sourceType != null) ? JsonEnrichMediator.findType(sourceType) : JsonEnrichMediator.CUSTOM;
        this.clone = clone == null || Boolean.parseBoolean(clone);
        this.inlineJSONNode = inlineJSONNode;
    }

    public Object evaluate(MessageContext synapseContext, SynapseLog synLog) {
        Object object = "";

        org.apache.axis2.context.MessageContext context = ((Axis2MessageContext) synapseContext).getAxis2MessageContext();

        if(!JsonUtil.hasAJsonPayload(context)) {
            synLog.error("JSON payload not found in message context");
        }

        switch (sourceType) {

            case JsonEnrichMediator.CUSTOM : {
                assert jsonPath != null : "JSONPath should be non null in case of CUSTOM";

                String jsonString = JsonUtil.jsonPayloadToString(context);

                object = JsonPath.using(configuration).parse(jsonString).read(jsonPath);

                if (!clone) {

                    // when cloning is false, remove the element in JSON path from payload
                    String modifiedJsonString = JsonPath.using(configuration).parse(jsonString).delete(jsonPath).jsonString();
                    try {
                        JsonUtil.getNewJsonPayload(context, modifiedJsonString, true, true);
                    } catch (Exception ex) {
                        synLog.error("Error while setting json payload, when cloning is false");
                    }
                }

                break;
            }
            case JsonEnrichMediator.PAYLOAD: {

                object = JsonUtil.jsonPayloadToString(context);

                if (!clone)
                    JsonUtil.removeJsonPayload(context);

                break;
            }
            case JsonEnrichMediator.INLINE: {

                assert inlineJSONNode != null || inlineKey != null : "inlineJSONNode or key shouldn't be null when type is INLINE";

                if (inlineJSONNode != null && !inlineJSONNode.trim().equals("")) {
                    object = JsonPath.using(configuration).parse(inlineJSONNode).json();
                } else if (inlineKey != null && !inlineKey.trim().equals("")) {

                    Object inlineObj = synapseContext.getEntry(inlineKey);

                    if ((inlineObj instanceof String) && !(((String) inlineObj).trim().equals(""))) {
                        object = JsonPath.using(configuration).parse(((String) inlineObj)).json();
                    } else {
                        synLog.error("Source failed to get inline JSON" + "inlineKey=" + inlineKey);
                    }
                } else {
                    synLog.error("Source failed to get inline JSON" + "inlineJSONNode=" + inlineJSONNode + ", inlineKey=" + inlineKey);
                }

                break;
            }
            case JsonEnrichMediator.PROPERTY: {

                assert property != null : "property shouldn't be null when type is PROPERTY";

                Object o = synapseContext.getProperty(property);

                if (o instanceof String) {
                    String sourceStr = (String) o;

                    object = sourceStr;
                } else {
                    synLog.error("Invalid source property type");
                }

                if (!clone)
                    synapseContext.getPropertyKeySet().remove(property);

                break;
            }
            default: {

                synLog.error("Case mismatch for type: " + sourceType);
            }
        }

        return object;
    }

    /**
     * Getters and Setters
     */

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isClone() {
        return clone;
    }

    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public String getInlineJSONNode() {
        return inlineJSONNode;
    }

    public void setInlineJSONNode(String inlineJSONNode) {
        this.inlineJSONNode = inlineJSONNode;
    }

    public String getInlineKey() {
        return inlineKey;
    }

    public void setInlineKey(String inlineKey) {
        this.inlineKey = inlineKey;
    }
}
