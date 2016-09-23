package de.viadee.bpm.camundaStaticValidator.output;

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

import de.viadee.bpm.camundaStaticValidator.ConstantsConfig;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.BpmnElement;
import de.viadee.bpm.camundaStaticValidator.processing.model.data.CheckerIssue;
import de.viadee.bpm.camundaStaticValidator.processing.model.graph.Path;

/**
 * Ergebnisse aus dem Checker in ein definiertes XML-Format schreiben
 * 
 */
public class XmlOutputWriter implements IssueOutputWriter {

  public void write(final Collection<CheckerIssue> issues) throws OutputWriterException {

    Writer writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(ConstantsConfig.VALIDATION_XML_OUTPUT), "utf-8"));
      final JAXBContext context = JAXBContext.newInstance(XmlCheckerIssues.class);
      final Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      m.marshal(transformToXmlDatastructure(issues), writer);
    } catch (final UnsupportedEncodingException e) {
      throw new OutputWriterException("unsupported encoding");
    } catch (final FileNotFoundException e) {
      throw new OutputWriterException("output file couldn't be generated");
    } catch (final JAXBException e) {
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
          List<XmlPathElement> pathElements = new ArrayList<XmlPathElement>();
          for (final BpmnElement element : elements) {
            String elementName = element.getBaseElement().getAttributeValue("name");
            if (elementName != null) {
              // filter newlines
              elementName = elementName.replace("\n", "");
            }
            pathElements.add(new XmlPathElement(element.getBaseElement().getId(), elementName));
          }
          xmlPaths.add(new XmlPath(pathElements));
        }
      }
      final String elementName = issue.getElementName();
      xmlIssues.addIssue(new XmlCheckerIssue(issue.getId(), issue.getRuleName(),
          issue.getClassification().name(), issue.getBpmnFile(), issue.getResourceFile(),
          issue.getElementId(), elementName == null ? null : elementName.replace("\n", ""),
          issue.getMessage(), issue.getVariable(),
          issue.getAnomaly() == null ? null : issue.getAnomaly().getDescription(),
          xmlPaths.isEmpty() ? null : xmlPaths));
    }
    return xmlIssues;
  }
}
