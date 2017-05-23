/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the viadee Unternehmensberatung GmbH.
 * 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.checker.JavaDelegateChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

/**
 * unit tests for class JavaDelegateChecker
 *
 */
public class JavaDelegateCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final Rule rule = new Rule("JavaDelegateChecker", true, null, null, null);
    checker = new JavaDelegateChecker(rule, null);
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: JavaDelegate has been correct set
   */
  @Test
  public void testCorrectJavaDelegateReference() {
    final String PATH = BASE_PATH + "JavaDelegateCheckerTest_CorrectJavaDelegateReference.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() > 0) {
      Assert.fail("correct java delegate generates an issue");
    }
  }

  /**
   * Case: There are no technical attributes
   */
  @Test
  public void testNoTechnicalAttributes() {
    final String PATH = BASE_PATH + "JavaDelegateCheckerTest_NoTechnicalAttributes.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("task 'Service Task 1' with no code reference yet",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: java delegate has not been set
   */
  @Test
  public void testNoJavaDelegateEntered() {
    final String PATH = BASE_PATH + "JavaDelegateCheckerTest_NoJavaDelegateEntered.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("task 'Service Task 1' with no class name",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: The path of the java delegate isn't correct
   */
  @Test
  public void testWrongJavaDelegatePath() {
    final String PATH = BASE_PATH + "JavaDelegateCheckerTest_WrongJavaDelegatePath.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
      Assert.assertEquals("class for task 'Service Task 1' not found",
          issues.iterator().next().getMessage());
    }
  }

  /**
   * Case: The java delegates implements no or a wrong interface
   */
  @Test
  public void testWrongJavaDelegateInterface() {
    final String PATH = BASE_PATH + "JavaDelegateCheckerTest_WrongJavaDelegateInterface.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> baseElements = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());

    final Collection<CheckerIssue> issues = checker.check(element, cl);

    if (issues.size() != 1) {
      Assert.fail("collection with the issues is bigger or smaller as expected");
    } else {
            Assert.assertEquals("class for task 'Service Task 1' does not implement interface JavaDelegate",
          issues.iterator().next().getMessage());
    }
  }
}
