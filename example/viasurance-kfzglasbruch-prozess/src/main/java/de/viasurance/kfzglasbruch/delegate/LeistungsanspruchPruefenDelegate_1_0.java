package de.viasurance.kfzglasbruch.delegate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.velocity.app.VelocityEngine;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.Spin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.web.client.RestTemplate;

import de.viasurance.kfzglasbruch.config.EnvironmentProperties;
import de.viasurance.model.Deckungstyp;
import de.viasurance.model.Vertrag;
import de.viasurance.model.Zahlungsstatus;

public class LeistungsanspruchPruefenDelegate_1_0 implements JavaDelegate {

    private final static Logger LOGGER = Logger.getLogger(LeistungsanspruchPruefenDelegate_1_0.class.getName());

    @Autowired
    private EnvironmentProperties environmentProperties;

    @Autowired
    private VelocityEngine velocityEngine;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        LOGGER.info("Leistungsanspruch wird geprueft");

        // KIE Execution Server erfordert Anmeldung mit User und Passwort
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(environmentProperties.getKieServerUser(),
                environmentProperties.getKieServerPassword()));

        HttpClientBuilder builder = HttpClientBuilder.create().setDefaultCredentialsProvider(provider);

        if (environmentProperties.isKieServerProxyRequired()) {
            HttpHost proxy = new HttpHost(environmentProperties.getProxyHost(),
                    Integer.valueOf(environmentProperties.getProxyPort()), environmentProperties.getProxyProtocol());
            builder = builder.setProxy(proxy);
        }

        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(builder.build());
        RestTemplate template = new RestTemplate(factory);

        Vertrag v = (Vertrag) execution.getVariable("ext_vertrag");
        Map<String, Object> model = new HashMap<>();
        model.put("teilkaskoVorhanden", v.getDeckungstyp() == Deckungstyp.TEILKASKO);
        model.put("vollkaskoVorhanden", v.getDeckungstyp() == Deckungstyp.VOLLKASKO);
        model.put("mahnstatusGesetzt", v.getZahlungsstatus() == Zahlungsstatus.MAHNUNG);

        String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                "de/viasurance/kfzglasbruch/templates/kie-server-template.vm", "UTF-8", model);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_XML.toString());
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = template.postForEntity(environmentProperties.getKieServerRestUrl(),
                requestEntity, String.class);

        // XML-Reponse parsen
        boolean leistungsanspruchVorhanden = Boolean.valueOf(Spin.XML(Spin.XML(response.getBody()).textContent())
                .xPath("//de.viadee.kfzglasbruch.Pruefergebnis/leistungsanspruchVorhanden/text()").string());

        LOGGER.info("Leistungsanspruch vorhanden? -> " + (leistungsanspruchVorhanden ? "JA" : "NEIN"));

        execution.setVariable("ext_leistungsanspruchVorhanden", leistungsanspruchVorhanden);

        execution.removeVariable("ext_vertrag");

        LeseVsnr.leseVariable(execution);
    }

    public void setEnvironmentProperties(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }
}