package de.viadee.bpmnAnalytics;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.junit.BeforeClass;
import org.junit.Test;

import de.viadee.bpmnAnalytics.processing.ProcessVariableReader;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.data.VariableOperation;
import junit.framework.Assert;

public class ProcessVariableReaderTest {

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

  @Test
  public void testRecogniseVariablesInClass() {
    final String PATH = BASE_PATH + "ProcessVariableReaderTest_RecogniseVariablesInClass.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<ServiceTask> allServiceTasks = modelInstance
        .getModelElementsByType(ServiceTask.class);

    final ProcessVariableReader variableReader = new ProcessVariableReader(null, null);

    final BpmnElement element = new BpmnElement(PATH, allServiceTasks.iterator().next());
    final Map<String, ProcessVariable> variables = variableReader.getVariablesFromElement(element,
        cl);
    Assert.assertEquals(2, variables.size());
  }

  @Test
  public void testRecogniseInputOutputAssociations() {
    final String PATH = BASE_PATH + "ProcessVariableReaderTest_InputOutputCallActivity.bpmn";

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(new File(PATH));

    final Collection<CallActivity> allServiceTasks = modelInstance
        .getModelElementsByType(CallActivity.class);

    final ProcessVariableReader variableReader = new ProcessVariableReader(null, null);

    final BpmnElement element = new BpmnElement(PATH, allServiceTasks.iterator().next());
    final Map<String, ProcessVariable> variables = variableReader.getVariablesFromElement(element,
        cl);

    final ProcessVariable nameOfVariableInMainProcess = variables
        .get("nameOfVariableInMainProcess");
    Assert.assertNotNull(nameOfVariableInMainProcess);
    Assert.assertEquals(VariableOperation.WRITE, nameOfVariableInMainProcess.getOperation());

    final ProcessVariable nameOfVariableInMainProcess2 = variables
        .get("nameOfVariableInMainProcess2");
    Assert.assertNotNull(nameOfVariableInMainProcess2);
    Assert.assertEquals(VariableOperation.WRITE, nameOfVariableInMainProcess2.getOperation());

    final ProcessVariable someVariableInMainProcess = variables.get("someVariableInMainProcess");
    Assert.assertNotNull(someVariableInMainProcess);
    Assert.assertEquals(VariableOperation.READ, someVariableInMainProcess.getOperation());

    final ProcessVariable someVariableInMainProcess2 = variables.get("someVariableInMainProcess2");
    Assert.assertNotNull(someVariableInMainProcess2);
    Assert.assertEquals(VariableOperation.READ, someVariableInMainProcess2.getOperation());
  }
}
