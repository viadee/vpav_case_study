import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.viadee.bpm.camundaStaticValidator.CamundaStaticValidatorTestHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { SpringTestConfig.class })
public class GenerateBeanMappingTest {

    @Autowired
    private ApplicationContext ctx;

    @Test
    public void generateMapping() {
        CamundaStaticValidatorTestHelper.generateBeanMappingFile(ctx);
    }
}
