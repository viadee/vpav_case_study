package de.viasurance.kfzglasbruch.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import de.viasurance.kfzglasbruch.wsclient.VertragssystemWebService;
import de.viasurance.model.Vertrag;

public class VertragErmittelnDelegate_1_1 implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(VertragErmittelnDelegate_1_1.class.getName());

    @Autowired
    @Lazy
    private VertragssystemWebService vertragssystemWebService;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {

        final String vsnr = (String) execution.getVariable("ext_vsnr");

        LOGGER.info("Vertrag wird abgerufen (VSNR: " + vsnr + ")");

        final Vertrag vertrag = vertragssystemWebService.getVertragByVsnr(vsnr);

        LOGGER.info("Vertragsdaten: " + Spin.JSON(vertrag).toString());

        execution.setVariable("ext_vertrag", vertrag);
    }

    public void setVertragssystemWebService(final VertragssystemWebService vertragssystemWebService) {
        this.vertragssystemWebService = vertragssystemWebService;
    }
}