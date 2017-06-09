package de.viadee.bpm.vPAV;
/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the viadee Unternehmensberatung GmbH.
 * 4. Neither the name of the viadee Unternehmensberatung GmbH nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.viadee.bpm.vPAV.processing.checker.BusinessRuleTaskChecker;

public class XmlScanner {
    
    private static Logger logger = Logger.getLogger(XmlScanner.class.getName());

    private final String businessRuleTask = "bpmn:businessRuleTask";
    private final String c_class = "camunda:class";
    private final String c_exp = "camunda:expression";
    private final String c_dexp = "camunda:delegateExpression";
    private final String c_dmn = "camunda:decisionRef";
    static String path = "src/test.bpmn"; 

    private String node_value;
    private String node_name;


    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    Document doc;

    public XmlScanner() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
    }
    
    public boolean getImplementation(String path) throws SAXException, IOException, XPathExpressionException {

        boolean issue = false;
        // parse the given bpmn model
        doc = builder.parse(path);

        // create nodelist that contains all BusinessRuleTasks
        NodeList list = doc.getElementsByTagName(businessRuleTask);

        // iterate over list and check child of each node
        for (int i = 0; i < list.getLength(); i++) {

            Element businessRuleTask_Element = (Element) list.item(i);
            NamedNodeMap businessRuleTask_Element_Attr = businessRuleTask_Element.getAttributes();

            // check if more than 1 inner attribute exists
            // first attribute is id, second is the camunda implementation
            if (businessRuleTask_Element_Attr.getLength() > 1) {
                
                Node attr = businessRuleTask_Element_Attr.item(0);
                node_name = attr.getNodeName();
                node_value = attr.getNodeValue();
                
                logger.log(Level.WARNING, node_name);
                
                if (node_value.isEmpty()) {
                    switch (node_name) {
                    case c_class:
                        System.out.println("No java class reference set for: " + node_name);
                        issue = true;
                        break;
                    case c_exp:
                        System.out.println("No expression set for: " + node_name);
                        issue = true;
                        break;
                    case c_dexp:
                        System.out.println("No delegate expression set for: " + node_name);
                        issue = true;
                        break;
                    case c_dmn:
                        System.out.println("No dmn reference set for: " + node_name);
                        issue = true;
                        break;
                    }
                }
            } else if (businessRuleTask_Element_Attr.getLength() == 1) {
                issue = true;
                System.out.println("No implementation set for BusinessRuleTask");
            }
        }
        return issue;
    }
}