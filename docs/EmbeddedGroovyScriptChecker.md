Embedded Groovy Script Checker
=================================


## Assumptions
----------------------------------------------
- The **BPMN-models** have to be in **classpath**
- The **java classes _(delegates)_** have to be in the **classpath**

## Configuration
------------------------------------------
The rule should be configured as follows:
```xml
<rule>
  <name>EmbeddedGroovyScriptChecker</name>
  <state>true</state>
</rule>
```

Via `<state>true</state>` the check can be enabled.

Via `<state>false</state>` the check can be disabled.

## Error messages:
-----------------------------------------


## Examples
----------------------------------------

| ** **                                                                        | 
|:------------------------------------------------------------------------------------------------------:| 
|![alt text](img/EmbeddedGroovyScriptChecker_.PNG "Description")    |
| |

| ** **                                                   |
|:------------------------------------------------------------------------------------------------------:| 
| ![alt text](img/EmbeddedGroovyScriptChecker_.PNG "Description")                           |
| |

| ** **                                                |
|:------------------------------------------------------------------------------------------------------:| 
![alt text](img/EmbeddedGroovyScriptChecker_.PNG "Description")      |
