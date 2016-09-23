package de.viasurance.kfzglasbruch.listener;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import de.viasurance.kfzglasbruch.ProcessVariableConstants;

public class ProzessGestartetListener_1_0 implements ExecutionListener {

    private static final Logger LOGGER = Logger.getLogger(ProzessGestartetListener_1_0.class.getName());

    @Override
    public void notify(DelegateExecution execution) throws Exception {

        String instanceId = execution.getProcessInstanceId();

        ObjectValue kunde = (ObjectValue) execution.getVariable("ext_kunde");
        ObjectValue kfz = (ObjectValue) execution.getVariable(ProcessVariableConstants.KFZ);
        ObjectValue schaden = (ObjectValue) execution.getVariable("ext_schaden");
        ObjectValue anhang = (ObjectValue) execution.getVariable("ext_anhang");

        LOGGER.info("Prozess KfzGlasbruch gestartet mit ID: " + instanceId + "\nKunde: " + kunde.getValueSerialized()
                + "\nKfz: " + kfz.getValueSerialized() + "\nSchaden: " + schaden.getValueSerialized() + "\nAnhang: "
                + anhang.getValueSerialized() + "\nVSNR: " + execution.getVariable("ext_vsnr"));
    }
}