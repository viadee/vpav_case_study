package de.viasurance.kfzglasbruch.delegate;

import java.io.File;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import de.viasurance.kfzglasbruch.config.EnvironmentProperties;
import de.viasurance.model.Schaden;

public class SachbearbeiterInformierenDelegate_1_0 implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(SachbearbeiterInformierenDelegate_1_0.class.getName());

    @Autowired
    private EnvironmentProperties environmentProperties;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public void execute(final DelegateExecution execution) throws Exception {

        String userEmail = (String) execution.getVariable("ext_useremail");

        execution.setVariable("localTest", true);

        execution.removeVariable("localTest");

        final String to = (userEmail != null) ? userEmail : environmentProperties.getEmailDefaultAddress();

        LOGGER.info("Sachbearbeiter wird per E-Mail informiert (E-Mail-Adresse: " + to + ")");

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                message.setTo(to);
                message.setFrom(environmentProperties.getEmailFrom());
                message.setSubject(environmentProperties.getEmailSubject());

                // Gespeicherte Datei als Anhang hinzufügen
                String dateiname = (String) execution.getVariable("dateiname");
                message.addAttachment(dateiname,
                        Paths.get(environmentProperties.getArchivOrdner() + File.separator + dateiname).toFile());

                Map<String, Object> model = new HashMap<>(execution.getVariables());

                // Schadenshöhe wird als Euro-Betrag formatiert
                Schaden schaden = (Schaden) execution.getVariable("ext_schaden");
                model.put("schadenshoeheFormatiert",
                        DecimalFormat.getCurrencyInstance().format(Double.valueOf(schaden.getSchadenshoehe()) / 100));

                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                        "de/viasurance/kfzglasbruch/templates/email-template.vm", "UTF-8", model);

                message.setText(text, true);
            }
        };

        mailSender.send(preparator);
    }

    public void setEnvironmentProperties(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}