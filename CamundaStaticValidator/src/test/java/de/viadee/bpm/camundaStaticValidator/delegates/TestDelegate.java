package de.viadee.bpm.camundaStaticValidator.delegates;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class TestDelegate implements org.camunda.bpm.engine.delegate.JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    // TODO Auto-generated method stub
    execution.setVariable("dshfhdsfhfds", true);
    execution.getVariable("dshfhdsfds");
  }

}
