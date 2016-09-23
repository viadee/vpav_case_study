package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELException;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;

import de.odysseus.el.tree.IdentifierNode;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.impl.Builder;
import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.ProcessingException;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.data.CriticalityEnum;

/**
 * check versioning of the referenced classes, scripts and beans
 *
 */
public class VersioningChecker implements ElementChecker {

  private Rule rule;

  private Map<String, String> beanMapping;

  private Collection<String> resourcesNewestVersions;

  public VersioningChecker(final Rule rule, final Map<String, String> beanMapping,
      final Collection<String> resourcesNewestVersions) {
    this.rule = rule;
    this.beanMapping = beanMapping;
    this.resourcesNewestVersions = resourcesNewestVersions;
  }

  @Override
  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader classLoader) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final BaseElement baseElement = element.getBaseElement();

    // Service Task, Business Task, Send Task
    issues.addAll(checkCommonTasks(element));

    // Script Task
    issues.addAll(checkScriptTask(element));

    // Listener
    final ExtensionElements extensionElements = baseElement.getExtensionElements();

    if (extensionElements != null) {
      // Execution Listener
      issues.addAll(checkExecutionListener(element, extensionElements));

      // Task Listener
      issues.addAll(checkTaskListener(element, extensionElements));
    }

    // Message Event Definition
    issues.addAll(checkMessageEventDefinition(element));

    return issues;
  }

  /**
   * check versioning for execution listener
   * 
   * @param element
   * @param extensionElements
   * @return issues
   */
  private Collection<CheckerIssue> checkExecutionListener(final BpmnElement element,
      final ExtensionElements extensionElements) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    List<CamundaExecutionListener> execListenerList = extensionElements.getElementsQuery()
        .filterByType(CamundaExecutionListener.class).list();
    for (final CamundaExecutionListener listener : execListenerList) {
      final String l_expression = listener.getCamundaExpression();
      if (l_expression != null) {
        prepareBeanWarning(l_expression, element, issues);
      }
      final String l_delegateExpression = listener.getCamundaDelegateExpression();
      if (l_delegateExpression != null) {
        prepareBeanWarning(l_delegateExpression, element, issues);
      }
      final String javaReference = getJavaReference(listener.getCamundaClass());
      if (javaReference != null) {
        prepareClassWarning(javaReference, element, issues);
      }

      final CamundaScript script = listener.getCamundaScript();
      if (script != null && script.getCamundaScriptFormat() != null
          && script.getCamundaScriptFormat().equals("groovy")) {
        final String resourcePath = script.getCamundaResource();
        prepareScriptWarning(resourcePath, element, issues);
      }
    }
    return issues;
  }

  /**
   * check versioning for task listener
   * 
   * @param element
   * @param extensionElements
   * @return issues
   */
  private Collection<CheckerIssue> checkTaskListener(final BpmnElement element,
      final ExtensionElements extensionElements) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    List<CamundaTaskListener> taskListenerList = extensionElements.getElementsQuery()
        .filterByType(CamundaTaskListener.class).list();
    for (final CamundaTaskListener listener : taskListenerList) {
      final String l_expression = listener.getCamundaExpression();
      if (l_expression != null) {
        prepareBeanWarning(l_expression, element, issues);
      }
      final String l_delegateExpression = listener.getCamundaDelegateExpression();
      if (l_delegateExpression != null) {
        prepareBeanWarning(l_delegateExpression, element, issues);
      }
      final String javaReference = getJavaReference(listener.getCamundaClass());
      if (javaReference != null) {
        prepareClassWarning(javaReference, element, issues);
      }

      final CamundaScript script = listener.getCamundaScript();
      if (script != null && script.getCamundaScriptFormat() != null
          && script.getCamundaScriptFormat().equals("groovy")) {
        final String resourcePath = script.getCamundaResource();
        prepareScriptWarning(resourcePath, element, issues);
      }
    }
    return issues;
  }

  /**
   * check versioning for service task, send task or business rule task
   * 
   * @param element
   * @return issues
   */
  private Collection<CheckerIssue> checkCommonTasks(final BpmnElement element) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    final BaseElement baseElement = element.getBaseElement();
    if (baseElement instanceof ServiceTask || baseElement instanceof SendTask
        || baseElement instanceof BusinessRuleTask) {
      // Class, Expression, Delegate Expression
      final String t_expression = baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
          "expression");
      if (t_expression != null) {
        prepareBeanWarning(t_expression, element, issues);
      }

      final String t_delegateExpression = baseElement
          .getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "delegateExpression");
      if (t_delegateExpression != null) {
        prepareBeanWarning(t_delegateExpression, element, issues);
      }
      final String javaReference = getJavaReference(
          baseElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "class"));
      if (javaReference != null) {
        prepareClassWarning(javaReference, element, issues);
      }
    }
    return issues;
  }

  /**
   * check versioning for script task
   * 
   * @param element
   * @return issues
   */
  private Collection<CheckerIssue> checkScriptTask(final BpmnElement element) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    final BaseElement baseElement = element.getBaseElement();
    if (baseElement instanceof ScriptTask) {
      final ScriptTask scriptTask = (ScriptTask) baseElement;
      if (scriptTask.getScriptFormat() != null && scriptTask.getScriptFormat().equals("groovy")) {
        // External Resource
        String resourcePath = scriptTask.getCamundaResource();
        prepareScriptWarning(resourcePath, element, issues);
      }
    }
    return issues;
  }

  /**
   * check versioning for message event
   * 
   * @param element
   * @return
   */
  private Collection<CheckerIssue> checkMessageEventDefinition(final BpmnElement element) {
    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    final BaseElement baseElement = element.getBaseElement();
    if (baseElement instanceof MessageEventDefinition) {
      // Class, Expression, Delegate Expression
      final MessageEventDefinition eventDef = (MessageEventDefinition) baseElement;
      final String javaReference = getJavaReference(eventDef.getCamundaClass());
      if (javaReference != null) {
        prepareClassWarning(javaReference, element, issues);
      }
      final String e_expression = eventDef.getCamundaExpression();
      if (e_expression != null) {
        prepareBeanWarning(e_expression, element, issues);
      }
      final String e_delegateExpression = eventDef.getCamundaDelegateExpression();
      if (e_delegateExpression != null) {
        prepareBeanWarning(e_delegateExpression, element, issues);
      }
    }
    return issues;
  }

  /**
   * convert package format into java file path
   * 
   * @param javaResource
   * @return file path
   */
  private String getJavaReference(final String javaResource) {
    if (javaResource != null) {
      return javaResource.replaceAll("\\.", "/") + ".java";
    }
    return null;
  }

  /**
   * Finds java bean in an expression and returns the java file path
   * 
   * @param expression
   * @return file path
   */
  private String findBeanReferenceInExpression(final String expression, final BpmnElement element,
      final Collection<CheckerIssue> issues) {

    try {
      final String filteredExpression = expression.replaceAll("[\\w]+\\.", "");
      final TreeBuilder treeBuilder = new Builder();
      final Tree tree = treeBuilder.build(filteredExpression);

      final Iterable<IdentifierNode> identifierNodes = tree.getIdentifierNodes();
      final Set<String> paths = new HashSet<String>();
      for (final IdentifierNode node : identifierNodes) {
        if (beanMapping != null) {
          final String packagePath = beanMapping.get(node.getName());
          if (packagePath != null) {
            paths.add(packagePath);
          }
        }
      }
      if (!paths.isEmpty()) {
        return getJavaReference(paths.iterator().next());
      }
    } catch (final ELException e) {
      throw new ProcessingException(
          "el expression " + expression + " in " + element.getProcessdefinition() + ", element ID: "
              + element.getBaseElement().getId() + " couldn't be parsed",
          e);
    }
    return null;
  }

  /**
   * prepares an issue for script after check
   * 
   * @param resourcePath
   * @param element
   * @param issues
   */
  private void prepareScriptWarning(final String resourcePath, final BpmnElement element,
      final Collection<CheckerIssue> issues) {
    if (resourcePath != null) {
      if (!resourcesNewestVersions.contains(resourcePath)) {
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
            element.getProcessdefinition(), resourcePath, element.getBaseElement().getId(),
            element.getBaseElement().getAttributeValue("name"), null, null, null,
            "script reference is deprecated or file with version doesn't exist"));
      }
    }
  }

  /**
   * prepares an issue for bean after check
   * 
   * @param expression
   * @param element
   * @param issues
   */
  private void prepareBeanWarning(final String expression, final BpmnElement element,
      final Collection<CheckerIssue> issues) {
    final String beanReference = findBeanReferenceInExpression(expression, element, issues);
    if (beanReference != null && !resourcesNewestVersions.contains(beanReference)) {
      issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
          element.getProcessdefinition(), beanReference, element.getBaseElement().getId(),
          element.getBaseElement().getAttributeValue("name"), null, null, null,
          "bean reference is deprecated or file with version doesn't exist"));
    }
  }

  /**
   * prepares an issue for class after check
   * 
   * @param javaReference
   * @param element
   * @param issues
   */
  private void prepareClassWarning(final String javaReference, final BpmnElement element,
      final Collection<CheckerIssue> issues) {
    if (javaReference != null) {
      if (!resourcesNewestVersions.contains(javaReference)) {
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
            element.getProcessdefinition(), javaReference, element.getBaseElement().getId(),
            element.getBaseElement().getAttributeValue("name"), null, null, null,
            "class reference is deprecated or file with version doesn't exist"));
      }
    }
  }
}
