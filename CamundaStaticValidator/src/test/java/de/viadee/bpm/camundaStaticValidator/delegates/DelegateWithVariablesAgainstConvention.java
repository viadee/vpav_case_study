package de.viadee.bpm.camundaStaticValidator.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class DelegateWithVariablesAgainstConvention implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {

    execution.setVariable("extBlub", true);
  }
}
