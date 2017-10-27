package de.viarepair.abrechnungssystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;

import com.vaadin.spring.annotation.EnableVaadin;

import de.viarepair.abrechnungssystem.rest.RestService;

@Configuration
@EnableVaadin
@PropertySource(value = { "classpath:de/viarepair/abrechnungssystem/config/${environment}.properties" })
public class AppConfig {

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource context = new DefaultSpringSecurityContextSource(
                environmentProperties().getLdapUrl());
        context.setUserDn(environmentProperties().getLdapUserDn());
        context.setPassword(environmentProperties().getLdapPassword());
        return context;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource());
        LdapUserSearch userSearch = new FilterBasedLdapUserSearch(environmentProperties().getLdapSearchBase(),
                environmentProperties().getLdapSearchFilter(), contextSource());
        bindAuthenticator.setUserSearch(userSearch);
        return new LdapAuthenticationProvider(bindAuthenticator);
    }

    @Bean
    public RestService restService() {
        return new RestService();
    }

    @Bean
    // Zum Aufloesen von ${...} in @Value
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public EnvironmentProperties environmentProperties() {
        return new EnvironmentProperties();
    }
}