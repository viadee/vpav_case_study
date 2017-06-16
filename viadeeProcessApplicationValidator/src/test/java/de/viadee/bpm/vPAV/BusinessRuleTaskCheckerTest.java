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

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.checker.BusinessRuleTaskChecker;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

/**
 * unit tests for class BusinessRuleTaskChecker
 *
 */
public class BusinessRuleTaskCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ElementChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final Rule rule = new Rule("BusinessRuleTaskChecker", true, null, null, null);
    checker = new BusinessRuleTaskChecker(rule);
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  
  /**
   * Case: BusinessRuleTask without specified implementation
   */
  
  
//  @Test
//  public void testBusinessRuleTaskWithNoImplementation(){
//      final String PATH = BASE_PATH + "test.bpmn";      
//      // parse bpmn model
//      final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));
//      final Collection<BusinessRuleTask> baseElements = modelInstance
//          .getModelElementsByType(BusinessRuleTask.class);
//      final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());
//
//      final Collection<CheckerIssue> issues = checker.check(element, cl); 
//            
//      assertTrue("Implementation of BusinessRuleTask has to be set", !issues.isEmpty());      
//  }
  
  /**
   * Case: BusinessRuleTask with DMN implementation needs to specify a decision ref
   */
  
//  @Test
//  public void testBusinessRuleTaskAndDmnWithoutRef(){
//      final String PATH = BASE_PATH + "BusinessRuleTaskCheckerAndDmnWithoutRefTest.bpmn";      
//      // parse bpmn model
//      final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));
//      final Collection<BusinessRuleTask> baseElements = modelInstance
//          .getModelElementsByType(BusinessRuleTask.class);
//      final BpmnElement element = new BpmnElement(PATH, baseElements.iterator().next());
//
//      final Collection<CheckerIssue> issues = checker.check(element, cl); 
//               
//      assertFalse("Decision Reference for DMN has to be set", issues.isEmpty());
//      
//  }
  
}