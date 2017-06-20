Business Rule Task Checker
=================================
The Business Rule Task Checker processes BPMN models and checks, whether the following conditions apply to Business Rule Tasks:
- No implementation specified
- Implementation without its respective reference specified

## Assumptions
----------------------------------------------
- The **BPMN-models** have to be in the **classpath** at build time
- The **java classes _(delegates)_** have to be in the **classpath** at build time

## Error messages:
-----------------------------------------
**no implementation or reference has been specified for '%taskName%'**

_No implementation or reference to source code has been deposited._

