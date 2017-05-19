package de.viadee.bpm.vPAV.config.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.config.model.ElementConvention;
import de.viadee.bpm.vPAV.config.model.ElementFieldTypes;
import de.viadee.bpm.vPAV.config.model.ModelConvention;
import de.viadee.bpm.vPAV.config.model.Rule;
import de.viadee.bpm.vPAV.config.model.Setting;

public final class XmlConfigReader implements ConfigReader {

    public Map<String, Rule> read(final File file) throws ConfigReaderException {

        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XmlRuleSet.class);
            final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            // If ruleSet.xml doesn't exists, then load default ruleSet
            if (file.exists()) {
                final XmlRuleSet ruleSet = (XmlRuleSet) jaxbUnmarshaller.unmarshal(file);
                return transformFromXmlDatastructues(ruleSet);
            } else {
                final XmlRuleSet ruleSet = (XmlRuleSet) jaxbUnmarshaller
                        .unmarshal(new File(ConstantsConfig.RULESETDEFAULT));
                return transformFromXmlDatastructues(ruleSet);
            }

        } catch (JAXBException e) {
            throw new ConfigReaderException(e);
        }
    }

    private static Map<String, Rule> transformFromXmlDatastructues(final XmlRuleSet ruleSet)
            throws ConfigReaderException {
        final Map<String, Rule> rules = new HashMap<String, Rule>();

        final Collection<XmlRule> xmlRules = ruleSet.getRules();
        for (final XmlRule rule : xmlRules) {
            final String name = rule.getName();
            if (name == null)
                throw new ConfigReaderException("rule name is not set");
            final boolean state = rule.isState();
            final Collection<XmlElementConvention> xmlElementConventions = rule.getElementConventions();
            final Collection<ElementConvention> elementConventions = new ArrayList<ElementConvention>();
            if (xmlElementConventions != null) {
                for (final XmlElementConvention xmlElementConvention : xmlElementConventions) {
                    final XmlElementFieldTypes xmlElementFieldTypes = xmlElementConvention
                            .getElementFieldTypes();
                    ElementFieldTypes elementFieldTypes = null;
                    if (xmlElementFieldTypes != null) {
                        elementFieldTypes = new ElementFieldTypes(xmlElementFieldTypes.getElementFieldTypes(),
                                xmlElementFieldTypes.isExcluded());
                    }
                    elementConventions.add(new ElementConvention(xmlElementConvention.getName(),
                            elementFieldTypes, xmlElementConvention.getPattern()));
                }
            }
            final Collection<XmlModelConvention> xmlModelConventions = rule.getModelConventions();
            final Collection<ModelConvention> modelConventions = new ArrayList<ModelConvention>();
            if (xmlModelConventions != null) {
                for (final XmlModelConvention xmlModelConvention : xmlModelConventions) {
                    modelConventions.add(
                            new ModelConvention(xmlModelConvention.getName(), xmlModelConvention.getPattern()));
                }
            }
            final Collection<XmlSetting> xmlSettings = rule.getSettings();
            final Map<String, Setting> settings = new HashMap<String, Setting>();
            if (xmlSettings != null) {
                for (final XmlSetting xmlSetting : xmlSettings) {
                    settings.put(xmlSetting.getName(),
                            new Setting(xmlSetting.getName(), xmlSetting.getValue()));
                }
            }
            rules.put(name, new Rule(name, state, settings, elementConventions, modelConventions));
        }

        return rules;
    }
}
