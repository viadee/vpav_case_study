package de.viadee.bpm.camundaStaticValidator.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.viadee.bpm.camundaStaticValidator.ConstantsConfig;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.BpmnElement;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.CheckerIssue;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.Path;

public class JsonOutputWriter implements IssueOutputWriter {

  public void write(final Collection<CheckerIssue> issues) throws OutputWriterException {
    final String json = transformToJsonDatastructure(issues);
    if (json != null && !json.isEmpty()) {
      try (final FileWriter file = new FileWriter(ConstantsConfig.VALIDATION_JSON_OUTPUT)) {
        file.write(json);
      } catch (final IOException ex) {
        throw new OutputWriterException("json output couldn't be written");
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static String transformToJsonDatastructure(final Collection<CheckerIssue> issues) {
    final JSONArray jsonIssues = new JSONArray();
    if (issues != null && issues.size() > 0) {
      for (final CheckerIssue issue : issues) {
        final JSONObject obj = new JSONObject();
        obj.put("id", issue.getId());
        obj.put("bpmnFile", issue.getBpmnFile());
        obj.put("ruleName", issue.getRuleName());
        obj.put("elementId", issue.getElementId());
        obj.put("elementName", issue.getElementName());
        obj.put("classification", issue.getClassification().name());
        obj.put("resourceFile", issue.getResourceFile());
        obj.put("variable", issue.getVariable());
        obj.put("anomaly", issue.getAnomaly() == null ? null : issue.getAnomaly().getDescription());
        final JSONArray jsonPaths = new JSONArray();
        final List<Path> paths = issue.getInvalidPaths();
        if (paths != null && paths.size() > 0) {
          for (final Path path : paths) {
            final JSONArray jsonPath = new JSONArray();
            final List<BpmnElement> elements = path.getElements();
            for (BpmnElement element : elements) {
              final JSONObject jsonElement = new JSONObject();
              final String id = element.getBaseElement().getId();
              final String name = element.getBaseElement().getAttributeValue("name");
              jsonElement.put("elementId", id);
              jsonElement.put("elementName", name == null ? null : name.replaceAll("\n", ""));
              jsonPath.add(jsonElement);
            }
            jsonPaths.add(jsonPath);
          }
        }
        obj.put("paths", jsonPaths);
        obj.put("message", issue.getMessage());
        jsonIssues.add(obj);
      }
    }

    return jsonIssues.toJSONString();
  }
}
