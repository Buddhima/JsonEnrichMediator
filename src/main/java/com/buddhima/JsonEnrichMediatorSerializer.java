package com.buddhima;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;

public class JsonEnrichMediatorSerializer extends AbstractMediatorSerializer {
    protected OMElement serializeSpecificMediator(Mediator m) {
        assert m != null : "mediator cannot be null";
        assert m instanceof JsonEnrichMediator : "mediator should be of type JsonEnrichMediator";

        JsonEnrichMediator mediator = (JsonEnrichMediator)m;

        OMElement jsonEnrichEle = fac.createOMElement("jsonEnrich", synNS);

        OMElement sourceEle = serializeSource(mediator.getSource());
        OMElement targetEle = serializeTarget(mediator.getTarget());

        jsonEnrichEle.addChild(sourceEle);
        jsonEnrichEle.addChild(targetEle);

        return jsonEnrichEle;
    }

    private OMElement serializeSource(JsonSource source) {
        OMElement sourceEle = fac.createOMElement("source", synNS);

        if (source.getSourceType() != JsonEnrichMediator.CUSTOM) {
            sourceEle.addAttribute(fac.createOMAttribute("type", nullNS,
                    JsonEnrichMediator.findTypeString(source.getSourceType())));
        }

        if (source.isClone()) {
            sourceEle.addAttribute(fac.createOMAttribute("clone", nullNS, Boolean.toString(source.isClone())));
        }

        if (source.getSourceType() == JsonEnrichMediator.PROPERTY) {
            sourceEle.addAttribute(fac.createOMAttribute("property", nullNS, source.getProperty()));
        } else if (source.getSourceType() == JsonEnrichMediator.CUSTOM) {
            sourceEle.addAttribute(fac.createOMAttribute("JSONPath", nullNS, source.getJsonPath()));
        } else if (source.getSourceType() == JsonEnrichMediator.INLINE) {
            sourceEle.setText(source.getInlineJSONNode());
        }
        return sourceEle;
    }

    private OMElement serializeTarget(JsonTarget target) {
        OMElement targetEle = fac.createOMElement("target", synNS);

        if (target.getTargetType() != JsonEnrichMediator.CUSTOM) {
            targetEle.addAttribute(fac.createOMAttribute("type", nullNS,
                    JsonEnrichMediator.findTypeString(target.getTargetType())));
        }

        if (!target.getAction().equals(JsonTarget.ACTION_SET)) {
            targetEle.addAttribute(fac.createOMAttribute("action", nullNS, target.getAction()));
        }

        if (target.getTargetType() == JsonEnrichMediator.PROPERTY) {
            targetEle.addAttribute(fac.createOMAttribute("property", nullNS, target.getProperty()));
        } else if (target.getTargetType() == JsonEnrichMediator.CUSTOM) {
            targetEle.addAttribute(fac.createOMAttribute("property", nullNS, target.getProperty()));
            targetEle.addAttribute(fac.createOMAttribute("JSONPath", nullNS, target.getJsonPath()));
        }

        return targetEle;
    }

    public String getMediatorClassName() {
        return JsonEnrichMediator.class.getName();
    }
}
