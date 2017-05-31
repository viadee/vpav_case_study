package de.viadee.bpm.vPAV;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestApplicationConfiguration {

    @Bean
    public String ersteBean() {
        return "I am a java delegate class";
    }

    @Bean
    public String zweiteBean() {
        return "I am another java delegate class";
    }

}
