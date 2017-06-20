Process Variables Model Checker
=================================
The Process Variables Model Checker processes BPMN models and checks a model for anomalies in the data flow. The checker anomalies 

## Assumptions
----------------------------------------------
- The **BPMN-models** have to be in the **classpath** at build time

## Configuration
------------------------------------------
The rule should be configured as follows:
```xml
<rule>
  <name>ProcessVariablesModelChecker</name>
  <state>true</state>
</rule>

```

