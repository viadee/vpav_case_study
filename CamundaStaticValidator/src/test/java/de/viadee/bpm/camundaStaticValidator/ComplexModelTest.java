package de.viadee.bpm.camundaStaticValidator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpm.camundaStaticValidator.processing.ElementGraphBuilder;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.AnomalyContainer;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.IGraph;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.Path;

public class ComplexModelTest {

  private static final String BASE_PATH = "src/test/resources/";

  private static ClassLoader cl;

  @BeforeClass
  public static void setup() throws MalformedURLException {
    final File file = new File(".");
    final String currentPath = file.toURI().toURL().toString();
    final URL classUrl = new URL(currentPath + "src/test/java/");
    final URL resourcesUrl = new URL(currentPath + "src/test/resources/");
    final URL[] classUrls = { classUrl, resourcesUrl };
    cl = new URLClassLoader(classUrls);
  }

  /**
   * Case: Check complex model for invalid paths
   * 
   * Included: * sub processes * boundary events * java delegate * spring bean * DMN model
   */
  @Test
  public void testGraphOnComplexModel() {
    final String PATH = BASE_PATH + "ComplexModelTest_GraphOnComplexModel.bpmn";
    final File processdefinition = new File(PATH);

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(processdefinition);

    // create many gateway paths to increase the complexity
    createGatewayPaths("ExclusiveGateway_0mkf3hf", "ExclusiveGateway_00pfwgg", modelInstance, 1);
    // createGatewayPaths("ExclusiveGateway_10gsr88", "ExclusiveGateway_13qew7s", modelInstance,
    // 500);

    final Map<String, String> decisionRefToPathMap = new HashMap<String, String>();
    decisionRefToPathMap.put("decision", "table.dmn");

    final Map<String, String> beanMappings = new HashMap<String, String>();
    beanMappings.put("springBean", "de.viadee.bpm.camundaStaticValidator.delegates.TestDelegate");

    long startTime = System.currentTimeMillis();

    final ElementGraphBuilder graphBuilder = new ElementGraphBuilder(decisionRefToPathMap, null,
        beanMappings, null, null);
    // create data flow graphs
    final Collection<IGraph> graphCollection = graphBuilder.createProcessGraph(modelInstance,
        processdefinition.getPath(), cl, new ArrayList<String>());

    long estimatedTime = System.currentTimeMillis() - startTime;
    System.out.println("Graph creation: " + estimatedTime + "ms");
    long startTime2 = System.currentTimeMillis();

    // calculate invalid paths based on data flow graphs
    final Map<AnomalyContainer, List<Path>> invalidPathMap = graphBuilder
        .createInvalidPaths(graphCollection);

    long estimatedTime2 = System.currentTimeMillis() - startTime2;
    System.out.println("Graph search: " + estimatedTime2 + "ms");
  }

  /**
   * Create paths between two gateways
   * 
   * @param gateway1_id
   * @param gateway2_id
   * @param modelInstance
   * @param count
   */
  private void createGatewayPaths(final String gateway1_id, final String gateway2_id,
      final BpmnModelInstance modelInstance, final int count) {

    final ModelElementInstance element_von = modelInstance.getModelElementById(gateway1_id);
    final ModelElementInstance element_zu = modelInstance.getModelElementById(gateway2_id);

    for (int i = 1; i < count + 1; i++) {
      // 1) create task
      final Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
      final FlowNode task = createElement(modelInstance, processes.iterator().next(), "task" + i,
          Task.class);
      // 2) connect with sequence flows
      createSequenceFlow(modelInstance, processes.iterator().next(), (FlowNode) element_von, task);
      createSequenceFlow(modelInstance, processes.iterator().next(), task, (FlowNode) element_zu);
    }
  }

  /**
   * create an bpmn element
   * 
   * @param parentElement
   * @param id
   * @param elementClass
   * @return
   */
  private <T extends BpmnModelElementInstance> T createElement(
      final BpmnModelInstance modelInstance, BpmnModelElementInstance parentElement, String id,
      Class<T> elementClass) {
    T element = modelInstance.newInstance(elementClass);
    element.setAttributeValue("id", id, true);
    parentElement.addChildElement(element);
    return element;
  }

  /**
   * create a sequence flow
   * 
   * @param process
   * @param from
   * @param to
   * @return
   */
  private SequenceFlow createSequenceFlow(final BpmnModelInstance modelInstance, Process process,
      FlowNode from, FlowNode to) {
    SequenceFlow sequenceFlow = createElement(modelInstance, process,
        from.getId() + "-" + to.getId(), SequenceFlow.class);
    process.addChildElement(sequenceFlow);
    sequenceFlow.setSource(from);
    from.getOutgoing().add(sequenceFlow);
    sequenceFlow.setTarget(to);
    to.getIncoming().add(sequenceFlow);
    return sequenceFlow;
  }
}
