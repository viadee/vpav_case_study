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
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.xml.sax.SAXException;

import de.viadee.bpm.vPAV.AbstractRunner;
import de.viadee.bpm.vPAV.BPMNScanner;
import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.CheckName;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;

/**
 * Class NoScriptChecker
 *
 * Checks a bpmn model, if there is any script (Script inside a script task - Script as an execution listener - Script
 * as a task listener - Script inside an inputOutput parameter mapping)
 *
 */
public class NoScriptChecker extends AbstractElementChecker {

    public NoScriptChecker(final Rule rule) {
        super(rule);
    }

    @Override
    public Collection<CheckerIssue> check(final BpmnElement element) {
        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        String path;
        for (final String output : AbstractRunner.getModelPath()) {
            path = ConstantsConfig.BASEPATH + output;
            try {
                issues.addAll(checkSingleModel(element, path));
            } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        return issues;
    }

    public Collection<CheckerIssue> checkSingleModel(final BpmnElement element, String path)
            throws ParserConfigurationException, XPathExpressionException, SAXException, IOException {

        final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
        final BaseElement bpmnElement = element.getBaseElement();
        BPMNScanner scan = new BPMNScanner();

        // ScripTasks not allowed
        if (bpmnElement instanceof ScriptTask) {
            issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                    element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                    bpmnElement.getAttributeValue("name"), null, null, null,
                    "ScriptTask '" + CheckName.checkName(bpmnElement) + "' not allowed"));
        }

        if (!(bpmnElement instanceof Process) && !(bpmnElement instanceof SubProcess)) {
            // Search for camunda:script tag
            if (scan.hasScript(path, bpmnElement.getAttributeValue("id")).containsKey(true)) {
                // Error, because script were found
                issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
                        element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
                        bpmnElement.getAttributeValue("name"), null, null, null,
                        "task '" + CheckName.checkName(bpmnElement) + "' with script"));
            }
        }

        return issues;
    }
}
