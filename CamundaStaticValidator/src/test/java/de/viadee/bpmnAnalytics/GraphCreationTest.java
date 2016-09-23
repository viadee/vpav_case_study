package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.processing.ElementGraphBuilder;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.graph.IGraph;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;
import junit.framework.Assert;

/**
 * Unit Tests for data flow graph creation and calculation of invalid paths
 *
 */
public class GraphCreationTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java/");
    final URL[] classUrls = { classUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: Data flow graph creation and calculation of invalid paths
   */
  @Test
  public void testGraph() {
    final String PATH = BASE_PATH + "testGraphCreation.bpmn";
    final File processdefinition = new File(PATH);

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(processdefinition);

    final ElementGraphBuilder graphBuilder = new ElementGraphBuilder();
    // create data flow graphs
    final Collection<IGraph> graphCollection = graphBuilder.createProcessGraph(modelInstance,
        processdefinition.getPath(), cl);

    // calculate invalid paths based on data flow graphs
    final Map<ProcessVariable, List<Path>> invalidPathMap = graphBuilder
        .calculateAllInvalidPaths(graphCollection);

    // get invalid paths
    final List<Path> jepppaTest = invalidPathMap
        .get(new ProcessVariable("jepppa", null, null, null, PATH, false, PATH));
    Assert.assertEquals(
        "[[StartEvent_1, SequenceFlow_1aapyv6, ServiceTask_108g52x, SequenceFlow_0yhv5j2, ServiceTask_05g4a96, SequenceFlow_09j6ilt, ExclusiveGateway_0mkf3hf, SequenceFlow_0t7iwpj, Task_0546a8y, SequenceFlow_1m6lt2o, ExclusiveGateway_00pfwgg, SequenceFlow_0btqo3y], [StartEvent_1, SequenceFlow_1aapyv6, ServiceTask_108g52x, SequenceFlow_0yhv5j2, ServiceTask_05g4a96, SequenceFlow_09j6ilt, ExclusiveGateway_0mkf3hf, SequenceFlow_1mggduw, Task_11t5rso, SequenceFlow_06ehu4z, ExclusiveGateway_00pfwgg, SequenceFlow_0btqo3y]]",
        jepppaTest.toString());

    final List<Path> testHallo2 = invalidPathMap
        .get(new ProcessVariable("hallo2", null, null, null, PATH, false, PATH));
    Assert.assertEquals(
        "[[StartEvent_1, SequenceFlow_1aapyv6, ServiceTask_108g52x, SequenceFlow_0yhv5j2, ServiceTask_05g4a96, SequenceFlow_09j6ilt, ExclusiveGateway_0mkf3hf, SequenceFlow_1mggduw, Task_11t5rso, SequenceFlow_1ck3twv, SubProcess_00ff1kx, StartEvent_0bcezfo, SequenceFlow_0jkf21p, SubProcess_0aqkwyh, StartEvent_0sqm3mr, SequenceFlow_1qax2e0, BusinessRuleTask_119jb6t]]",
        testHallo2.toString());
  }
}
