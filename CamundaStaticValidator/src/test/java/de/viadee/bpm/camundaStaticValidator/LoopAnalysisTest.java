package de.viadee.bpm.camundaStaticValidator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.camundaStaticValidator.processing.ElementGraphBuilder;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.Anomaly;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.AnomalyContainer;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.IGraph;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.Path;

public class LoopAnalysisTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: Data flow graph creation and calculation of invalid paths
   */
  @Test
  public void testLoop() {
    final String PATH = BASE_PATH + "LoopAnalysisTest_TestLoop.bpmn";
    final File processdefinition = new File(PATH);

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(processdefinition);

    final ElementGraphBuilder graphBuilder = new ElementGraphBuilder();
    // create data flow graphs
    final Collection<IGraph> graphCollection = graphBuilder.createProcessGraph(modelInstance,
        processdefinition.getPath(), cl, new ArrayList<String>());

    graphBuilder.createInvalidPaths(graphCollection);

    // calculate invalid paths based on data flow graphs
    final Map<AnomalyContainer, List<Path>> invalidPathMap = graphBuilder
        .createInvalidPaths(graphCollection);

    // get invalid paths
    final List<Path> varTest = invalidPathMap
        .get(new AnomalyContainer("dd", Anomaly.DD, "ServiceTask_1ev9i13", null));
    Assert.assertEquals(
        "[[ServiceTask_1ev9i13, SequenceFlow_0s4fyqh, ExclusiveGateway_1vhe4nv, SequenceFlow_12tyqqh, ExclusiveGateway_0utydka, SequenceFlow_0g3rb21, ServiceTask_1ev9i13]]",
        varTest.toString());
  }
}
