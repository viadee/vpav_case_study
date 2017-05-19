package de.viadee.bpm.vPAV;

public final class ConstantsConfig {

    public static final String RULESET = "src/main/resources/ruleSet.xml";

    public static final String RULESETDEFAULT = "src/main/resources/ruleSetDefault.xml";

    public static final String BEAN_MAPPING = "target/beanMapping.xml";

    public static final String IGNORE_FILE = "src/main/resources/.ignoreIssues";

    public static final String BPMN_FILE_PATTERN = ".*\\.bpmn";

    public static final String DMN_FILE_PATTERN = ".*\\.dmn";

    public static final String SCRIPT_FILE_PATTERN = ".*\\.groovy";

    public static final String JAVA_FILE_PATTERN = ".*\\.java";

    public static final String DEFAULT_VERSIONED_FILE_PATTERN = "([^_]*)_{1}([0-9][_][0-9]{1})\\.(java|groovy)";

    public static final String VALIDATION_XML_OUTPUT = "target/bpmn_validation.xml";

    public static final String VALIDATION_JSON_OUTPUT = "target/bpmn_validation.json";
}
