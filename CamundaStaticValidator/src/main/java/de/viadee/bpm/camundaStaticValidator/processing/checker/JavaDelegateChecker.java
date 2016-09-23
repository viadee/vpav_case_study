package de.viadee.bpm.camundaStaticValidator.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.odysseus.el.tree.IdentifierNode;
import de.odysseus.el.tree.Tree;
import de.odysseus.el.tree.TreeBuilder;
import de.odysseus.el.tree.impl.Builder;
import de.viadee.bpm.camundaStaticValidator.config.model.Rule;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.BpmnElement;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.CheckerIssue;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.CriticalityEnum;

/**
 * Class JavaDelegateChecker
 * 
 * Checks a bpmn model, if code references (java delegates) for tasks have been set correctly.
 *
 */
public class JavaDelegateChecker implements ElementChecker {

  private final Rule rule;

  private Map<String, String> beanMapping;

  public JavaDelegateChecker(final Rule rule, final Map<String, String> beanMapping) {
    this.rule = rule;
    this.beanMapping = beanMapping;
  }

  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader classLoader) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final BaseElement bpmnElement = element.getBaseElement();

    // read attributes from task
    final String classAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "class");
    final String delegateExprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "delegateExpression");
    final String exprAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS,
        "expression");
    final String typeAttr = bpmnElement.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "type");

    if (classAttr != null) {
      if (classAttr.trim().length() == 0 && delegateExprAttr == null && exprAttr == null
          && typeAttr == null) {
        // Error, because no class has been configured
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
            element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
            bpmnElement.getAttributeValue("name"), null, null, null,
            "task '" + bpmnElement.getAttributeValue("name") + "' with no class name"));
      }
      if (classAttr.trim().length() > 0) {
        issues.addAll(checkClassFile(element, classLoader, classAttr));
      }
    }
    if (delegateExprAttr != null) {
      // check validity of a bean
      if (beanMapping != null) {
        final String filteredExpression = delegateExprAttr.replaceAll("[\\w]+\\.", "");
        final TreeBuilder treeBuilder = new Builder();
        final Tree tree = treeBuilder.build(filteredExpression);
        final Iterable<IdentifierNode> identifierNodes = tree.getIdentifierNodes();
        for (final IdentifierNode node : identifierNodes) {
          final String classFile = beanMapping.get(node.getName());
          if (classFile != null && classFile.trim().length() > 0) {
            issues.addAll(checkClassFile(element, classLoader, classFile));
          }
        }
      }
    }
    if (classAttr == null && delegateExprAttr == null && exprAttr == null && typeAttr == null) {
      // No technical attributes have been added
      issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
          element.getProcessdefinition(), null, bpmnElement.getAttributeValue("id"),
          bpmnElement.getAttributeValue("name"), null, null, null,
          "task '" + bpmnElement.getAttributeValue("name") + "' with no code reference yet"));
    }
    return issues;
  }

  private Collection<CheckerIssue> checkClassFile(final BpmnElement element,
      final ClassLoader classLoader, final String className) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();
    final BaseElement bpmnElement = element.getBaseElement();
    final String classPath = className.replaceAll("\\.", "/") + ".java";

    // If a class path has been found, check the correctness
    try {
      Class<?> clazz = classLoader.loadClass(className);

      // Checks, whether the correct interface was implemented
      Class<?>[] interfaces = clazz.getInterfaces();
      boolean javaDelegateImplemented = false;
      for (final Class<?> _interface : interfaces) {
        if (_interface.getName().contains("JavaDelegate")) {
          javaDelegateImplemented = true;
        }
      }
      if (javaDelegateImplemented == false) {
        // class implements not the interface "JavaDelegate"
        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
            element.getProcessdefinition(), classPath, bpmnElement.getAttributeValue("id"),
            bpmnElement.getAttributeValue("name"), null, null, null,
            "class for task '" + bpmnElement.getAttributeValue("name")
                + "' does not implement interface JavaDelegate"));
      }

    } catch (final ClassNotFoundException e) {
      // Throws an error, if the class was not found
      issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
          element.getProcessdefinition(), classPath, bpmnElement.getAttributeValue("id"),
          bpmnElement.getAttributeValue("name"), null, null, null,
          "class for task '" + bpmnElement.getAttributeValue("name") + "' not found"));
    }

    return issues;
  }
}
