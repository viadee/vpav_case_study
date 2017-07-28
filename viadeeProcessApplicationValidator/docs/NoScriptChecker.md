No Script Checker
================================= 
The No Script Checker processes BPMN models and checks whether there is a script in the model.

## Assumptions
----------------------------------------------
- The **BPMN-models** have to be in the **classpath** at build time

## Configuration
------------------------------------------
The rule should be configured as follows:
```xml
<rule>
	<name>NoScriptChecker</name>
	<state>true</state>
</rule>

```

## Error messages:
-----------------------------------------
**task %elementId with script**

_There is a script inside a script task or a script as an execution listener or a script as a task listener or a script inside an inputOutput parameter mapping_

**SequenceFlow %elementId with Script as condition expression**

_There is a script as condition expression of a sequence flow_

**ScriptTask %elementId not allowed**

_ScriptTask not allowed_

## Examples
----------------------------------------


