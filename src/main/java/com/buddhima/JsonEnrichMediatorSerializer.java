package com.buddhima.xml;

import com.buddhima.JsonEnrichMediator;
import com.buddhima.JsonSource;
import com.buddhima.JsonTarget;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.config.xml.AbstractMediatorSerializer;

/**
 * Created by buddhima on 3/15/17.
 */
public class JsonEnrichMediatorSerializer extends AbstractMediatorSerializer {
    protected OMElement serializeSpecificMediator(Mediator m) {
        assert m != null : "mediator cannot be null";
        assert m instanceof JsonEnrichMediator : "mediator should be of type EnrichMediator";

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
                    intTypeToString(source.getSourceType())));
        }

        if (source.isClone()) {
            sourceEle.addAttribute(fac.createOMAttribute("clone", nullNS,
                    Boolean.toString(source.isClone())));
        }

        if (source.getSourceType() == JsonEnrichMediator.PROPERTY) {
            sourceEle.addAttribute(fac.createOMAttribute("property", nullNS, source.getProperty()));
        } else if (source.getSourceType() == JsonEnrichMediator.CUSTOM) {
            SynapseXPathSerializer.serializeXPath(source.getXpath(), sourceEle, "xpath");
        } else if (source.getSourceType() == JsonEnrichMediator.INLINE) {
            if (source.getInlineOMNode() instanceof OMElement) {
                sourceEle.addChild(((OMElement) source.getInlineOMNode()).cloneOMElement());
            } else if (source.getInlineOMNode() instanceof OMText) {
                /*Text as inline content*/
                sourceEle.setText(((OMText) source.getInlineOMNode()).getText());
            } else if (source.getInlineKey() != null) {
                sourceEle.addAttribute("key", source.getInlineKey(), null);
            }
        }
        return sourceEle;
    }

    private OMElement serializeTarget(JsonTarget target) {
        return null;
    }

    private String intTypeToString(int type) {
        if (type == JsonEnrichMediator.CUSTOM) {
            return EnrichMediatorFactory.CUSTOM;
        } else if (type == JsonEnrichMediator.BODY) {
            return EnrichMediatorFactory.BODY;
        } else if (type == JsonEnrichMediator.ENVELOPE) {
            return EnrichMediatorFactory.ENVELOPE;
        } else if (type == JsonEnrichMediator.PROPERTY) {
            return EnrichMediatorFactory.PROPERTY;
        } else if (type == JsonEnrichMediator.INLINE) {
            return EnrichMediatorFactory.INLINE;
        }
        return null;
    }


    public String getMediatorClassName() {
        return JsonEnrichMediator.class.getName();
    }
}
