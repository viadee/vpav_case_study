package de.viadee.bpm.vPAV.output;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import de.viadee.bpm.vPAV.ConstantsConfig;
import de.viadee.bpm.vPAV.processing.model.data.BpmnElement;
import de.viadee.bpm.vPAV.processing.model.data.CheckerIssue;
import de.viadee.bpm.vPAV.processing.model.graph.Path;

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

  private static String transformToJsonDatastructure(final Collection<CheckerIssue> issues) {
    final JsonArray jsonIssues = new JsonArray();
    if (issues != null && issues.size() > 0) {
      for (final CheckerIssue issue : issues) {
        final JsonObject obj = new JsonObject();
        obj.addProperty("id", issue.getId());
        obj.addProperty("bpmnFile", issue.getBpmnFile());
        obj.addProperty("ruleName", issue.getRuleName());
        obj.addProperty("elementId", issue.getElementId());
        obj.addProperty("elementName", issue.getElementName());
        obj.addProperty("classification", issue.getClassification().name());
        obj.addProperty("resourceFile", issue.getResourceFile());
        obj.addProperty("variable", issue.getVariable());
        obj.addProperty("anomaly",
            issue.getAnomaly() == null ? null : issue.getAnomaly().getDescription());
        final JsonArray jsonPaths = new JsonArray();
        final List<Path> paths = issue.getInvalidPaths();
        if (paths != null && paths.size() > 0) {
          for (final Path path : paths) {
            final JsonArray jsonPath = new JsonArray();
            final List<BpmnElement> elements = path.getElements();
            for (BpmnElement element : elements) {
              final JsonObject jsonElement = new JsonObject();
              final String id = element.getBaseElement().getId();
              final String name = element.getBaseElement().getAttributeValue("name");
              jsonElement.addProperty("elementId", id);
              jsonElement.addProperty("elementName",
                  name == null ? null : name.replaceAll("\n", ""));
              jsonPath.add(jsonElement);
            }
            jsonPaths.add(jsonPath);
          }
        }
        obj.add("paths", jsonPaths);
        obj.addProperty("message", issue.getMessage());
        jsonIssues.add(obj);
      }
    }

    return new GsonBuilder().setPrettyPrinting().create().toJson(jsonIssues);
  }
}
