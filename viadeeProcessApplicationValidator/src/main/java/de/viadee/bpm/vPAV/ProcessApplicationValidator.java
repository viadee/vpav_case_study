package de.viadee.bpm.vPAV;

import java.util.Collection;

import org.springframework.context.ApplicationContext;

import de.viadee.bpm.vPAV.beans.BeanMappingGenerator;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

public class ProcessApplicationValidator extends AbstractRunner {

    /*
     * run vPAV with given ApplicationContext (Spring)
     *
     * @param ApplicationContext
     */
    public static Collection<CheckerIssue> assertBPMModelConsistency(ApplicationContext ctx) {

        beanMapping = BeanMappingGenerator.generateBeanMappingFile(ctx);
        retrieveClassLoader();
        run_vPAV();

        return AbstractRunner.getfilteredIssues();
    }

    /*
     * run vPAV (no Spring)
     */
    public static boolean assertBPMModelConsistency() {

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
