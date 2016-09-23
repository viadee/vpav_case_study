package de.viadee.bpmnAnalytics.processing;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELException;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.CompletionCondition;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.LoopCardinality;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.Script;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.dmn.Dmn;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.Decision;
import org.camunda.bpm.model.dmn.instance.InputExpression;
import org.camunda.bpm.model.dmn.instance.Output;
import org.camunda.bpm.model.dmn.instance.Text;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import de.odysseus.el.tree.IdentifierNode;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.impl.Builder;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.ElementChapter;
import de.viadee.bpmnAnalytics.processing.model.data.KnownElementFieldType;
import de.viadee.bpmnAnalytics.processing.model.data.ProcessVariable;

/**
 * search process variables for an bpmn element
 *
 */
public final class ProcessVariableReader {

  private final Map<String, String> decisionRefToPathMap;

  private final Map<String, String> beanMapping;

  public ProcessVariableReader(final Map<String, String> decisionRefToPathMap,
      final Map<String, String> beanMapping) {
    this.decisionRefToPathMap = decisionRefToPathMap;
    this.beanMapping = beanMapping;
  }

  /**
   * Examining an bpmn element for variables
   * 
   * @return variables
   */
  public Map<String, ProcessVariable> getVariablesFromElement(final BpmnElement element,
      final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();

    // 1) Search variables in task
    processVariables.putAll(getVariablesFromTask(element, cl));
    // 2) Search variables in sequence flow
    processVariables.putAll(searchVariablesFromSequenceFlow(element, cl));
    // 3) Search variables in ExtensionElements
    processVariables.putAll(searchExtensionsElements(element, cl));

    return processVariables;
  }

  /**
   * Analyse bpmn extension elements for variables
   * 
   * @param element
   * @param cl
   *          ClassLoader
   * @return variables
   */
  private Map<String, ProcessVariable> searchExtensionsElements(final BpmnElement element,
      final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();
    final BaseElement baseElement = element.getBaseElement();
    final BpmnModelElementInstance scopeElement = baseElement.getScope();
    String scopeElementId = null;
    if (scopeElement != null) {
      scopeElementId = scopeElement.getAttributeValue("id");
    }
    final ExtensionElements extensionElements = baseElement.getExtensionElements();
    if (extensionElements != null) {
      // 1) Search in Execution Listeners
      processVariables.putAll(
          getVariablesFromExecutionListener(element, extensionElements, scopeElementId, cl));

      // 2) Search in Task Listeners
      processVariables
          .putAll(getVariablesFromTaskListener(element, extensionElements, scopeElementId, cl));

      // 3) Search in Form Data
      processVariables.putAll(getVariablesFromFormData(element, extensionElements, scopeElementId));
    }

    return processVariables;
  }

  /**
   * get process variables from execution listeners
   * 
   * @param extensionElements
   * @param processdefinition
   * @param elementId
   * @param cl
   *          ClassLoader
   * @return variables
   */
  private Map<String, ProcessVariable> getVariablesFromExecutionListener(final BpmnElement element,
      final ExtensionElements extensionElements, final String scopeId, final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();
    List<CamundaExecutionListener> listenerList = extensionElements.getElementsQuery()
        .filterByType(CamundaExecutionListener.class).list();
    for (final CamundaExecutionListener listener : listenerList) {
      final String l_expression = listener.getCamundaExpression();
      if (l_expression != null) {
        processVariables.putAll(findVariablesInExpression(l_expression, element,
            ElementChapter.ExecutionListener, KnownElementFieldType.Expression, scopeId, cl));
      }
      final String l_delegateExpression = listener.getCamundaDelegateExpression();
      if (l_delegateExpression != null) {
        processVariables.putAll(findVariablesInExpression(l_delegateExpression, element,
            ElementChapter.ExecutionListener, KnownElementFieldType.DelegateExpression, scopeId,
            cl));
      }
      processVariables.putAll(getVariablesFromJavaDelegate(listener.getCamundaClass(), element,
          ElementChapter.ExecutionListener, KnownElementFieldType.Class, scopeId, cl));

      final CamundaScript script = listener.getCamundaScript();
      if (script != null && script.getCamundaScriptFormat().equals("groovy")) {
        // inline script or external file?
        final String inlineScript = script.getTextContent();
        if (inlineScript != null && inlineScript.trim().length() > 0) {
          processVariables
              .putAll(searchProcessVariablesInCode(element, ElementChapter.ExecutionListener,
                  KnownElementFieldType.InlineScript, null, scopeId, inlineScript));
        } else {
          final String resourcePath = script.getCamundaResource();
          if (resourcePath != null) {
            processVariables.putAll(getVariablesFromGroovyScript(resourcePath, element,
                ElementChapter.ExecutionListener, KnownElementFieldType.ExternalScript, scopeId,
                cl));
          }
        }
      }
    }
    return processVariables;
  }

  /**
   * get process variables from task listeners
   * 
   * TODO: generalise this method eventually
   * 
   * @param extensionElements
   * @param processdefinition
   * @param elementId
   * @param cl
   *          ClassLoader
   * @return variables
   */
  private Map<String, ProcessVariable> getVariablesFromTaskListener(final BpmnElement element,
      final ExtensionElements extensionElements, final String scopeId, final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();
    List<CamundaTaskListener> listenerList = extensionElements.getElementsQuery()
        .filterByType(CamundaTaskListener.class).list();
    for (final CamundaTaskListener listener : listenerList) {
      final String l_expression = listener.getCamundaExpression();
      if (l_expression != null) {
        processVariables.putAll(findVariablesInExpression(l_expression, element,
            ElementChapter.TaskListener, KnownElementFieldType.Expression, scopeId, cl));
      }
      final String l_delegateExpression = listener.getCamundaDelegateExpression();
      if (l_delegateExpression != null) {
        processVariables.putAll(findVariablesInExpression(l_delegateExpression, element,
            ElementChapter.TaskListener, KnownElementFieldType.DelegateExpression, scopeId, cl));
      }
      processVariables.putAll(getVariablesFromJavaDelegate(listener.getCamundaClass(), element,
          ElementChapter.TaskListener, KnownElementFieldType.Class, scopeId, cl));

      final CamundaScript script = listener.getCamundaScript();
      if (script != null && script.getCamundaScriptFormat().equals("groovy")) {
        // inline script or external file?
        final String inlineScript = script.getTextContent();
        if (inlineScript != null && inlineScript.trim().length() > 0) {
          processVariables.putAll(searchProcessVariablesInCode(element, ElementChapter.TaskListener,
              KnownElementFieldType.InlineScript, null, scopeId, inlineScript));
        } else {
          final String resourcePath = script.getCamundaResource();
          if (resourcePath != null) {
            processVariables.putAll(getVariablesFromGroovyScript(resourcePath, element,
                ElementChapter.TaskListener, KnownElementFieldType.ExternalScript, scopeId, cl));
          }
        }
      }
    }

    return processVariables;
  }

  /**
   * get process variables from form fields (user tasks)
   * 
   * @param extensionElements
   * @param processdefinition
   * @param elementId
   * @return variables
   */
  private Map<String, ProcessVariable> getVariablesFromFormData(final BpmnElement element,
      final ExtensionElements extensionElements, final String scopeElementId) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();

    final Query<CamundaFormData> formDataQuery = extensionElements.getElementsQuery()
        .filterByType(CamundaFormData.class);
    if (formDataQuery.count() > 0) {
      final CamundaFormData formData = formDataQuery.singleResult();
      if (formData != null) {
        final Collection<CamundaFormField> formFields = formData.getCamundaFormFields();
        for (final CamundaFormField field : formFields) {
          processVariables.put(field.getCamundaId(),
              new ProcessVariable(field.getCamundaId(), element, ElementChapter.FormData,
                  KnownElementFieldType.FormField, null, true, scopeElementId));
        }
      }
    }

    return processVariables;
  }

  /**
   * get process variables from sequence flow conditions
   * 
   * @param element
   * @param cl
   * @return variables
   */
  private Map<String, ProcessVariable> searchVariablesFromSequenceFlow(final BpmnElement element,
      final ClassLoader cl) {

    Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();
    final BaseElement baseElement = element.getBaseElement();
    if (baseElement instanceof SequenceFlow) {
      final SequenceFlow flow = (SequenceFlow) baseElement;
      BpmnModelElementInstance scopeElement = flow.getScope();
      String scopeId = null;
      if (scopeElement != null) {
        scopeId = scopeElement.getAttributeValue("id");
      }
      final ConditionExpression expression = flow.getConditionExpression();
      if (expression != null) {
        if (expression.getLanguage() != null && expression.getLanguage().equals("groovy")) {
          // inline script or external file?
          final String inlineScript = expression.getTextContent();
          if (inlineScript != null && inlineScript.trim().length() > 0) {
            variables.putAll(searchProcessVariablesInCode(element, ElementChapter.Details,
                KnownElementFieldType.InlineScript, scopeId, null, inlineScript));
          } else {
            final String resourcePath = expression.getCamundaResource();
            if (resourcePath != null) {
              variables.putAll(getVariablesFromGroovyScript(resourcePath, element,
                  ElementChapter.Details, KnownElementFieldType.ExternalScript, scopeId, cl));
            }
          }
        } else {
          if (expression.getTextContent().trim().length() > 0) {
            variables = findVariablesInExpression(expression.getTextContent(), element,
                ElementChapter.Details, KnownElementFieldType.Expression, scopeId, cl);
          }
        }
      }
    }
    return variables;
  }

  /**
   * Analyse all types of tasks for process variables
   * 
   * @param element
   * @param cl
   *          ClassLoader
   * @return variables
   */
  private Map<String, ProcessVariable> getVariablesFromTask(final BpmnElement element,
      final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();

    final BaseElement baseElement = element.getBaseElement();
    BpmnModelElementInstance scopeElement = baseElement.getScope();
    String scopeId = null;
    if (scopeElement != null) {
      scopeId = scopeElement.getAttributeValue("id");
    }
    if (baseElement instanceof ServiceTask || baseElement instanceof SendTask
        || baseElement instanceof BusinessRuleTask) {
      final String t_expression = baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
          "expression");
      if (t_expression != null) {

        processVariables.putAll(findVariablesInExpression(t_expression, element,
            ElementChapter.Details, KnownElementFieldType.Expression, scopeId, cl));
      }

      final String t_delegateExpression = baseElement
          .getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "delegateExpression");
      if (t_delegateExpression != null) {
        processVariables.putAll(findVariablesInExpression(t_delegateExpression, element,
            ElementChapter.Details, KnownElementFieldType.DelegateExpression, scopeId, cl));
      }
      final String t_resultVariable = baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
          "resultVariable");
      if (t_resultVariable != null && t_resultVariable.trim().length() > 0) {
        processVariables.put(t_resultVariable, new ProcessVariable(t_resultVariable, element,
            ElementChapter.Details, KnownElementFieldType.ResultVariable, null, true, scopeId));
      }
      processVariables.putAll(getVariablesFromJavaDelegate(
          baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "class"), element,
          ElementChapter.Details, KnownElementFieldType.Class, scopeId, cl));

      if (baseElement instanceof BusinessRuleTask) {
        final String t_decisionRef = baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
            "decisionRef");
        if (t_decisionRef != null && t_decisionRef.trim().length() > 0
            && decisionRefToPathMap != null) {
          final String fileName = decisionRefToPathMap.get(t_decisionRef);
          if (fileName != null) {
            processVariables.putAll(readDmnFile(t_decisionRef, fileName, element,
                ElementChapter.Details, KnownElementFieldType.DMN, scopeId, cl));
          }
        }
      }

    } else if (baseElement instanceof UserTask) {
      final UserTask userTask = (UserTask) baseElement;
      final String assignee = userTask.getCamundaAssignee();
      if (assignee != null)
        processVariables.putAll(findVariablesInExpression(assignee, element, ElementChapter.Details,
            KnownElementFieldType.Assignee, scopeId, cl));
      final String candidateUsers = userTask.getCamundaCandidateUsers();
      if (candidateUsers != null)
        processVariables.putAll(findVariablesInExpression(candidateUsers, element,
            ElementChapter.Details, KnownElementFieldType.CandidateUsers, scopeId, cl));
      final String candidateGroups = userTask.getCamundaCandidateGroups();
      if (candidateGroups != null)
        processVariables.putAll(findVariablesInExpression(candidateGroups, element,
            ElementChapter.Details, KnownElementFieldType.CandidateGroups, scopeId, cl));
      final String dueDate = userTask.getCamundaDueDate();
      if (dueDate != null)
        processVariables.putAll(findVariablesInExpression(dueDate, element, ElementChapter.Details,
            KnownElementFieldType.DueDate, scopeId, cl));
      final String followUpDate = userTask.getCamundaFollowUpDate();
      if (followUpDate != null)
        processVariables.putAll(findVariablesInExpression(followUpDate, element,
            ElementChapter.Details, KnownElementFieldType.FollowUpDate, scopeId, cl));

    } else if (baseElement instanceof ScriptTask) {
      // Examine script task for process variables
      final ScriptTask scriptTask = (ScriptTask) baseElement;
      if (scriptTask.getScriptFormat() != null && scriptTask.getScriptFormat().equals("groovy")) {
        // inline script or external file?
        final Script script = scriptTask.getScript();
        if (script != null && script.getTextContent() != null
            && script.getTextContent().trim().length() > 0) {
          processVariables.putAll(searchProcessVariablesInCode(element, ElementChapter.Details,
              KnownElementFieldType.InlineScript, null, scopeId, script.getTextContent()));
        } else {
          final String resourcePath = scriptTask.getCamundaResource();
          if (resourcePath != null) {
            processVariables.putAll(getVariablesFromGroovyScript(resourcePath, element,
                ElementChapter.Details, KnownElementFieldType.ExternalScript, scopeId, cl));
          }
        }
      }
      String resultVariable = scriptTask.getCamundaResultVariable();
      if (resultVariable != null && resultVariable.trim().length() > 0) {
        processVariables.put(resultVariable, new ProcessVariable(resultVariable, element,
            ElementChapter.Details, KnownElementFieldType.ResultVariable, null, true, scopeId));
      }
    } else if (baseElement instanceof CallActivity) {
      final CallActivity callActivity = (CallActivity) baseElement;
      final String calledElement = callActivity.getCalledElement();
      if (calledElement != null && calledElement.trim().length() > 0) {
        processVariables.putAll(findVariablesInExpression(calledElement, element,
            ElementChapter.Details, KnownElementFieldType.CalledElement, scopeId, cl));
      }
      final String caseRef = callActivity.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
          "caseRef");
      if (caseRef != null && caseRef.trim().length() > 0) {
        processVariables.putAll(findVariablesInExpression(caseRef, element, ElementChapter.Details,
            KnownElementFieldType.CaseRef, scopeId, cl));
      }
    }

    // Check multi instance attributes
    processVariables.putAll(searchVariablesInMultiInstanceTask(element, cl));

    return processVariables;
  }

  /**
   * Examine multi instance tasks for process variables
   * 
   * @param element
   * @return variables
   */
  private Map<String, ProcessVariable> searchVariablesInMultiInstanceTask(final BpmnElement element,
      final ClassLoader cl) {

    final Map<String, ProcessVariable> processVariables = new HashMap<String, ProcessVariable>();

    final BaseElement baseElement = element.getBaseElement();
    BpmnModelElementInstance scopeElement = baseElement.getScope();
    String scopeId = null;
    if (scopeElement != null) {
      scopeId = scopeElement.getAttributeValue("id");
    }
    final ModelElementInstance loopCharacteristics = baseElement
        .getUniqueChildElementByType(LoopCharacteristics.class);
    if (loopCharacteristics != null) {
      final String collectionName = loopCharacteristics
          .getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "collection");
      if (collectionName != null && collectionName.trim().length() > 0) {
        processVariables.put(collectionName,
            new ProcessVariable(collectionName, element, ElementChapter.MultiInstance,
                KnownElementFieldType.CollectionElement, null, false, scopeId));
      }
      final String elementVariable = loopCharacteristics
          .getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "elementVariable");
      if (elementVariable != null && elementVariable.trim().length() > 0) {
        processVariables.put(elementVariable,
            new ProcessVariable(elementVariable, element, ElementChapter.MultiInstance,
                KnownElementFieldType.ElementVariable, null, false, scopeId));
      }
      final ModelElementInstance loopCardinality = loopCharacteristics
          .getUniqueChildElementByType(LoopCardinality.class);
      if (loopCardinality != null) {
        final String cardinality = loopCardinality.getTextContent();
        if (cardinality != null && cardinality.trim().length() > 0) {
          processVariables.putAll(findVariablesInExpression(cardinality, element,
              ElementChapter.MultiInstance, KnownElementFieldType.LoopCardinality, scopeId, cl));
        }
      }
      final ModelElementInstance completionCondition = loopCharacteristics
          .getUniqueChildElementByType(CompletionCondition.class);
      if (completionCondition != null) {
        final String completionConditionExpression = completionCondition.getTextContent();
        if (completionConditionExpression != null
            && completionConditionExpression.trim().length() > 0) {
          processVariables.putAll(findVariablesInExpression(completionConditionExpression, element,
              ElementChapter.MultiInstance, KnownElementFieldType.CompletionCondition, scopeId,
              cl));
        }
      }
    }
    return processVariables;
  }

  /**
   * Checks a java delegate for process variable references (read/write).
   * 
   * Constraints: Method examine only variables in java delegate and not in the method references
   * process variables with names, which only could be determined at runtime, can't be analysed.
   * e.g. execution.setVariable(execution.getActivityId() + "-" + execution.getEventName(), true)
   * 
   * @param classFile
   * @param element
   * @param cl
   *          ClassLoader
   * @return variables
   * @throws MalformedURLException
   */
  private Map<String, ProcessVariable> getVariablesFromJavaDelegate(final String classFile,
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String scopeId, final ClassLoader cl) {
    // convert package format in a concrete path to the java class (.java)
    String filePath = "";
    if (classFile != null && classFile.trim().length() > 0) {
      filePath = classFile.replaceAll("\\.", "/") + ".java";
    }
    final Map<String, ProcessVariable> variables = readResourceFile(filePath, element, chapter,
        fieldType, scopeId, cl);
    return variables;
  }

  /**
   * Checks an external groovy script for process variables (read/write).
   * 
   * @param groovyFile
   * @param cl
   *          ClassLoader
   * @return variables
   */
  private Map<String, ProcessVariable> getVariablesFromGroovyScript(final String groovyFile,
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String scopeId, final ClassLoader cl) {

    final Map<String, ProcessVariable> variables = readResourceFile(groovyFile, element, chapter,
        fieldType, scopeId, cl);
    return variables;
  }

  /**
   * Reads a resource file from class path
   * 
   * @param fileName
   * @param element
   * @param cl
   * @return variables
   */
  private Map<String, ProcessVariable> readResourceFile(final String fileName,
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String scopeId, final ClassLoader cl) {
    Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();
    if (fileName != null && fileName.trim().length() > 0) {
      final InputStream resource = cl.getResourceAsStream(fileName);
      if (resource != null) {
        try {
          final String methodBody = IOUtils.toString(cl.getResourceAsStream(fileName));
          variables = searchProcessVariablesInCode(element, chapter, fieldType, fileName, scopeId,
              methodBody);
        } catch (final IOException ex) {
          throw new RuntimeException(
              "resource '" + fileName + "' could not be read: " + ex.getMessage());
        }
      }
    }
    return variables;
  }

  /**
   * Scans a dmn file for process variables
   * 
   * @param filePath
   * @return
   */
  private Map<String, ProcessVariable> readDmnFile(final String decisionId, final String fileName,
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String scopeId, final ClassLoader cl) {

    final Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();

    if (fileName != null && fileName.trim().length() > 0) {
      final InputStream resource = cl.getResourceAsStream(fileName);
      if (resource != null) {
        // parse dmn model
        final DmnModelInstance modelInstance = Dmn.readModelFromStream(resource);
        final Decision decision = modelInstance.getModelElementById(decisionId);
        final Collection<InputExpression> inputExpressions = decision.getModelInstance()
            .getModelElementsByType(InputExpression.class);
        for (final InputExpression inputExpression : inputExpressions) {
          final Text variable = inputExpression.getText();
          variables.put(variable.getTextContent(), new ProcessVariable(variable.getTextContent(),
              element, chapter, fieldType, fileName, false, scopeId));
        }
        final Collection<Output> outputs = decision.getModelInstance()
            .getModelElementsByType(Output.class);
        for (final Output output : outputs) {
          final String variable = output.getName();
          variables.put(variable,
              new ProcessVariable(variable, element, chapter, fieldType, fileName, true, scopeId));
        }
      }
    }
    return variables;
  }

  /**
   * Examine java code for process variables
   * 
   * @param element
   * @param fileName
   * @param code
   * @return variables
   */
  private Map<String, ProcessVariable> searchProcessVariablesInCode(final BpmnElement element,
      final ElementChapter chapter, final KnownElementFieldType fieldType, final String fileName,
      final String scopeId, final String code) {

    final Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();
    variables.putAll(
        searchReadProcessVariablesInCode(element, chapter, fieldType, fileName, scopeId, code));
    variables.putAll(
        searchWrittenProcessVariablesInCode(element, chapter, fieldType, fileName, scopeId, code));

    return variables;
  }

  /**
   * search read process variables
   * 
   * @param element
   * @param fileName
   * @param code
   * @return
   */
  private Map<String, ProcessVariable> searchReadProcessVariablesInCode(final BpmnElement element,
      final ElementChapter chapter, final KnownElementFieldType fieldType, final String fileName,
      final String scopeId, final String code) {

    final Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();

    // remove white spaces from code
    final String cleanedCode = code.replaceAll(" ", "");

    // search locations where variables are read
    final Pattern getVariablePattern = Pattern.compile("(\\.getVariable\\()(.*?)(\\))");
    final Matcher matcher = getVariablePattern.matcher(cleanedCode);

    final String FILTER_PATTERN = "'|\"";
    while (matcher.find()) {
      final String filteredMatch = matcher.group(2).replaceAll(FILTER_PATTERN, "");
      variables.put(filteredMatch, new ProcessVariable(filteredMatch, element, chapter, fieldType,
          fileName, false, scopeId));
    }

    return variables;
  }

  /**
   * search written process variables
   * 
   * @param element
   * @param fileName
   * @param code
   * @return
   */
  private Map<String, ProcessVariable> searchWrittenProcessVariablesInCode(
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String fileName, final String scopeId,
      final String code) {

    final Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();

    // remove white spaces from code
    final String cleanedCode = code.replaceAll(" ", "");

    // search locations where variables are written
    final Pattern setVariablePattern = Pattern.compile("(\\.setVariable\\((.*?),)(.*?)(\\))");
    final Matcher matcher = setVariablePattern.matcher(cleanedCode);

    final String FILTER_PATTERN = "'|\"";
    while (matcher.find()) {
      final String filteredMatch = matcher.group(2).replaceAll(FILTER_PATTERN, "");
      variables.put(filteredMatch,
          new ProcessVariable(filteredMatch, element, chapter, fieldType, fileName, true, scopeId));
    }

    return variables;
  }

  /**
   * Examine JUEL expressions for variables
   * 
   * @param expression
   * @param element
   * @return variables
   * @throws ProcessingException
   */
  private Map<String, ProcessVariable> findVariablesInExpression(final String expression,
      final BpmnElement element, final ElementChapter chapter,
      final KnownElementFieldType fieldType, final String scopeId, final ClassLoader cl) {
    final Map<String, ProcessVariable> variables = new HashMap<String, ProcessVariable>();

    try {
      // remove object name from method calls, otherwise the method arguments could not be found
      final String filteredExpression = expression.replaceAll("[\\w]+\\.", "");
      final TreeBuilder treeBuilder = new Builder();
      final Tree tree = treeBuilder.build(filteredExpression);

      final Iterable<IdentifierNode> identifierNodes = tree.getIdentifierNodes();
      for (final IdentifierNode node : identifierNodes) {
        // checks, if found variable is a bean
        final String className = isBean(node.getName());
        if (className != null) {
          // read variables in class file (bean)
          variables.putAll(
              getVariablesFromJavaDelegate(className, element, chapter, fieldType, scopeId, cl));
        } else {
          // save variable
          variables.put(node.getName(), new ProcessVariable(node.getName(), element, chapter,
              fieldType, null, false, scopeId));
        }
      }
      // extract written variables
      variables.putAll(searchWrittenProcessVariablesInCode(element, chapter, fieldType, null,
          scopeId, expression));
    } catch (final ELException e) {
      throw new ProcessingException("el expression " + expression + " in "
          + element.getProcessdefinition() + ", element ID: " + element.getBaseElement().getId()
          + ", Type: " + fieldType.getDescription() + " couldn't be parsed", e);
    }

    return variables;
  }

  /**
   * Checks a variable being a bean
   * 
   * @param variable
   * @return classpath to bean definition
   */
  private String isBean(final String variable) {
    if (beanMapping != null) {
      return beanMapping.get(variable);
    }
    return null;
  }
}
