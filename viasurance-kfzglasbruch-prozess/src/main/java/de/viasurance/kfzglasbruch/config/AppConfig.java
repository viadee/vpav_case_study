package de.viasurance.kfzglasbruch.config;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import de.viasurance.kfzglasbruch.delegate.KontoErmittelnDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.LeistungsanspruchPruefenDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.SachbearbeiterInformierenDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.VertragErmittelnDelegate_1_0;
import de.viasurance.kfzglasbruch.listener.ProzessGestartetListener_1_0;
import de.viasurance.kfzglasbruch.listener.VsnrManuellErmitteltListener_1_0;
import de.viasurance.kfzglasbruch.wsclient.Partnersystem;
import de.viasurance.kfzglasbruch.wsclient.PartnersystemWebService;
import de.viasurance.kfzglasbruch.wsclient.Vertragssystem;
import de.viasurance.kfzglasbruch.wsclient.VertragssystemWebService;

/**
 * Hauptkonfigurationsklasse der Anwendung.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.viasurance.kfzglasbruch.rest")
@Import(value = { ProcessEngineConfig.class })
@PropertySource(value = { "classpath:de/viasurance/kfzglasbruch/config/${environment}.properties" })
public class AppConfig {

    @Bean
    // Zum Aufloesen von ${...} in @Value
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public EnvironmentProperties environmentProperties() {
        return new EnvironmentProperties();
    }

    @Bean
    public SpringServletProcessApplication kfzGlasbruchProcessApplication() {
        return new SpringServletProcessApplication();
    }

    @Bean
    public KontoErmittelnDelegate_1_0 kontoErmittelnDelegate() {
        return new KontoErmittelnDelegate_1_0();
    }

    // @Bean
    // public SchadenAnlegenDelegate schadenAnlegenDelegate() {
    // return new SchadenAnlegenDelegate();
    // }

    @Bean
    public VertragErmittelnDelegate_1_0 vertragErmittelnDelegate() {
        return new VertragErmittelnDelegate_1_0();
    }

    @Bean
    public LeistungsanspruchPruefenDelegate_1_0 leistungsanspruchPruefenDelegate() {
        return new LeistungsanspruchPruefenDelegate_1_0();
    }

    @Bean
    public SachbearbeiterInformierenDelegate_1_0 sachbearbeiterInformierenDelegate() {
        return new SachbearbeiterInformierenDelegate_1_0();
    }

    @Bean
    public ProzessGestartetListener_1_0 prozessGestartetListener() {
        return new ProzessGestartetListener_1_0();
    }

    @Bean
    public VsnrManuellErmitteltListener_1_0 vsnrManuellErmitteltListener() {
        return new VsnrManuellErmitteltListener_1_0();
    }

    @Bean
    @Lazy
    public VertragssystemWebService vertragssystemWebService() {
        return new Vertragssystem().getVertragssystemWebServicePort();
    }

    @Bean
    @Lazy
    public PartnersystemWebService partnersystemWebService() {
        return new Partnersystem().getPartnersystemWebServicePort();
    }

    @Bean
    public JavaMailSender mailSender() {
        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(environmentProperties().getEmailHost());
        mailSender.setPort(environmentProperties().getEmailPort());
        mailSender.setProtocol(environmentProperties().getEmailProtocol());
        mailSender.setDefaultEncoding("UTF-8");
        return mailSender;
    }

    @Bean
    public VelocityEngineFactoryBean velocityEngine() {
        final VelocityEngineFactoryBean velocityEngine = new VelocityEngineFactoryBean();
        final Map<String, Object> velocityProperties = new HashMap<>();
        velocityProperties.put("resource.loader", "class");
        velocityProperties.put("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.setVelocityPropertiesMap(velocityProperties);
        return velocityEngine;
    }
}
