
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

    private final int anzVersioning = 5;

    private final int anzDelegate = 9;

    private final int anzVariablesNameConvention = 5;

    private final int anzGroovyScript = 3;

    private final int anzXorNaming = 4;

    private final int anzNoScript = 5;

    private final int anzDmnTask = 3;

    private final int anzTimerExpression = 3;

    private final int anzElementId = 2;

    private final int anzProcessVariables = 6;

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void validateModel() {
        assertTrue("Model inconsistency found. Please check target folder for validation output",
                ProcessApplicationValidator.findModelErrors(ctx).isEmpty());
    }

    @Test
    public void errorsInModelMustBeFound() {
        Collection<CheckerIssue> issues = ProcessApplicationValidator.findModelInconsistencies(ctx);
        Collection<CheckerIssue> filteredIssues = new ArrayList<CheckerIssue>();
        filteredIssues.addAll(issues);

        // VersioningChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("VersioningChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("VersioningChecker doesn't work correct. Expected " + anzVersioning + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzVersioning);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // JavaDelegateChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("JavaDelegateChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("JavaDelegateChecker doesn't work correct. Expected " + anzDelegate + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzDelegate);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // ProcessVariablesNameConventionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("ProcessVariablesNameConventionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("ProcessVariablesNameConventionChecker doesn't work correct. Expected " + anzVariablesNameConvention
                + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzVariablesNameConvention);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // EmbeddedGroovyScriptChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("EmbeddedGroovyScriptChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue(
                "EmbeddedGroovyScriptChecker doesn't work correct. Expected " + anzGroovyScript + " issue but found "
                        + filteredIssues.size(),
                filteredIssues.size() == anzGroovyScript);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // XorNamingConventionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("XorNamingConventionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue(
                "XorNamingConventionChecker doesn't work correct. Expected " + anzXorNaming + " issue but found "
                        + filteredIssues.size(),
                filteredIssues.size() == anzXorNaming);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // NoScriptChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("NoScriptChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("NoScriptChecker doesn't work correct. Expected " + anzNoScript + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzNoScript);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // DmnTaskChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("DmnTaskChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("DmnTaskChecker doesn't work correct. Expected " + anzDmnTask + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzDmnTask);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // TimerExpressionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("TimerExpressionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("TimerExpressionChecker doesn't work correct. Expected " + anzTimerExpression + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzTimerExpression);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // ElementIdConventionChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("ElementIdConventionChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue(
                "ElementIdConventionChecker doesn't work correct. Expected " + anzElementId + " issues but found "
                        + filteredIssues.size(),
                filteredIssues.size() == anzElementId);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

        // ProcessVariablesModelChecker
        for (CheckerIssue issue : issues) {
            if (!issue.getRuleName().equals("ProcessVariablesModelChecker")) {
                filteredIssues.remove(issue);
            }
        }
        assertTrue("ProcessVariablesModelChecker doesn't work correct. Expected " + anzProcessVariables
                + " issues but found "
                + filteredIssues.size(),
                filteredIssues.size() == anzProcessVariables);
        filteredIssues.clear();
        filteredIssues.addAll(issues);

    }
}
