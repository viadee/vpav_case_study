package de.viasurance.kfzglasbruch.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;

public class LeseVsnr {

    public static void leseVariable(final DelegateExecution execution) {
        execution.getVariable("ext_vsnummer"); // UR-Anomalie, da es die Variable nicht gibt
    }

}
