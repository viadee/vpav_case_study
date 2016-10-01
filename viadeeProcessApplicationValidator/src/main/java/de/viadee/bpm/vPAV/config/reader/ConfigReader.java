package de.viadee.bpm.vPAV.config.reader;

import java.io.File;
import java.util.Map;

import de.viadee.bpm.vPAV.config.model.Rule;

public interface ConfigReader {

  Map<String, Rule> read(final File file) throws ConfigReaderException;
}
