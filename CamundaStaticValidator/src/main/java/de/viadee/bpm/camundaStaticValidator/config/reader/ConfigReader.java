package de.viadee.bpm.camundaStaticValidator.config.reader;

import java.io.File;
import java.util.Map;

import de.viadee.bpm.camundaStaticValidator.config.model.Rule;

public interface ConfigReader {

  Map<String, Rule> read(final File file) throws ConfigReaderException;
}
