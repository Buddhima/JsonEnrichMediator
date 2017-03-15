# Json Enrich Mediator
This mediator works with native JSON in WSO2 ESB.
Existing mediator does not support JSON manipulations.

## Configuring Mediator
Build this repository with maven

Add following .jar files to *ESB_HOME/repository/components/dropins*

* JsonEnrichMediator-1.0.0.jar
* json-path-2.1.0.jar (external)
* json-smart-2.2.jar (external)

## Syntax
```
<class name="com.buddhima.JsonEnrichMediator"
                description="HandlerMediatorClass">
    <property name="sourceType" value="inline"/>
    <property name="sourceJSONPath" value="$.foo"/>
    <property name="sourceClone" value="false"/>
    <property name="sourceProperty" value="company"/>
    <property name="sourceInlineJSONNode" value="{'wso2':'telco'}"/>
    <property name="targetType" value="custom"/>
    <property name="targetJSONPath" value="$.name"/>
    <property name="targetAction" value="put"/>
    <property name="targetProperty" value="newPlace"/>
</class>
```
