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
    public static Collection<CheckerIssue> findModelInconsistencies(ApplicationContext ctx) {

        beanMapping = BeanMappingGenerator.generateBeanMappingFile(ctx);
        retrieveClassLoader();
        run_vPAV();

        return AbstractRunner.getfilteredIssues();
    }

    /*
     * run vPAV (no Spring)
     */
    public static Collection<CheckerIssue> findModelInconsistencies() {

        retrieveClassLoader();
        run_vPAV();

        return AbstractRunner.getfilteredIssues();
    }

    public static void retrieveClassLoader() {
        classLoader = ProcessApplicationValidator.class.getClassLoader();
    }

}
