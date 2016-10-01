package de.viadee.bpm.vPAV.processing;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.checker.CheckerFactory;
import de.viadee.bpm.vPAV.processing.checker.ElementChecker;
import de.viadee.bpm.vPAV.processing.checker.ModelChecker;
import de.viadee.bpm.vPAV.processing.checker.ProcessVariablesModelChecker;
import de.viadee.bpm.vPAV.processing.model.data.AnomalyContainer;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.graph.IGraph;
import de.viadee.bpm.vPAV.processing.model.graph.Path;

/**
 * Calls model and element checkers for a concrete bpmn processdefinition
 *
 */
public class BpmnModelDispatcher {

  public static Collection<CheckerIssue> dispatch(final File processdefinition,
      final Map<String, String> decisionRefToPathMap, final Map<String, String> processIdToPathMap,
      final Map<String, String> beanMapping,
      final Map<String, Collection<String>> messageIdToVariables,
      final Map<String, Collection<String>> processIdToVariables,
      final Collection<String> resourcesNewestVersions, final Map<String, Rule> conf,
      final ClassLoader cl) throws ConfigItemNotFoundException {

    // parse bpmn model
    final BpmnModelInstance modelInstance = Bpmn.readModelFromFile(processdefinition);

    // hold bpmn elements
    final Collection<BaseElement> baseElements = modelInstance
        .getModelElementsByType(BaseElement.class);

    final ElementGraphBuilder graphBuilder = new ElementGraphBuilder(decisionRefToPathMap,
        processIdToPathMap, beanMapping, messageIdToVariables, processIdToVariables);

    // create data flow graphs for bpmn model
    final Collection<IGraph> graphCollection = graphBuilder.createProcessGraph(modelInstance,
        processdefinition.getPath(), cl, new ArrayList<String>());

    // add data flow information to graph and calculate invalid paths
    final Map<AnomalyContainer, List<Path>> invalidPathMap = graphBuilder
        .createInvalidPaths(graphCollection);

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    // call model checkers
    // TODO: move it to a factory class later
    final Rule processVariablesModelRule = conf
        .get(getClassName(ProcessVariablesModelChecker.class));
    if (processVariablesModelRule == null)
      throw new ConfigItemNotFoundException(
          getClassName(ProcessVariablesModelChecker.class) + " not found");
    if (processVariablesModelRule.isActive()) {
      final ModelChecker processVarChecker = new ProcessVariablesModelChecker(
          processVariablesModelRule, invalidPathMap);
      issues.addAll(processVarChecker.check(modelInstance, cl));
    }

    // execute element checkers
    for (final BaseElement baseElement : baseElements) {
      BpmnElement element = graphBuilder.getElement(baseElement.getId());
      if (element == null) {
        // if element is not in the data flow graph, create it.
        element = new BpmnElement(processdefinition.getPath(), baseElement);
      }
      final Collection<ElementChecker> checkerCollection = CheckerFactory
          .createCheckerInstancesBpmnElement(conf, beanMapping, resourcesNewestVersions, element);
      for (final ElementChecker checker : checkerCollection) {
        issues.addAll(checker.check(element, cl));
      }
    }

    return issues;
  }

  private static String getClassName(Class<?> clazz) {
    return clazz.getSimpleName();
  }
}
