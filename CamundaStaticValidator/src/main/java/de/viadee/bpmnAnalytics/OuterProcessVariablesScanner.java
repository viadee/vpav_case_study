package de.viadee.bpmnAnalytics;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;

import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.FieldVisitor;
import groovyjarjarasm.asm.Opcodes;

/**
 * scan process variables, which are set in outer java classes
 *
 * important TODO: variables to bpmn element (message correlation)
 */
public class OuterProcessVariablesScanner {

  private Set<String> javaResources;

  private ClassLoader classLoader;

  private Map<String, Collection<String>> messageIdToVariableMap = new HashMap<String, Collection<String>>();

  private Map<String, Collection<String>> processIdToVariableMap = new HashMap<String, Collection<String>>();

  public OuterProcessVariablesScanner(final Set<String> javaResources,
      final ClassLoader classLoader) {
    this.javaResources = javaResources;
    this.classLoader = classLoader;
  }

  /**
   * scan variables
   *
   * @throws IOException
   */
  public void scanProcessVariables() throws IOException {
    for (final String filePath : javaResources) {
      if (!filePath.startsWith("javax")) {
        final String content = readResourceFile(filePath);
        if (content != null) {
          final Collection<String> processVariables = readProcessVariables(filePath);
          if (!processVariables.isEmpty()) {
            // if correlateMessage and startProcessInstanceByMessage called
            // together in one class take the intersection to avoid duplicates
            final Set<String> messageIds = new HashSet<String>();
            messageIds.addAll(checkStartProcessByMessageIdPattern(content));
            messageIds.addAll(checkCorrelateMessagePattern(content));
            for (final String messageId : messageIds) {
              if (messageIdToVariableMap.containsKey(messageId)) {
                // if messageId is already set, create intersection of variables and overwrite map
                // item
                final Collection<String> existingProcessVariables = messageIdToVariableMap
                    .get(messageId);
                final List<String> intersectionProcessVariables = ListUtils.intersection(
                    (List<String>) existingProcessVariables, (List<String>) processVariables);
                messageIdToVariableMap.put(messageId, intersectionProcessVariables);
              } else {
                messageIdToVariableMap.put(messageId, processVariables);
              }
            }
            final Collection<String> processIds = checkStartProcessByKeyPattern(content);
            for (final String processId : processIds) {
              processIdToVariableMap.put(processId, processVariables);
            }
          }
        }
      }
    }
  }

  /**
   * get mapping for message id
   * 
   * @return
   */
  public Map<String, Collection<String>> getMessageIdToVariableMap() {
    return messageIdToVariableMap;
  }

  /**
   * get mapping for process id
   * 
   * @return
   */
  public Map<String, Collection<String>> getProcessIdToVariableMap() {
    return processIdToVariableMap;
  }

  /**
   * read resource file
   * 
   * @param fileName
   * @return
   */
  private String readResourceFile(final String fileName) {
    String methodBody = "";
    if (fileName != null && fileName.trim().length() > 0) {
      final InputStream resource = classLoader.getResourceAsStream(fileName);
      if (resource != null) {
        try {
          methodBody = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (final IOException ex) {
          throw new RuntimeException(
              "resource '" + fileName + "' could not be read: " + ex.getMessage(), ex);
        }
      }
    }
    return methodBody;
  }

  /**
   * check pattern for startProcessInstanceByMessage
   * 
   * @param code
   * @return message ids
   */
  private Collection<String> checkStartProcessByMessageIdPattern(final String code) {

    // remove special characters from code
    final String FILTER_PATTERN = "'|\"| ";
    final String cleanedCode = code.replaceAll(FILTER_PATTERN, "");

    // search locations where variables are read
    final Pattern pattern = Pattern.compile("\\.startProcessInstanceByMessage\\((\\w+),(.*)");
    final Matcher matcher = pattern.matcher(cleanedCode);

    final Collection<String> messageIds = new ArrayList<String>();
    while (matcher.find()) {
      final String match = matcher.group(1);
      messageIds.add(match);
    }

    return messageIds;
  }

  /**
   * check pattern for startProcessInstanceByKey
   * 
   * @param code
   * @return process keys
   */
  private Collection<String> checkStartProcessByKeyPattern(final String code) {

    // remove special characters from code
    final String FILTER_PATTERN = "'|\"| ";
    final String cleanedCode = code.replaceAll(FILTER_PATTERN, "");

    // search locations where variables are read
    final Pattern pattern = Pattern.compile("\\.startProcessInstanceByKey\\((\\w+),(.*)");
    final Matcher matcher = pattern.matcher(cleanedCode);

    final Collection<String> processIds = new ArrayList<String>();
    while (matcher.find()) {
      final String match = matcher.group(1);
      processIds.add(match);
    }

    return processIds;
  }

  /**
   * check pattern for correlateMessage
   * 
   * @param code
   * @return message ids
   */
  private Collection<String> checkCorrelateMessagePattern(final String code) {

    // remove special characters from code
    final String FILTER_PATTERN = "'|\"| ";
    final String cleanedCode = code.replaceAll(FILTER_PATTERN, "");

    // search locations where variables are read
    final Pattern pattern = Pattern.compile("\\.correlateMessage\\((\\w+),(.*)");
    final Matcher matcher = pattern.matcher(cleanedCode);

    final Collection<String> messageIds = new ArrayList<String>();
    while (matcher.find()) {
      final String match = matcher.group(1);
      messageIds.add(match);
    }

    return messageIds;
  }

  /**
   * examine process variables for class (use bytecode analysis)
   * 
   * @param filePath
   * @return
   * @throws IOException
   */
  private Collection<String> readProcessVariables(final String filePath) throws IOException {

    final Collection<String> processVariables = new ArrayList<String>();

    if (filePath != null) {
      final String[] splittedFilePath = filePath.split("\\.");
      if (splittedFilePath.length > 0) {
        ClassVisitor cl = new ClassVisitor(Opcodes.ASM4) {

          @Override
          public FieldVisitor visitField(int access, String name, String desc, String signature,
              Object value) {
            if (!name.startsWith("this")) {
              processVariables.add(name);
            }
            super.visitField(access, name, desc, signature, value);
            return null;
          }
        };
        InputStream in = classLoader
            .getResourceAsStream(splittedFilePath[0] + "$InitialProcessVariables.class");
        if (in != null) {
          ClassReader classReader = new ClassReader(in);
          classReader.accept(cl, 0);
        }
      }
    }
    return processVariables;
  }
}
