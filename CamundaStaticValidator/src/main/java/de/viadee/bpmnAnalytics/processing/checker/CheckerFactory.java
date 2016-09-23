package de.viadee.bpmnAnalytics.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.Task;

import de.viadee.bpmnAnalytics.config.model.Rule;
import de.viadee.bpmnAnalytics.processing.ConfigItemNotFoundException;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;

/**
 * Factory decides which Checkers will be used in defined situations
 *
 */
public final class CheckerFactory {

  /**
   * create checkers
   * 
   * @param ruleConf
   * @param element
   * @return Checkers
   */
  public static Collection<ElementChecker> createCheckerInstancesBpmnElement(
      final Map<String, Rule> ruleConf, final Map<String, String> beanMapping,
      final Collection<String> resourcesNewestVersions, final BpmnElement element)
      throws ConfigItemNotFoundException {

    final Collection<ElementChecker> checkers = new ArrayList<ElementChecker>();
    final BaseElement baseElement = element.getBaseElement();
    if (baseElement == null) {
      throw new RuntimeException("Bpmn Element couldn't be found");
    }

    final Rule javaDelegateRule = ruleConf.get(getClassName(JavaDelegateChecker.class));
    if (javaDelegateRule == null)
      throw new ConfigItemNotFoundException(getClassName(JavaDelegateChecker.class) + " not found");

    if ((baseElement instanceof ServiceTask || baseElement instanceof SendTask
        || baseElement instanceof ReceiveTask || baseElement instanceof ScriptTask
        || baseElement instanceof BusinessRuleTask) && javaDelegateRule.isActive()) {
      checkers.add(new JavaDelegateChecker(javaDelegateRule, beanMapping));
    }

    final Rule processVariablesNameConventionRule = ruleConf
        .get(getClassName(ProcessVariablesNameConventionChecker.class));
    if (processVariablesNameConventionRule == null)
      throw new ConfigItemNotFoundException(
          getClassName(ProcessVariablesNameConventionChecker.class) + " not found");
    if (processVariablesNameConventionRule.isActive()) {
      checkers.add(new ProcessVariablesNameConventionChecker(processVariablesNameConventionRule));
    }

    final Rule taskNamingConventionRule = ruleConf
        .get(getClassName(TaskNamingConventionChecker.class));
    if (taskNamingConventionRule == null)
      throw new ConfigItemNotFoundException(
          getClassName(TaskNamingConventionChecker.class) + " not found");
    if (baseElement instanceof Task && taskNamingConventionRule.isActive()) {
      checkers.add(new TaskNamingConventionChecker(taskNamingConventionRule));
    }

    final Rule dmnTaskRule = ruleConf.get(getClassName(DmnTaskChecker.class));
    if (dmnTaskRule == null)
      throw new ConfigItemNotFoundException(getClassName(DmnTaskChecker.class) + " not found");
    if (baseElement instanceof BusinessRuleTask && dmnTaskRule.isActive()) {
      checkers.add(new DmnTaskChecker(dmnTaskRule));
    }

    final Rule versioningRule = ruleConf.get(getClassName(VersioningChecker.class));
    if (versioningRule == null)
      throw new ConfigItemNotFoundException(getClassName(VersioningChecker.class) + " not found");
    if (versioningRule.isActive()) {
      checkers.add(new VersioningChecker(versioningRule, beanMapping, resourcesNewestVersions));
    }

    final Rule embeddedGroovyScriptRule = ruleConf
        .get(getClassName(EmbeddedGroovyScriptChecker.class));
    if (embeddedGroovyScriptRule == null)
      throw new ConfigItemNotFoundException(
          getClassName(EmbeddedGroovyScriptChecker.class) + " not found");
    if (embeddedGroovyScriptRule.isActive()) {
      checkers.add(new EmbeddedGroovyScriptChecker(embeddedGroovyScriptRule));
    }

    return checkers;
  }

  private static String getClassName(Class<?> clazz) {
    return clazz.getSimpleName();
  }
}
