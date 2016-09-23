package de.viasurance.kfzglasbruch.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import de.viasurance.kfzglasbruch.wsclient.PartnersystemWebService;
import de.viasurance.model.Kontoverbindung;
import de.viasurance.model.Vertrag;

public class KontoErmittelnDelegate_1_0 {

    public static final Logger LOGGER = Logger.getLogger(KontoErmittelnDelegate_1_0.class.getName());

    @Autowired
    @Lazy
    private PartnersystemWebService partnersystemWebService;

    public void execute(final DelegateExecution execution) throws Exception {

        final Vertrag vertrag = (Vertrag) execution.getVariable("ext_vertrag");
        final String kundennummer = vertrag.getKundennummer();

        LOGGER.info("Konto wird ermittelt (Kundennummer: " + kundennummer + ")");

        final Kontoverbindung kontoverbindung = partnersystemWebService.getKontoverbindungByKundennummer(kundennummer);

        execution.setVariable("ext_kontoverbindung", kontoverbindung);
    }

    public void setPartnersystemWebService(final PartnersystemWebService partnersystemWebService) {
        this.partnersystemWebService = partnersystemWebService;
    }
}