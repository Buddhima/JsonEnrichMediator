package com.buddhima;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorFactory;
import org.apache.synapse.config.xml.XMLConfigConstants;

import javax.xml.namespace.QName;
import java.util.Properties;

public class JsonEnrichMediatorFactory extends AbstractMediatorFactory {

    private static final QName XML_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "jsonEnrich");
    private static final QName ATT_PROPERTY = new QName("property");
    private static final QName ATT_JSONPATH = new QName("JSONPath");
    private static final QName ATT_TYPE = new QName("type");
    private static final QName ATT_CLONE = new QName("clone");
    private static final QName ATT_ACTION = new QName("action");

    public static final QName SOURCE_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "source");
    public static final QName TARGET_Q = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "target");


    protected Mediator createSpecificMediator(OMElement elem, Properties properties) {
        if (!XML_Q.equals(elem.getQName())) {
            handleException("Unable to create the enrich mediator. " +
                    "Unexpected element as the enrich mediator configuration");
        }

        JsonEnrichMediator enrich = new JsonEnrichMediator();
        processAuditStatus(enrich, elem);

        OMElement sourceEle = elem.getFirstChildWithName(SOURCE_Q);
        if (sourceEle == null) {
            handleException("source element is mandatory");
        }

        try {
            JsonSource source = new JsonSource();
            enrich.setSource(source);

            OMElement targetEle = elem.getFirstChildWithName(TARGET_Q);
            if (targetEle == null) {
                handleException("target element is mandatory");
            }
            JsonTarget target = new JsonTarget();
            enrich.setTarget(target);

            populateSource(source, sourceEle);
            populateTarget(target, targetEle);
        } catch (Exception ex) {
            handleException("Error while initializing JsonEnrichMediator", ex);
        }

        return enrich;
    }

    private void populateSource(JsonSource source, OMElement sourceEle) {

        OMAttribute typeAttr = sourceEle.getAttribute(ATT_TYPE);
        if (typeAttr != null && typeAttr.getAttributeValue() != null) {
            source.setSourceType(JsonEnrichMediator.findType(typeAttr.getAttributeValue()));
        }

        OMAttribute cloneAttr = sourceEle.getAttribute(ATT_CLONE);
        if (cloneAttr != null && cloneAttr.getAttributeValue() != null) {
            source.setClone(Boolean.parseBoolean(cloneAttr.getAttributeValue()));
        }

        if (source.getSourceType() == JsonEnrichMediator.CUSTOM) {
            OMAttribute jsonPathAttr = sourceEle.getAttribute(ATT_JSONPATH);
            if (jsonPathAttr != null && jsonPathAttr.getAttributeValue() != null) {
                source.setJsonPath(jsonPathAttr.getAttributeValue());
            } else {
                handleException("JSONPath attribute is required for CUSTOM type");
            }
        } else if (source.getSourceType() == JsonEnrichMediator.PROPERTY) {
            OMAttribute propertyAttr = sourceEle.getAttribute(ATT_PROPERTY);
            if (propertyAttr != null && propertyAttr.getAttributeValue() != null) {
                source.setProperty(propertyAttr.getAttributeValue());
            } else {
                handleException("property attribute is required for PROPERTY type");
            }
        } else if (source.getSourceType() == JsonEnrichMediator.INLINE) {

            if (sourceEle.getText() != null && (!sourceEle.getText().equals(""))) {
                source.setInlineJSONNode(sourceEle.getText());
            } else if (sourceEle.getAttributeValue(ATT_KEY) != null) {
                source.setInlineKey(sourceEle.getAttributeValue(ATT_KEY));
            } else {
                handleException("JSON element is required for INLINE type");
            }

        }
    }

    private void populateTarget(JsonTarget target, OMElement targetEle) {
        OMAttribute typeAttr = targetEle.getAttribute(ATT_TYPE);
        if (typeAttr != null && typeAttr.getAttributeValue() != null) {
            int type = JsonEnrichMediator.findType(typeAttr.getAttributeValue());
            if (type >= 0) {
                target.setTargetType(type);
            } else {
                handleException("Un-expected type : " + typeAttr.getAttributeValue());
            }
        }

        OMAttribute actionAttr = targetEle.getAttribute(ATT_ACTION);
        if (actionAttr != null && actionAttr.getAttributeValue() != null) {
            target.setAction(actionAttr.getAttributeValue());
        }

        if (target.getTargetType() == JsonEnrichMediator.CUSTOM) {
            OMAttribute jsonPathAttr = targetEle.getAttribute(ATT_JSONPATH);
            if (jsonPathAttr != null && jsonPathAttr.getAttributeValue() != null) {
                target.setJsonPath(jsonPathAttr.getAttributeValue());
            } else {
                handleException("JSONPath attribute is required for CUSTOM type");
            }

            if (target.getAction().equalsIgnoreCase(JsonTarget.ACTION_PUT)) {

                OMAttribute propertyAttr = targetEle.getAttribute(ATT_PROPERTY);
                if (propertyAttr != null && propertyAttr.getAttributeValue() != null) {
                    target.setProperty(propertyAttr.getAttributeValue());
                } else {
                    handleException("property attribute is required for CUSTOM type with PUT action");
                }
            }

        }
        if (target.getTargetType() == JsonEnrichMediator.PROPERTY) {
            OMAttribute propertyAttr = targetEle.getAttribute(ATT_PROPERTY);
            if (propertyAttr != null && propertyAttr.getAttributeValue() != null) {
                target.setProperty(propertyAttr.getAttributeValue());
            } else {
                handleException("property attribute is required for PROPERTY type");
            }
        }
    }

    public QName getTagQName() {
        return XML_Q;
    }
}
