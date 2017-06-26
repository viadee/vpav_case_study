# viadee Process Application Validator (vPAV)

The tool checks Camunda projects for consistency and discovers errors in process-driven applications.
Called as a Maven plugin or JUnit test, it discovers esp. inconsistencies of given BPMN model in the classpath and the sourcecode of an underlying java project, 
such as a delegate reference to a non-existing java class or a non-existing Spring bean.

Find a list of the consistency checks below.

We recommend to integrate the consistency check in you CI builds - you can't find these inconsistencies early enough.

# Features

## Checker

### [BusinessRuleTaskChecker](BusinessRuleTaskChecker.md)

### [DmnTaskChecker](DmnTaskChecker.md)

### [EmbeddedGroovyScriptChecker](EmbeddedGroovyScriptChecker.md)

### [JavaDelegateChecker](JavaDelegateChecker.md)

### [ProcessVariablesModelChecker](ProcessVariablesModelChecker.md)

### [ProcessVariablesNameConventionChecker](ProcessVariablesNameConventionChecker.md)

### [TaskNamingConventionChecker](TaskNamingConventionChecker.md)

### [VersioningChecker](VersioningChecker.md)

All of these are configurable.

## Output

The result of the check is first of all a direct one: if at least one inconsistency is 
found on the ERROR level, it will break your build or count as a failed unit 
test which will break your build too.

Further, the consistency check will provide an XML version, a JSON version and
an visual version based on  [BPMN.io](https://bpmn.io/) of all errors and warnings found.

## Installation/Usage
There are two ways of installation. We recommend to use the JUnit approach as follows.

1. If you use Maven, add the dependency to your POM:
```xml
<dependency>
  <groupId>de.viadee.bpm</groupId>
  <artifactId>viadeeProcessApplicationValidator</artifactId>
  <version>...</version>
</dependency>
```
2. Configure a JUnit Test to fire up your usual Spring context, if you use Spring in your application or a simple test case otherwise to call the consistency check.

## Commitments
This library will remain under an open source licence indefinately.

We follow the [semantic versioning](http://semver.org) scheme (2.0.0).

In the sense of semantic versioning, the resulting XML and JSON outputs are the _only public API_ provided here. 
We will keep these as stable as possible, in order to enable users to analyse and integrate results into the toolsets of their choice.

## Cooperation
Feel free to report issues, questions, ideas or patches. We are looking forward to it.

## License (BSD4)
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
    must display the following acknowledgement:
    This product includes software developed by the viadee Unternehmensberatung GmbH.
 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
