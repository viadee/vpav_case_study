package de.viadee.bpmnAnalytics.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class DelegateWithCorrectVariable implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {

    execution.getVariable("ext_Blub");
  }
}
