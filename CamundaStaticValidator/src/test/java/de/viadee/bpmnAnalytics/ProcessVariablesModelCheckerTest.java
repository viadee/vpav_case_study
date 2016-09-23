package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.ElementGraphBuilder;
import de.viadee.bpmnAnalytics.processing.checker.ModelChecker;
import de.viadee.bpmnAnalytics.processing.checker.ProcessVariablesModelChecker;
import de.viadee.bpmnAnalytics.processing.model.data.AnomalyContainer;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.graph.IGraph;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

public class ProcessVariablesModelCheckerTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static BpmnModelInstance modelInstance;

  private static ModelChecker checker;

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);

    final String PATH = BASE_PATH + "ProcessVariablesModelCheckerTest_GraphCreation.bpmn";
    final File processdefinition = new File(PATH);

    // parse bpmn model
    modelInstance = Bpmn.readModelFromFile(processdefinition);

    final ElementGraphBuilder graphBuilder = new ElementGraphBuilder();
    // create data flow graphs
    final Collection<IGraph> graphCollection = graphBuilder.createProcessGraph(modelInstance,
        processdefinition.getPath(), cl, new ArrayList<String>());

    // calculate invalid paths based on data flow graphs
    final Map<AnomalyContainer, List<Path>> invalidPathMap = graphBuilder
        .createInvalidPaths(graphCollection);

    final Rule rule = new Rule("ProcessVariablesModelChecker", true, null, null, null);
    checker = new ProcessVariablesModelChecker(rule, invalidPathMap);
  }

  /**
   * Case: there is an empty script reference
   */
  @Test
  public void testProcessVariablesModelChecker() {
    final Collection<CheckerIssue> issues = checker.check(modelInstance, cl);

    if (issues.size() == 0) {
      Assert.fail("there should be generated an issue");
    }

    Iterator<CheckerIssue> iterator = issues.iterator();
    final CheckerIssue issue1 = iterator.next();
    Assert.assertEquals("SequenceFlow_0bi6kaa", issue1.getElementId());
    Assert.assertEquals("geloeschteVariable", issue1.getVariable());
    Assert.assertEquals("DU", issue1.getAnomaly().toString());
    final CheckerIssue issue2 = iterator.next();
    Assert.assertEquals("SequenceFlow_0btqo3y", issue2.getElementId());
    Assert.assertEquals("jepppa", issue2.getVariable());
    Assert.assertEquals("DD", issue2.getAnomaly().toString());
    final CheckerIssue issue3 = iterator.next();
    Assert.assertEquals("ServiceTask_05g4a96", issue3.getElementId());
    Assert.assertEquals("intHallo", issue3.getVariable().toString());
    Assert.assertEquals("UR", issue3.getAnomaly().toString());
    final CheckerIssue issue4 = iterator.next();
    Assert.assertEquals("BusinessRuleTask_119jb6t", issue4.getElementId());
    Assert.assertEquals("hallo2", issue4.getVariable());
    Assert.assertEquals("UR", issue4.getAnomaly().toString());
  }
}