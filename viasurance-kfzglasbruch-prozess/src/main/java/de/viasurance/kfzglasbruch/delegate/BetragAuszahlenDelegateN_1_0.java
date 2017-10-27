package de.viasurance.kfzglasbruch.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;

import de.viasurance.model.Kontoverbindung;

public class BetragAuszahlenDelegateN_1_0 implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(BetragAuszahlenDelegateN_1_0.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        Kontoverbindung kontoverbindung = (Kontoverbindung) execution.getVariable("ext_kontoverbindung");

        LOGGER.info("Betrag wird auf folgendes Konto ausgezahlt: " + Spin.JSON(kontoverbindung).toString());
    }
}