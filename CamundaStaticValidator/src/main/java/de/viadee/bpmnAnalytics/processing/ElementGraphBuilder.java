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
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.Message;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.SubProcess;

import de.viadee.bpmnAnalytics.processing.model.data.AnomalyContainer;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.ElementChapter;
import de.viadee.bpmnAnalytics.processing.model.data.KnownElementFieldType;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;
import de.viadee.bpmnAnalytics.processing.model.data.VariableOperation;
import de.viadee.bpmnAnalytics.processing.model.graph.Graph;
import de.viadee.bpmnAnalytics.processing.model.graph.IGraph;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

public class ElementGraphBuilder {

  private Map<String, BpmnElement> elementMap = new HashMap<String, BpmnElement>();

  private Collection<BpmnElement> startNodes = new ArrayList<BpmnElement>();

  private Map<String, String> beanRefToClassMap;

  private Map<String, String> decisionRefToPathMap;

  private Map<String, Collection<String>> messageIdToVariables;

  private Map<String, Collection<String>> processIdToVariables;

  public ElementGraphBuilder() {
  }

  public ElementGraphBuilder(final Map<String, String> decisionRefToPathMap,
      final Map<String, String> beanMapping,
      final Map<String, Collection<String>> messageIdToVariables,
      final Map<String, Collection<String>> processIdToVariables) {
    this.decisionRefToPathMap = decisionRefToPathMap;
    this.beanRefToClassMap = beanMapping;
    this.messageIdToVariables = messageIdToVariables;
    this.processIdToVariables = processIdToVariables;
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
          // mention sequence flows
          final SequenceFlow flow = (SequenceFlow) element;
          flows.add(flow);
        } else if (element instanceof BoundaryEvent) {
          // mention boundary events
          BoundaryEvent event = (BoundaryEvent) element;
          boundaryEvents.add(event);
        } else if (element instanceof SubProcess) {
          final SubProcess subprocess = (SubProcess) element;
          addElementsSubprocess(subProcesses, flows, boundaryEvents, graph, subprocess,
              processdefinition, cl);
        }
        // initialize element
        final BpmnElement node = new BpmnElement(processdefinition, element);
        // examine process variables and save it with access operation
        final Map<String, ProcessVariable> variables = new ProcessVariableReader(
            decisionRefToPathMap, beanRefToClassMap).getVariablesFromElement(node, cl);
        // examine process variables for element and set it
        node.setProcessVariables(variables);
        // mention element
        elementMap.put(element.getId(), node);
        if (element.getElementType().getTypeName().equals("startEvent")) {
          // add process variables for start event, which set by call startProcessInstanceByMessage
          addProcessVariablesByStartForMessageId(element, node);

          // add process variables for start event, which set by call startProcessInstanceByKey
          final String processId = node.getBaseElement().getParentElement().getAttributeValue("id");
          addProcessVariablesByStartForProcessId(node, processId);

          startNodes.add(node);
        }
        // save process elements as a node
        graph.addVertex(node);
      }
      // add edges into the graph
      addEdges(processdefinition, graph, flows, boundaryEvents, subProcesses);

      graphCollection.add(graph);
    }

    return graphCollection;
  }

  private void addProcessVariablesByStartForProcessId(final BpmnElement node,
      final String processId) {
    if (processIdToVariables != null && processId != null) {
      final Collection<String> outerVariables = processIdToVariables.get(processId);
      // variablen hinzufügen
      if (outerVariables != null) {
        for (final String varName : outerVariables) {
          node.setProcessVariable(varName,
              new ProcessVariable(varName, node, ElementChapter.OutstandingVariable,
                  KnownElementFieldType.Class, null, VariableOperation.WRITE, ""));
        }
      }
    }
  }

  private void addProcessVariablesByStartForMessageId(final FlowElement element,
      final BpmnElement node) {
    if (messageIdToVariables != null) {
      if (element instanceof StartEvent) {
        final StartEvent startEvent = (StartEvent) element;
        final Collection<MessageEventDefinition> messageEventDefinitions = startEvent
            .getChildElementsByType(MessageEventDefinition.class);
        for (MessageEventDefinition eventDef : messageEventDefinitions) {
          final Message message = eventDef.getMessage();
          final String messageName = message.getName();
          final Collection<String> outerVariables = messageIdToVariables.get(messageName);
          if (outerVariables != null) {
            for (final String varName : outerVariables) {
              node.setProcessVariable(varName,
                  new ProcessVariable(varName, node, ElementChapter.OutstandingVariable,
                      KnownElementFieldType.Class, null, VariableOperation.WRITE, ""));
            }
          }
        }
      }
    }
  }

  public BpmnElement getElement(final String id) {
    return elementMap.get(id);
  }

  /**
   * create invalid paths for data flow anomalies
   * 
   * @param graphCollection
   * @return
   */
  public Map<AnomalyContainer, List<Path>> createInvalidPaths(
      final Collection<IGraph> graphCollection) {
    final Map<AnomalyContainer, List<Path>> invalidPathMap = new HashMap<AnomalyContainer, List<Path>>();

    for (final IGraph g : graphCollection) {
      // add data flow information to graph
      g.setAnomalyInformation(startNodes.iterator().next());
      // get nodes with data anomalies
      final Map<BpmnElement, List<AnomalyContainer>> anomalies = g.getNodesWithAnomalies();

      for (final BpmnElement element : anomalies.keySet()) {
        for (AnomalyContainer anomaly : anomalies.get(element)) {
          // create paths for data flow anomalies
          final List<Path> paths = g.getAllInvalidPaths(element, anomaly);
          for (final Path path : paths) {
            // reverse order for a better readability
            Collections.reverse(path.getElements());
          }
          invalidPathMap.put(anomaly, new ArrayList<Path>(paths));
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

      graph.addEdge(srcElement, flowElement, 100);
      graph.addEdge(flowElement, destElement, 100);
    }
    for (final BoundaryEvent event : boundaryEvents) {
      final BpmnElement dstElement = elementMap.get(event.getId());
      final Activity source = event.getAttachedTo();
      final BpmnElement srcElement = elementMap.get(source.getId());
      graph.addEdge(srcElement, dstElement, 100);
    }
    for (final SubProcess subProcess : subProcesses) {
      final BpmnElement subprocessElement = elementMap.get(subProcess.getId());
      // integration of a subprocess in data flow graph
      // inner elements will be directly connected into the graph
      final Collection<StartEvent> startEvents = subProcess
          .getChildElementsByType(StartEvent.class);
      final Collection<EndEvent> endEvents = subProcess.getChildElementsByType(EndEvent.class);
      if (startEvents != null && startEvents.size() > 0 && endEvents != null
          && endEvents.size() > 0) {
        final Collection<SequenceFlow> incomingFlows = subProcess.getIncoming();
        for (final SequenceFlow incomingFlow : incomingFlows) {
          final BpmnElement srcElement = elementMap.get(incomingFlow.getId());
          for (final StartEvent startEvent : startEvents) {
            final BpmnElement dstElement = elementMap.get(startEvent.getId());
            graph.addEdge(srcElement, dstElement, 100);
            graph.removeEdge(srcElement, subprocessElement);
          }
        }
        final Collection<SequenceFlow> outgoingFlows = subProcess.getOutgoing();
        for (final EndEvent endEvent : endEvents) {
          final BpmnElement srcElement = elementMap.get(endEvent.getId());
          for (final SequenceFlow outgoingFlow : outgoingFlows) {
            final BpmnElement dstElement = elementMap.get(outgoingFlow.getId());
            graph.addEdge(srcElement, dstElement, 100);
            graph.removeEdge(subprocessElement, dstElement);
          }
        }
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
      // Prozessvariablen zu Element ermitteln und setzen
      node.setProcessVariables(variables);
      // Element merken
      elementMap.put(subElement.getId(), node);
      // Prozess-Elemente direkt als Knoten speichern
      graph.addVertex(node);
    }
  }
}
