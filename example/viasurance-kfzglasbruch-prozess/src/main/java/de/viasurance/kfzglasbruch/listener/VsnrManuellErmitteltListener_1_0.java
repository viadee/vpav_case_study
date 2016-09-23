package de.viasurance.kfzglasbruch.listener;

import java.util.logging.Logger;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class VsnrManuellErmitteltListener_1_0 implements TaskListener {

    private static final Logger LOGGER = Logger.getLogger(VsnrManuellErmitteltListener_1_0.class.getName());

    @Autowired
    private IdentityService identityService;

    @Override
    public void notify(DelegateTask delegateTask) {

        LOGGER.info("VSNR wurde manuell ermittelt");

        // E-Mail-Adresse des Benutzers, der den Task abgeschlossen hat,
        // ermitteln und in den Prozesskontext schreiben. An diese Adresse wird
        // später die Benachrichtigung gesendet.

        User user = identityService.createUserQuery().userId(delegateTask.getAssignee()).singleResult();

        delegateTask.setVariable("ext_useremail", user.getEmail());
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }
}