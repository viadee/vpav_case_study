
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.viadee.bpm.vPAV.ProcessApplicationValidator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
public class ModelConsistencyTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void errorsInModelMustBeFound() {
        assertFalse("Model inconsistency found. Please check target folder for validation output",
                ProcessApplicationValidator.assertBPMModelConsistency(ctx));
    }

}
