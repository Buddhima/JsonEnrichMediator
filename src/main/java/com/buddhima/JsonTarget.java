package com.buddhima;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;

public class JsonTarget {

    private String jsonPath = null;
    private String property = null;
    private int targetType = JsonEnrichMediator.CUSTOM;

    // constants
    public static final String ACTION_SET = "set";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_PUT = "put";
    private String action = ACTION_SET;
//    public static final String XPATH_PROPERTY_PATTERN = "'[^']*'"
//
    private Configuration configuration = Configuration.defaultConfiguration();
    private SynapseLog log;


    public JsonTarget(String jsonPath, String property, String targetType, String action) throws Exception {
        this.jsonPath = jsonPath;
        this.property = property;
        this.targetType =  (targetType != null) ? JsonEnrichMediator.findType(targetType) : JsonEnrichMediator.CUSTOM;
        this.action = action;
    }

    public void insert(MessageContext synapseContext, Object sourceNode, SynapseLog synLog) {

        this.log = synLog;

        switch (targetType) {

            case JsonEnrichMediator.CUSTOM : {

                assert jsonPath != null : "JSONPath should be non null in case of CUSTOM";

                setValueInPath(synapseContext, jsonPath, sourceNode);

                break;
            }
            case JsonEnrichMediator.PAYLOAD: {

                org.apache.axis2.context.MessageContext context = ((Axis2MessageContext) synapseContext).getAxis2MessageContext();
                try {
                    String jsonString = JsonPath.using(configuration).parse(sourceNode).jsonString();
                    JsonUtil.getNewJsonPayload(context, jsonString, true, true);
                } catch (Exception ex) {
                    synLog.error("Error while setting json payload in BODY");
                }

                break;
            }
            case JsonEnrichMediator.PROPERTY: {

                synapseContext.setProperty(property, sourceNode);

                break;
            }
            default: {

                if (targetType == JsonEnrichMediator.INLINE) {
                    synLog.error("Target as INLINE not supported");
                }

                synLog.error("Case mismatch for type: " + targetType);
            }
        }
    }

    private void setValueInPath(MessageContext synapseContext, String jsonPath, Object sourceNode) {

        String expression = jsonPath;

        // Though SynapseJsonPath support "$.", the JSONPath implementation does not support it
        if (expression.endsWith(".")) {
            expression = expression.substring(0, expression.length()-1);
        }

        org.apache.axis2.context.MessageContext context = ((Axis2MessageContext) synapseContext).getAxis2MessageContext();

        assert JsonUtil.hasAJsonPayload(context) : "Message Context does not contain a JSON payload";

        String jsonString = JsonUtil.jsonPayloadToString(context);
        String newJsonString = "";

        if (action.equalsIgnoreCase(ACTION_SET)) {
            // replaces an existing value in json
            newJsonString = JsonPath.using(configuration).parse(jsonString).set(expression, sourceNode).jsonString();
        } else if (action.equalsIgnoreCase(ACTION_ADD)) {
            // adds a value to a json array
            newJsonString = JsonPath.using(configuration).parse(jsonString).add(expression, sourceNode).jsonString();
        } else if (action.equalsIgnoreCase(ACTION_PUT)) {
            // adds a new property to a json object
            assert property != null : "new property name should be specified";
            newJsonString = JsonPath.using(configuration).parse(jsonString).put(expression, property, sourceNode).jsonString();
        } else {
            // invalid action
            log.error("Invalid action set: " + action);
        }

        try {
            if (!newJsonString.trim().isEmpty()) {
                JsonUtil.getNewJsonPayload(context, newJsonString, true, true);
            }
        } catch (Exception ex) {
            log.error(ex);
        }

    }

}
