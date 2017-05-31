/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.bpm.vPAV;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 * 
 * @ContextConfiguration(classes = { TestApplicationConfiguration.class })
 */

public class CamundaStaticValidatorTestHelperTest
        extends TestCase {

    /**
     * Create the test case
     *
     * @param testName
     *            name of the test case
     */
    public CamundaStaticValidatorTestHelperTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CamundaStaticValidatorTestHelperTest.class);
    }

    /**
     * See if all bean names are in the mapping file
     * 
     * @throws IOException
     */
    public void testApp() throws IOException {
        // Given an application context with at least 2 beans
        ApplicationContext ctx = new AnnotationConfigApplicationContext(TestApplicationConfiguration.class);

        // When the bean export feature is used
        ViadeeProcessApplicationValidatorTestHelper.generateBeanMappingFile(ctx);
        List<String> list = new ArrayList<>();

        // Then an xml file containing the names of the 2 beans should have been written
        Stream<String> stream = Files.lines(Paths.get("target/beanMapping.xml"));
        list = stream.collect(Collectors.toList());
        stream.close();

        assertTrue(list.get(2).substring(12, 21).equals("ersteBean"));
        assertTrue(list.get(3).substring(12, 22).equals("zweiteBean"));
    }
}
