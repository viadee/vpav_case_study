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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.xml.sax.SAXException;

import de.viadee.bpm.vPAV.AbstractRunner;
import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.XmlScanner;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.CheckName;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

public class BusinessRuleTaskChecker extends AbstractElementChecker {

    public BusinessRuleTaskChecker(final Rule rule) {
        super(rule);
    }

    public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl) {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        if (element.getBaseElement() instanceof BusinessRuleTask) {
            String path;
            for (final String output : AbstractRunner.getModelPath()) {
                path = ConstantsConfig.BASEPATH + output;
                try {
                    issues.addAll(checkSingleModel(element, cl, path));
                } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return issues;
    }

    public Collection<CheckerIssue> checkSingleModel(final BpmnElement element, final ClassLoader cl, String path)
            throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        final BaseElement bpmnElement = element.getBaseElement();
        XmlScanner scan = new XmlScanner();

        // read attributes from task
        final String implementationAttr = scan.getImplementation(path, bpmnElement.getId());

        final String classAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "class");
        final String delegateExprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "delegateExpression");
        final String exprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "expression");
        final String typeAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "type");
        final String dmnAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
                "decisionRef");

        if (implementationAttr != null) {
            // check if DMN reference is not empty
            if (implementationAttr.equals("camunda:decisionRef")) {
                if (dmnAttr == null || dmnAttr.trim().length() == 0) {
                    // Error, because no delegateExpression has been configured
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no dmn reference"));
                } else {
                    issues.addAll(checkDMNFile(element, cl, dmnAttr));
                }
            }

            // check if expression is correct
            else if (implementationAttr.equals("camunda:expression")) {
                if (exprAttr == null || exprAttr.trim().length() == 0) {
                    // Error, because no delegateExpression has been configured
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no expression"));
                }
                // expression überprüfbar?
            }

            // check if class is correct
            else if (implementationAttr.equals("camunda:class")) {
                if (classAttr == null || classAttr.trim().length() == 0) {
                    // Error, because no class has been configured
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no class name"));
                }
            }

            // check if delegateExpression is correct
            else if (implementationAttr.equals("camunda:delegateExpression")) {
                if (delegateExprAttr == null || delegateExprAttr.trim().length() == 0) {
                    // Error, because no delegateExpression has been configured
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no delegate expression"));
                }
                // delegate expression überprüfen notwenig, wenn ja was?
            }

            // check if external is correct
            else if (implementationAttr.equals("camunda:type")) {
                if (typeAttr == null || typeAttr.trim().length() == 0) {
                    // Error, because no delegateExpression has been configured
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no external topic"));
                }
                // external topic prüfbar?
            }

            else if (implementationAttr.equals("implementation"))
                if (dmnAttr == null && classAttr == null && delegateExprAttr == null
                        && exprAttr == null && typeAttr == null) {
                    // No technical attributes have been added
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                            bpmnElement.getAttributeValue("name"), null, null, null,
                            "task " + CheckName.checkName(bpmnElement) + " with no code reference yet"));
                }
        }
        return issues;
    }

    private Collection<CheckerIssue> checkDMNFile(final BpmnElement element,
            final ClassLoader classLoader, final String dmnName) {

        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        final BaseElement bpmnElement = element.getBaseElement();
        final String dmnPath = dmnName.replaceAll("\\.", "/") + ".dmn";

        // If a class path has been found, check the correctness

        URL urlDMN = classLoader.getResource(dmnPath);

        if (urlDMN == null) {
            // Throws an error, if the class was not found
            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                    element.getProcessdefinition(), dmnPath, bpmnElement.getAttributeValue("id"),
                    bpmnElement.getAttributeValue("name"), null, null, null,
                    "dmn File for task " + CheckName.checkName(bpmnElement) + " not found"));
        }

        return issues;
    }
}