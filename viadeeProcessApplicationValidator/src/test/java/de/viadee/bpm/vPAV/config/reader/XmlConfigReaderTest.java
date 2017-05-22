package de.viadee.bpm.vPAV.config.reader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Map;

import org.junit.Test;

import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.PathFinder;
import de.viadee.bpm.vPAV.config.model.Rule;

public class XmlConfigReaderTest {

    /**
     * Test loading a correct config file
     * 
     * @throws ConfigReaderException
     */
    @Test()
    public void testLoadingCorrectXMLConfigFile() throws ConfigReaderException {
        // Given
        XmlConfigReader reader = new XmlConfigReader();
        final String pathRuleSetDefault = new PathFinder(this.getClass().getClassLoader())
                .readResourceFile(ConstantsConfig.RULESETDEFAULT);

        // When
        Map<String, Rule> result = reader.read(new File(pathRuleSetDefault));

        // Then
        assertFalse("No rules could be read", result.isEmpty());
    }

    /**
     * Test loading a non-existing config file and check for defaults
     * 
     * @throws ConfigReaderException
     */
    @Test()
    public void testLoadingNonExistingXMLConfigFile() throws ConfigReaderException {
        // Given
        XmlConfigReader reader = new XmlConfigReader();

        // When
        Map<String, Rule> result = reader.read(new File("non-existing.xml"));

        // Then
        // DefaultXML correctly read
        assertFalse("No rules could be read - no defaults are returned", result.isEmpty());
        // Default rules correct
        assertTrue("False Default ruleSet ", result.get("JavaDelegateChecker").isActive());
        assertTrue("False Default ruleSet ", result.get("EmbeddedGroovyScriptChecker").isActive());
        assertTrue("False Default ruleSet ", result.get("VersioningChecker").isActive());
        assertFalse("False Default ruleSet ", result.get("DmnTaskChecker").isActive());
        assertFalse("False Default ruleSet ", result.get("ProcessVariablesModelChecker").isActive());
        assertFalse("False Default ruleSet ", result.get("ProcessVariablesNameConventionChecker").isActive());
        assertFalse("False Default ruleSet ", result.get("TaskNamingConventionChecker").isActive());
    }

    /**
     * Test loading a incorrect config file
     * 
     * 
     */
    @Test()
    public void testLoadingIncorrectNameXMLConfigFile() throws ConfigReaderException {
        // Given
        XmlConfigReader reader = new XmlConfigReader();

        // When Then
        try {
            reader.read(new File("src/test/resources/ruleSetIncorrectName.xml"));
            fail("Erwartete ConfigReaderException (Regelname leer) nicht geworfen");
        } catch (ConfigReaderException e) {
            try {
                reader.read(new File("src/test/resources/ruleSetIncorrect.xml"));
                fail("Erwartete ConfigReaderException (xml nicht erkannt) nicht geworfen");
            } catch (ConfigReaderException ez) {

            }
        }
    }

}
