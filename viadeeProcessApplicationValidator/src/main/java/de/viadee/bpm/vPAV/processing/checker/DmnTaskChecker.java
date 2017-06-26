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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.xml.sax.SAXException;

import de.viadee.bpm.vPAV.BpmnCheckerMojo;
import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.XmlScanner;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

/**
 * Checks, whether a business rule task with dmn implementation is valid
 * 
 */
public class DmnTaskChecker extends AbstractElementChecker {

    public DmnTaskChecker(final Rule rule) {
        super(rule);
    }

    @Override
    public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl) {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        String path;

        for (final String output : BpmnCheckerMojo.getModelPath()) {
            path = ConstantsConfig.BASEPATH + output;
            issues.addAll(checkSingleModel(element, path));
        }
        return issues;
    }

    public Collection<CheckerIssue> checkSingleModel(final BpmnElement element, String path) {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        final BaseElement baseElement = element.getBaseElement();
        ArrayList<String> errors = new ArrayList<String>();

        if (baseElement instanceof BusinessRuleTask) {
            final BusinessRuleTask task = (BusinessRuleTask) baseElement;
            final String id = baseElement.getId();
            try {
                XmlScanner scan = new XmlScanner();
                errors = scan.getImplementation(path, id);
            } catch (ParserConfigurationException | XPathExpressionException | SAXException | IOException e) {
                e.printStackTrace();
            }

            // no references to business rules
            for (String error : errors) {
                if (!error.isEmpty() && error.equals("camunda:decisionRef")) {
                    issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                            element.getProcessdefinition(), null, task.getId(), task.getName(), null, null,
                            null,
                            "business rule task with dmn implementation without a decision ref"));
                }
            }
        }
        return issues;
    }
}
