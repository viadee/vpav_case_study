import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.IdentityService;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import de.viasurance.kfzglasbruch.config.EnvironmentProperties;
import de.viasurance.kfzglasbruch.delegate.BetragAuszahlenDelegateN_1_0;
import de.viasurance.kfzglasbruch.delegate.CorrectDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.KontoErmittelnDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.LeistungsanspruchPruefenDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.NoInterfaceDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.NoVersionDelegate;
import de.viasurance.kfzglasbruch.delegate.ReadExternalDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.SachbearbeiterInformierenDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.VersioningDelegate_1_0;
import de.viasurance.kfzglasbruch.delegate.VertragErmittelnDelegate_1_0;
import de.viasurance.kfzglasbruch.listener.ProzessGestartetListener_1_0;
import de.viasurance.kfzglasbruch.listener.VsnrManuellErmitteltListener_1_0;

@Configuration
public class SpringTestConfig {

    public SpringTestConfig() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    private KontoErmittelnDelegate_1_0 kontoErmittelnDelegate;

    // @InjectMocks
    // private SchadenAnlegenDelegate schadenAnlegenDelegate;

    @InjectMocks
    private VertragErmittelnDelegate_1_0 vertragErmittelnDelegate;

    @InjectMocks
    private LeistungsanspruchPruefenDelegate_1_0 leistungsanspruchPruefenDelegate;

    @InjectMocks
    private BetragAuszahlenDelegateN_1_0 betragAuszahlenDelegate;

    @InjectMocks
    private SachbearbeiterInformierenDelegate_1_0 sachbearbeiterInformierenDelegate;

    @InjectMocks
    private ProzessGestartetListener_1_0 prozessGestartetListener;

    @InjectMocks
    private VsnrManuellErmitteltListener_1_0 vsnrManuellErmitteltListener;

    @InjectMocks
    private CorrectDelegate_1_0 correctDelegate;

    @InjectMocks
    private NoInterfaceDelegate_1_0 noInterfaceDelegate;

    @InjectMocks
    private ReadExternalDelegate_1_0 readExternalDelegate;

    @InjectMocks
    private NoVersionDelegate noVersionDelegate;

    @InjectMocks
    private VersioningDelegate_1_0 versioningDelegate;

    @Bean
    public NoVersionDelegate noVersionDelegate() {
        return noVersionDelegate;
    }

    @Bean
    public VersioningDelegate_1_0 versioningDelegate() {
        return versioningDelegate;
    }

    @Bean
    public KontoErmittelnDelegate_1_0 kontoErmittelnDelegate() {
        return kontoErmittelnDelegate;
    }

    @Bean
    public ReadExternalDelegate_1_0 readExternalDelegate() {
        return readExternalDelegate;
    }

    @Bean
    public NoInterfaceDelegate_1_0 noInterfaceDelegate() {
        return noInterfaceDelegate;
    }

    @Bean
    public CorrectDelegate_1_0 correctDelegate() {
        return correctDelegate;
    }

    // @Bean
    // public SchadenAnlegenDelegate schadenAnlegenDelegate() {
    // return schadenAnlegenDelegate;
    // }

    @Bean
    public VertragErmittelnDelegate_1_0 vertragErmittelnDelegate() {
        return vertragErmittelnDelegate;
    }

    @Bean
    public LeistungsanspruchPruefenDelegate_1_0 leistungsanspruchPruefenDelegate() {
        return leistungsanspruchPruefenDelegate;
    }

    @Bean
    public SachbearbeiterInformierenDelegate_1_0 sachbearbeiterInformierenDelegate() {
        return sachbearbeiterInformierenDelegate;
    }

    @Bean
    public ProzessGestartetListener_1_0 prozessGestartetListener() {
        return prozessGestartetListener;
    }

    @Bean
    public VsnrManuellErmitteltListener_1_0 vsnrManuellErmitteltListener() {
        return vsnrManuellErmitteltListener;
    }

    @Bean
    public EnvironmentProperties environmentProperties() {
        return null;
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

    @Bean
    public JavaMailSender mailSender() {
        return null;
    }

    @Bean
    public IdentityService identityService() {
        return null;
    }
}
