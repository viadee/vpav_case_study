package de.viadee.bpmnAnalytics.beans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class BeanMappingXmlParser {

  public static Map<String, String> parse(final File beanMappingFile) {

    final Map<String, String> beanNamesCorrespondingClasses = new HashMap<String, String>();

    try {
      final Document xmlDoc = readXmlDocumentFile(beanMappingFile);
      beanNamesCorrespondingClasses.putAll(readBeanNamesAndCorrespondingClasses(xmlDoc));
    } catch (final ParserConfigurationException | SAXException | IOException ex) {
      throw new RuntimeException("bean mapping couldn't be loaded from beanMapping.xml");
    }

    return beanNamesCorrespondingClasses;
  }

  /**
   * Read Xml document
   * 
   * @param beanMappingFile
   * @return
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  private static Document readXmlDocumentFile(final File beanMappingFile)
      throws ParserConfigurationException, IOException, SAXException {
    final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    final Document xmlDoc = dBuilder.parse(beanMappingFile);
    return xmlDoc;
  }

  /**
   * get bean names with corresponding classes from xml document
   * 
   * @param xmlDoc
   * @return
   */
  private static Map<String, String> readBeanNamesAndCorrespondingClasses(final Document xmlDoc) {
    final Map<String, String> beanNamesCorrespondingClasses = new HashMap<String, String>();

    final NodeList nodeList = xmlDoc.getElementsByTagName("bean");
    for (int i = 0; i < nodeList.getLength(); i++) {
      final Node xmlNode = nodeList.item(i);
      if (xmlNode.getNodeType() == Node.ELEMENT_NODE) {
        final Element xmlElement = (Element) xmlNode;
        beanNamesCorrespondingClasses.put(xmlElement.getAttribute("name"),
            xmlElement.getAttribute("value"));
      }
    }

    return beanNamesCorrespondingClasses;
  }
}
