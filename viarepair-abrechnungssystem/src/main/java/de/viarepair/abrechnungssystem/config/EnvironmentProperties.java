package de.viarepair.abrechnungssystem.config;

import org.springframework.beans.factory.annotation.Value;

public class EnvironmentProperties {

    @Value("${proxy.host}")
    private String proxyHost;

    @Value("${proxy.protocol}")
    private String proxyProtocol;

    @Value("${proxy.port}")
    private String proxyPort;

    @Value("${kfzGlasbruch.restUrl}")
    private String kfzGlasbruchRestUrl;

    @Value("${kfzGlasbruch.proxyRequired}")
    private boolean kfzGlasbruchProxyRequired;

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.userDn}")
    private String ldapUserDn;

    @Value("${ldap.password}")
    private String ldapPassword;

    @Value("${ldap.searchBase}")
    private String ldapSearchBase;

    @Value("${ldap.searchFilter}")
    private String ldapSearchFilter;

    public String getProxyHost() {
        return proxyHost;
    }

    public String getProxyProtocol() {
        return proxyProtocol;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public String getKfzGlasbruchRestUrl() {
        return kfzGlasbruchRestUrl;
    }

    public boolean isKfzGlasbruchProxyRequired() {
        return kfzGlasbruchProxyRequired;
    }

    public String getLdapUrl() {
        return ldapUrl;
    }

    public String getLdapUserDn() {
        return ldapUserDn;
    }

    public String getLdapPassword() {
        return ldapPassword;
    }

    public String getLdapSearchBase() {
        return ldapSearchBase;
    }

    public String getLdapSearchFilter() {
        return ldapSearchFilter;
    }
}