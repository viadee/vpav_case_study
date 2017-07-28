
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.viadee.bpm.vPAV.ProcessApplicationValidator;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
public class ModelConsistencyTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void errorsInModelMustBeFound() {
        Collection<CheckerIssue> issues = ProcessApplicationValidator.findModelInconsistencies(ctx);
        Collection<CheckerIssue> filteredIssues = new ArrayList<CheckerIssue>();
        filteredIssues.addAll(issues);

        assertTrue("More or less issues were found than expected", issues.size() == 13);

        // VersioningChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("VersioningChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("VersioningChecker doesn't work correct", filteredIssues.size() == 6);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // JavaDelegateChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("JavaDelegateChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("JavaDelegateChecker doesn't work correct", filteredIssues.size() == 4);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // ProcessVariablesNameConventionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("ProcessVariablesNameConventionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("ProcessVariablesNameConventionChecker doesn't work correct", filteredIssues.size() == 1);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // EmbeddedGroovyScriptChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("EmbeddedGroovyScriptChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("EmbeddedGroovyScriptChecker doesn't work correct", filteredIssues.size() == 1);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // XorNamingConventionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("XorNamingConventionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("XorNamingConventionChecker doesn't work correct", filteredIssues.size() == 1);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // NoScriptChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("NoScriptChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("NoScriptChecker doesn't work correct", filteredIssues.size() == 0);

    }
}
