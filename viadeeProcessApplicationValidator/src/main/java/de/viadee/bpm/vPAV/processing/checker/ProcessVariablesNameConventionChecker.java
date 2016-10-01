package de.viadee.bpm.vPAV.processing.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.camunda.bpm.model.bpmn.instance.BaseElement;

import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.ElementFieldTypes;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.data.CriticalityEnum;
import de.viadee.bpm.vPAV.processing.model.data.ProcessVariable;
import de.viadee.bpm.vPAV.processing.model.data.VariableOperation;

/**
 *
 */
public class ProcessVariablesNameConventionChecker implements ElementChecker {

  private Rule rule;

  public ProcessVariablesNameConventionChecker(final Rule rule) {
    this.rule = rule;
  }

  /**
   * Checks process variables in an bpmn element, whether they comply naming conventions
   * 
   * @param processdefinition
   * @param cl
   */
  @Override
  public Collection<CheckerIssue> check(final BpmnElement element, final ClassLoader cl) {

    // analyse process variables are matching naming conventions
    final Collection<CheckerIssue> issues = checkNamingConvention(element);

    return issues;
  }

  /**
   * Use regular expressions to check process variable conventions
   * 
   * @param element
   * @return issues
   */
  private Collection<CheckerIssue> checkNamingConvention(final BpmnElement element) {

    final Collection<CheckerIssue> issues = new ArrayList<CheckerIssue>();

    final Collection<ElementConvention> elementConventions = rule.getElementConventions();
    if (elementConventions != null) {
      for (final ElementConvention convention : elementConventions) {
        final Pattern pattern = Pattern.compile(convention.getPattern());
        final ElementFieldTypes fieldTypes = convention.getElementFieldTypes();
        final Collection<String> fieldTypeItems = fieldTypes.getElementFieldTypes();
        for (final ProcessVariable variable : element.getProcessVariables().values()) {
          if (variable.getOperation() == VariableOperation.WRITE) {
            if (fieldTypeItems != null) {
              boolean isInRange = false;
              if (fieldTypes.isExcluded()) {
                isInRange = !fieldTypeItems.contains(variable.getFieldType().name());
              } else {
                isInRange = fieldTypeItems.contains(variable.getFieldType().name());
              }
              if (isInRange) {
                final Matcher patternMatcher = pattern.matcher(variable.getName());
                if (!patternMatcher.matches()) {
                  final BaseElement baseElement = element.getBaseElement();
                  issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.WARNING,
                      element.getProcessdefinition(), variable.getResourceFilePath(),
                      baseElement.getId(), baseElement.getAttributeValue("name"),
                      variable.getName(), null, null,
                      "process variable is against the naming convention '" + convention.getName()
                          + "'" + " (compare " + variable.getChapter() + ", "
                          + variable.getFieldType().getDescription() + ")"));
                }
              }
            }
          }
        }
      }
    }

    return issues;
  }
}
