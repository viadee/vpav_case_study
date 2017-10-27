package de.viasurance.kfzglasbruch.config;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguration der camunda-Beans.
 */
@Configuration
public class ProcessEngineConfig {

    // Die destroyMethod muss leer sein, da von Spring sonst automatisch die
    // close-Methode der processEngine-Bean aufgerufen wird, wenn die Anwendung
    // neu deployed wird. Dies führt dazu, dass im Cockpit und der Tasklist die
    // Process Engine nicht mehr verfügbar ist.
    @Bean(destroyMethod = "")
    public ProcessEngine processEngine() {
        return BpmPlatform.getProcessEngineService().getDefaultProcessEngine();
    }

    @Bean
    public RuntimeService runtimeService() {
        return processEngine().getRuntimeService();
    }

    @Bean
    public IdentityService identityService() {
        return processEngine().getIdentityService();
    }
}