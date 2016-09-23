package de.viadee.bpmnAnalytics.output;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.viadee.bpmnAnalytics.ConstantsConfig;
import de.viadee.bpmnAnalytics.processing.model.data.BpmnElement;
import de.viadee.bpmnAnalytics.processing.model.data.CheckerIssue;
import de.viadee.bpmnAnalytics.processing.model.graph.Path;

/**
 * Ergebnisse aus dem Checker in ein definiertes XML-Format schreiben
 * 
 */
public class XmlOutputWriter implements IssueOutputWriter {

  public void write(final Collection<CheckerIssue> issues) throws OutputWriterException {

    Writer writer = null;
    try {
      writer = new BufferedWriter(
          new OutputStreamWriter(new FileOutputStream(ConstantsConfig.VALIDATION_OUTPUT), "utf-8"));
      final JAXBContext context = JAXBContext.newInstance(XmlCheckerIssues.class);
      final Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(transformToXmlDatastructure(issues), writer);
    } catch (final UnsupportedEncodingException e) {
      throw new OutputWriterException("unsupported encoding");
    } catch (final FileNotFoundException e) {
      throw new OutputWriterException("output file couldn't be generated");
    } catch (final JAXBException e) {
      e.printStackTrace();
      throw new OutputWriterException("xml output couldn't be generated (jaxb-error)");
    } finally {
      try {
        writer.close();
      } catch (Exception ex) {
        /* ignore */}
    }
  }

  private static XmlCheckerIssues transformToXmlDatastructure(
      final Collection<CheckerIssue> issues) {
    XmlCheckerIssues xmlIssues = new XmlCheckerIssues();
    for (final CheckerIssue issue : issues) {
      final List<XmlPath> xmlPaths = new ArrayList<XmlPath>();
      final List<Path> invalidPaths = issue.getInvalidPaths();
      if (invalidPaths != null) {
        for (final Path path : invalidPaths) {
          List<BpmnElement> elements = path.getElements();
          List<String> elementIds = new ArrayList<String>();
          for (final BpmnElement element : elements) {
            elementIds.add(element.getBaseElement().getId());
          }
          xmlPaths.add(new XmlPath(elementIds));
        }
      }
      xmlIssues.addIssue(
          new XmlCheckerIssue(issue.getId(), issue.getRuleName(), issue.getClassification().name(),
              issue.getBpmnFile(), issue.getResourceFile(), issue.getElementId(),
              issue.getMessage(), issue.getVariable(), xmlPaths.isEmpty() ? null : xmlPaths));
    }
    return xmlIssues;
  }
}
