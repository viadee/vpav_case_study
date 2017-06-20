Task Naming Convention Checker
=================================
The Task Naming Convention Checker verifies if tasks match a predefined naming convention.

## Assumptions
----------------------------------------------
- The **BPMN-models** have to be in the **classpath**

## Configuration
------------------------------------------
The rule should be configured as follows:
```xml
<rule>
    <name>TaskNamingConventionChecker</name>
    <state>false</state>
    <elementConventions>
        <elementConvention>
            <name>convention</name>
            <pattern>[A-ZÄÖÜ][a-zäöü\\\-\\\s]+</pattern>
        </elementConvention>
    </elementConventions>
</rule>

```

## Error messages
-----------------------------------------
**task name must be specified**

_The task name is missing and has to be specified in the model._

**task name '%taskName%' is against the naming convention**

_The task name is invalid and has to be changed according to the naming convention._

