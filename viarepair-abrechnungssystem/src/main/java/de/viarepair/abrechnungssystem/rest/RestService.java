package de.viarepair.abrechnungssystem.rest;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import de.viarepair.abrechnungssystem.config.EnvironmentProperties;
import de.viasurance.model.Schadensmeldung;

public class RestService {

    @Autowired
    private EnvironmentProperties environmentProperties;

    public void setEnvironmentProperties(final EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    public String sendeKfzGlasbruchSchadensmeldung(final Schadensmeldung schadensmeldung) throws RestClientException {

        HttpClientBuilder builder = HttpClientBuilder.create();
        if (environmentProperties.isKfzGlasbruchProxyRequired()) {
            final HttpHost proxy = new HttpHost(environmentProperties.getProxyHost(),
                    Integer.parseInt(environmentProperties.getProxyPort()), environmentProperties.getProxyProtocol());
            builder.setProxy(proxy);
        }
        final HttpClient client = builder.build();

        final ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

        final RestTemplate template = new RestTemplate(factory);

        return template.postForObject(environmentProperties.getKfzGlasbruchRestUrl(), schadensmeldung, String.class);
    }
}
