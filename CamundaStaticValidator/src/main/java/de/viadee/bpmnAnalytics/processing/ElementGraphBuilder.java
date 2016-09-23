package de.viadee.bpmnAnalytics.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;

import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.graph.Graph;
import de.viadee.bpmnAnalytics.processing.model.graph.IGraph;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

public class ElementGraphBuilder {

  private Map<String, BpmnElement> elementMap = new HashMap<String, BpmnElement>();

  private Collection<ProcessVariable> variablesRead = new ArrayList<ProcessVariable>();

  private Map<String, String> beanRefToClassMap;

  private Map<String, String> decisionRefToPathMap;

  public ElementGraphBuilder() {
  }

  public ElementGraphBuilder(final Map<String, String> decisionRefToPathMap,
      final Map<String, String> beanMapping) {
    this.decisionRefToPathMap = decisionRefToPathMap;
    this.beanRefToClassMap = beanMapping;
  }

  /**
   * 
   * @param modelInstance
   * @param processdefinition
   * @param cl
   * @return
   */
  public Collection<IGraph> createProcessGraph(final BpmnModelInstance modelInstance,
      final String processdefinition, final ClassLoader cl) {

    final Collection<IGraph> graphCollection = new ArrayList<IGraph>();

    final Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
    for (final Process process : processes) {
      final IGraph graph = new Graph();
      final Collection<FlowElement> elements = process.getFlowElements();
      final Collection<SequenceFlow> flows = new ArrayList<SequenceFlow>();
      final Collection<BoundaryEvent> boundaryEvents = new ArrayList<BoundaryEvent>();
      final Collection<SubProcess> subProcesses = new ArrayList<SubProcess>();

      for (final FlowElement element : elements) {
        if (element instanceof SequenceFlow) {
          // Sequenzflüsse gesondert merken
          final SequenceFlow flow = (SequenceFlow) element;
          flows.add(flow);
        } else if (element instanceof BoundaryEvent) {
          // Boundary Events merken
          BoundaryEvent event = (BoundaryEvent) element;
          boundaryEvents.add(event);
        } else if (element instanceof SubProcess) {
          final SubProcess subprocess = (SubProcess) element;
          addElementsSubprocess(subProcesses, flows, boundaryEvents, graph, subprocess,
              processdefinition, cl);
        }
        // Zu speicherndes Element initialisieren
        final BpmnElement node = new BpmnElement(processdefinition, element);
        // Prozessvariablen ermitteln und nach Zugriffsart zwischenspeichern
        final Map<String, ProcessVariable> variables = new ProcessVariableReader(
            decisionRefToPathMap, beanRefToClassMap).getVariablesFromElement(node, cl);
        // Gelesene Variablen merken
        for (final ProcessVariable var : variables.values()) {
          if (var.isWriteOperation() == false)
            variablesRead.add(var);
        }
        // Prozessvariablen zu Element ermitteln und setzen
        node.setProcessVariables(variables);
        // Element merken
        elementMap.put(element.getId(), node);
        // Prozess-Elemente direkt als Knoten speichern
        graph.addVertex(node);
      }
      // Kanten in Graphen einfügen
      addEdges(processdefinition, graph, flows, boundaryEvents, subProcesses);

      graphCollection.add(graph);
    }

    return graphCollection;
  }

  public Collection<ProcessVariable> getVariablesRead() {
    return variablesRead;
  }

  public BpmnElement getElement(final String id) {
    return elementMap.get(id);
  }

  /**
   * Calculate all invalid paths
   * 
   * TODO: divide between serveral graphs
   * 
   * @param graphCollection
   * @return Map
   */
  public Map<ProcessVariable, List<Path>> calculateAllInvalidPaths(
      final Collection<IGraph> graphCollection) {
    final Map<ProcessVariable, List<Path>> invalidPathMap = new HashMap<ProcessVariable, List<Path>>();

    for (final IGraph g : graphCollection) {
      for (final ProcessVariable p : this.getVariablesRead()) {
        final List<Path> paths = g.getAllInvalidPaths(p.getElement(), p.getName(), 100);

        for (final Path path : paths) {
          // reverse order for a better readability
          Collections.reverse(path.getElements());
        }

        // get existing paths for the variable
        final List<Path> pathsForVariable = invalidPathMap.get(p);
        // add paths only if the length is smaller than the existing or they don't exist
        if (pathsForVariable == null) { // no existing paths
          if (paths != null && paths.size() > 0) { // add paths for variable, if found
            invalidPathMap.put(p, new ArrayList<Path>(paths));
          }
        } else { // there are existing paths
          if (paths != null && paths.size() > 0
              && paths.get(0).getElements().size() < pathsForVariable.get(0).getElements().size()) {
            // add only if existing path is bigger
            invalidPathMap.put(p, new ArrayList<Path>(paths));
          }
        }
      }
    }
    return invalidPathMap;
  }

  /**
   * 
   * @param processdefinition
   * @param graph
   * @param flows
   * @param boundaryEvents
   * @param subProcesses
   */
  private void addEdges(final String processdefinition, final IGraph graph,
      final Collection<SequenceFlow> flows, final Collection<BoundaryEvent> boundaryEvents,
      final Collection<SubProcess> subProcesses) {
    for (final SequenceFlow flow : flows) {
      final BpmnElement flowElement = elementMap.get(flow.getId());
      final BpmnElement srcElement = elementMap.get(flow.getSource().getId());
      final BpmnElement destElement = elementMap.get(flow.getTarget().getId());

      // graph.addEdge(srcElement, flowElement, 100);
      // graph.addEdge(flowElement, destElement, 100);

      graph.addEdge(destElement, flowElement, 100);
      graph.addEdge(flowElement, srcElement, 100);
    }
    for (final BoundaryEvent event : boundaryEvents) {
      final BpmnElement dstElement = elementMap.get(event.getId());
      Activity source = event.getAttachedTo();
      final BpmnElement srcElement = elementMap.get(source.getId());
      // graph.addEdge(srcElement, dstElement, 100);
      graph.addEdge(dstElement, srcElement, 100);
    }
    for (final SubProcess subProcess : subProcesses) {
      final BpmnElement srcElement = elementMap.get(subProcess.getId());
      Collection<StartEvent> startEvents = subProcess.getChildElementsByType(StartEvent.class);
      if (startEvents != null && startEvents.size() > 0) {
        final BpmnElement dstElement = elementMap.get(startEvents.iterator().next().getId());
        // graph.addEdge(srcElement, dstElement, 100);
        graph.addEdge(dstElement, srcElement, 100);
      }
    }
  }

  /**
   * 
   * @param subProcesses
   * @param flows
   * @param graph
   * @param process
   * @param processdefinitionPath
   * @param cl
   */
  private void addElementsSubprocess(final Collection<SubProcess> subProcesses,
      final Collection<SequenceFlow> flows, final Collection<BoundaryEvent> events,
      final IGraph graph, final SubProcess process, final String processdefinitionPath,
      final ClassLoader cl) {
    subProcesses.add(process);
    final Collection<FlowElement> subElements = process.getFlowElements();
    for (final FlowElement subElement : subElements) {
      if (subElement instanceof SubProcess) {
        final SubProcess subProcess = (SubProcess) subElement;
        addElementsSubprocess(subProcesses, flows, events, graph, subProcess, processdefinitionPath,
            cl);
      } else if (subElement instanceof SequenceFlow) {
        final SequenceFlow flow = (SequenceFlow) subElement;
        flows.add(flow);
      } else if (subElement instanceof BoundaryEvent) {
        final BoundaryEvent boundaryEvent = (BoundaryEvent) subElement;
        events.add(boundaryEvent);
      }
      // Elemente des Subprozesses als Knoten aufnehmen
      final BpmnElement node = new BpmnElement(processdefinitionPath, subElement);
      // Prozessvariablen ermitteln und nach Zugriffsart zwischenspeichern
      final Map<String, ProcessVariable> variables = new ProcessVariableReader(decisionRefToPathMap,
          beanRefToClassMap).getVariablesFromElement(node, cl);
      // Gelesene Variablen merken
      for (final ProcessVariable var : variables.values()) {
        if (var.isWriteOperation() == false)
          variablesRead.add(var);
      }
      // Prozessvariablen zu Element ermitteln und setzen
      node.setProcessVariables(variables);
      // Element merken
      elementMap.put(subElement.getId(), node);
      // Prozess-Elemente direkt als Knoten speichern
      graph.addVertex(node);
    }
  }
}
