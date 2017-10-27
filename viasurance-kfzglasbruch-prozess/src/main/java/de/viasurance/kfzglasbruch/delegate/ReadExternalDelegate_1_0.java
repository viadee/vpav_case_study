package de.viasurance.kfzglasbruch.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ReadExternalDelegate_1_0 implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariable("variableAgainstExt", 5);
    }

}
