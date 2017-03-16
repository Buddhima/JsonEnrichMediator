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
<enrich>
    <source [clone=true|false] [type=custom|payload|property|inline] JSONPath="" property="" key="" />
    <target [type=custom|payload|property] [action=set|add|put] JSONPath="" property="" />
</enrich>
```

The main properties of JsonEnrichMediator are;

* Source
* Target

## Source Configurations
The following properties are available:

* **clone** - By setting the clone configuration, the source value can be cloned or used as a reference during enriching. The default value is false.
    * true
    * false

* **type** -  The type that the mediator uses from the original message to enrich the modified message that passes through the mediator.
    * custom - Custom JSONPath value. **JSONPath** attribute must specify the JSONPath expression.
    * payload - Complete original message uses for enriching.
    * property - A property in synapse-message-context uses for enriching. **property** attribute must specify the property name.
    * inline - Specify and inline JSON object or use **key** attribute a value specified as a local-entry.
    
* **JSONPath** - JSONPath expression uses to select the source, if the **type** is **custom**. The value should comply with syntax given at [here](https://github.com/jayway/JsonPath/blob/json-path-2.1.0/README.md)

* **property** - Name of the property in synapse-message-context that should be used for enriching, if the **type** is **property**.

* **key** - Key value of the inline JSON that should be used for enriching, if the JSON is specified as a local-entry.

## Target Configurations
The following properties are available:

* **type** - The type that the mediator uses from the original message to enrich the modified message that passes through the mediator.
    * custom - Custom JSONPath value. **action** attribute must specify the action to be used. **JSONPath** attribute must specify the JSONPath expression.
    * payload - Complete original message needs to replace.
    * property - A new property in synapse-message-context uses as the target. **property** attribute must specify the property name.

* **action** - By specifying the action type, the relevant action can be applied to outgoing messages.
    * set - Replaces the value of the property in **JSONPath** with the source value
    * add - Adds the source value to the JSON array specified by **JSONPath**.
    * put - Adds a new property to the JSON object specified by **JSONPath**. **property** attribute must specify the new property name.
    
* **JSONPath** - JSONPath expression uses to select the source, if the **type** is **custom**. The value should comply with syntax given at [here](https://github.com/jayway/JsonPath/blob/json-path-2.1.0/README.md)
  
* **property** - Name of the new property should be added, if the **type** is **property** or **custom** (when the **action** is **put**).

## Examples

Following JSON payload is used as the input

```json
{
	"me": {
		"country": "Sri Lanka",
		"language" : "Sinhala"
	}
}
```

### Example 1: Extracting a value from payload

Extracting a value specified by JSONPath to a synapse-context-property
 
```xml
<jsonEnrich>
    <source type="custom" clone="true" JSONPath="$.me.country"/>
    <target type="property" property="newPlace"/>
</jsonEnrich>
```

### Example 2: Move a value in payload

Cut and paste a value in payload to a new location

```xml
<jsonEnrich>
    <source type="custom" clone="false" JSONPath="$.me.country"/>
    <target type="custom" action="put" JSONPath="$" property="country"/>
</jsonEnrich>
```

Output

```json
{
  "me": {
    "language": "Sinhala"
  },
  "country": "Sri Lanka"
}
```

### Example 3: Replace a property with inline JSON

```xml
<jsonEnrich>
    <source type="inline">
        {"name" : "Buddhima"}
    </source>
    <target type="custom" action="set" JSONPath="$.me"/>
</jsonEnrich>
```

Output

```json
{
  "me": {
    "name": "Buddhima"
  }
}
```

## Future Improvements

- [ ] Add "action" support when target type is "property"

If you have suggestions or issues, please mention them at "issues" section at GitHub repository.


