package de.viadee.bpm.vPAV;

import org.springframework.context.ApplicationContext;

import de.viadee.bpm.vPAV.beans.BeanMappingGenerator;

public class ProcessApplicationValidator extends AbstractRunner {

    public static boolean assertBPMModelConsistency(ApplicationContext ctx) {

        beanMapping = BeanMappingGenerator.generateBeanMappingFile(ctx);
        retrieveClassLoader();
        run_vPAV();

        if (AbstractRunner.getfilteredIssues().isEmpty()) {
            return true;
        }
        return false;
    }

    public static void retrieveClassLoader() {
        classLoader = ProcessApplicationValidator.class.getClassLoader();
    }

}
