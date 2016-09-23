package de.viasurance.kfzglasbruch.config;

import org.springframework.beans.factory.annotation.Value;

public class EnvironmentProperties {

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.protocol}")
    private String proxyProtocol;

    @Value("${proxy.port}")
    private String proxyPort;

    @Value("${kieServer.proxyRequired}")
    private boolean kieServerProxyRequired;

    @Value("${kieServer.restUrl}")
    private String kieServerRestUrl;

    @Value("${kieServer.user}")
    private String kieServerUser;

    @Value("${kieServer.password}")
    private String kieServerPassword;

    @Value("${email.host}")
    private String emailHost;

    @Value("${email.port}")
    private int emailPort;

    @Value("${email.protocol}")
    private String emailProtocol;

    @Value("${email.defaultAddress}")
    private String emailDefaultAddress;

    @Value("${email.from}")
    private String emailFrom;

    @Value("${email.subject}")
    private String emailSubject;

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyProtocol() {
        return proxyProtocol;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getArchivOrdner() {
        final String property = "java.io.tmpdir";
        return System.getProperty(property);
    }

    public boolean isKieServerProxyRequired() {
        return kieServerProxyRequired;
    }

    public String getKieServerRestUrl() {
        return kieServerRestUrl;
    }

    public String getKieServerUser() {
        return kieServerUser;
    }

    public String getKieServerPassword() {
        return kieServerPassword;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public int getEmailPort() {
        return emailPort;
    }

    public String getEmailProtocol() {
        return emailProtocol;
    }

    public String getEmailDefaultAddress() {
        return emailDefaultAddress;
    }

    public String getEmailFrom() {
        return emailFrom;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

}