/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.odysseus.el.tree.IdentifierNode;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.impl.Builder;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

/**
 * Class JavaDelegateChecker
 * 
 * Checks a bpmn model, if code references (java delegates) for tasks have been set correctly.
 *
 */
public class JavaDelegateChecker extends AbstractElementChecker {

    private Map<String, String> beanMapping;

    public JavaDelegateChecker(final Rule rule, final Map<String, String> beanMapping) {
        super(rule);
        this.beanMapping = beanMapping;
    }

    public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader classLoader) {

        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

        final BaseElement bpmnElement = element.getBaseElement();

        // read attributes from task
        final String classAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "class");
        final String delegateExprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "delegateExpression");
        final String exprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "expression");
        final String typeAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "type");

        if (classAttr != null) {
            if (classAttr.trim().length() == 0 && delegateExprAttr == null && exprAttr == null
                    && typeAttr == null) {
                // Error, because no class has been configured
                issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                        element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                        bpmnElement.getAttributeValue("name"), null, null, null,
                        "task '" + bpmnElement.getAttributeValue("name") + "' with no class name"));
            }
            if (classAttr.trim().length() > 0) {
                issues.addAll(checkClassFile(element, classLoader, classAttr));
            }
        }
        if (delegateExprAttr != null) {
            // check validity of a bean
            if (beanMapping != null) {
                final String filteredExpression = delegateExprAttr.replaceAll("[\\w]+\\.", "");
                final TreeBuilder treeBuilder = new Builder();
                final Tree tree = treeBuilder.build(filteredExpression);
                final Iterable<IdentifierNode> identifierNodes = tree.getIdentifierNodes();
                // if beanMapping ${...} reference
                if (identifierNodes.iterator().hasNext()) {
                    for (final IdentifierNode node : identifierNodes) {
                        final String classFile = beanMapping.get(node.getName());
                        // correct beanmapping was found -> check if class exists
                        if (classFile != null && classFile.trim().length() > 0) {
                            issues.addAll(checkClassFile(element, classLoader, classFile));
                        } else {
                            // incorrect beanmapping
                            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                                    element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                                    bpmnElement.getAttributeValue("name"), null, null, null,
                                    "Couldn't find correct beanmapping for delegate expression in task '"
                                            + bpmnElement.getAttributeValue("name") + "'"));
                        }
                    }
                } else {
                    issues.addAll(checkClassFile(element, classLoader, delegateExprAttr));
                }
            }
        }
        if (classAttr == null && delegateExprAttr == null && exprAttr == null && typeAttr == null) {
            // No technical attributes have been added
            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                    element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                    bpmnElement.getAttributeValue("name"), null, null, null,
                    "task '" + bpmnElement.getAttributeValue("name") + "' with no code reference yet"));
        }
        return issues;
    }

    private Collection<CheckerIssue> checkClassFile(final BpmnElement element,
            final ClassLoader classLoader, final String className) {

        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        final BaseElement bpmnElement = element.getBaseElement();
        final String classPath = className.replaceAll("\\.", "/") + ".java";

        // If a class path has been found, check the correctness
        try {
            Class<?> clazz = classLoader.loadClass(className);

            // Checks, whether the correct interface was implemented
            Class<?>[] interfaces = clazz.getInterfaces();
            boolean javaDelegateImplemented = false;
            for (final Class<?> _interface : interfaces) {
                if (_interface.getName().contains("JavaDelegate")) {
                    javaDelegateImplemented = true;
                }
            }
            if (javaDelegateImplemented == false) {
                // class implements not the interface "JavaDelegate"
                issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                        element.getProcessdefinition(), classPath, bpmnElement.getAttributeValue("id"),
                        bpmnElement.getAttributeValue("name"), null, null, null,
                        "class for task '" + bpmnElement.getAttributeValue("name")
                                + "' does not implement interface JavaDelegate"));
            }

        } catch (final ClassNotFoundException e) {
            // Throws an error, if the class was not found
            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                    element.getProcessdefinition(), classPath, bpmnElement.getAttributeValue("id"),
                    bpmnElement.getAttributeValue("name"), null, null, null,
                    "class for task '" + bpmnElement.getAttributeValue("name") + "' not found"));
        }

        return issues;
    }
}
