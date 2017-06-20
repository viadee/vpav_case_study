package de.viadee.bpm.vPAV;
/**
 * Copyright � 2017, viadee Unternehmensberatung GmbH All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. All advertising materials mentioning features or use of this software must display the following
 * acknowledgement: This product includes software developed by the viadee Unternehmensberatung GmbH. 4. Neither the
 * name of the viadee Unternehmensberatung GmbH nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.IOException;
import java.util.ArrayList;

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

public class XmlScanner {

    private final String businessRuleTask_new = "bpmn:businessRuleTask";
    private final String businessRuleTask_old = "businessRuleTask";
    private final String c_class = "camunda:class";
    private final String c_exp = "camunda:expression";
    private final String c_dexp = "camunda:delegateExpression";
    private final String c_dmn = "camunda:decisionRef";
    private final String imp = "implementation";
    
    private String node_value;
    private String node_name;

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document doc;


    public XmlScanner() throws ParserConfigurationException {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
    }

    public ArrayList<String> getImplementation(String path, String id)
            throws SAXException, IOException, XPathExpressionException {
        // List to hold return values               
        ArrayList<String> return_values = new ArrayList<String>();
  
        // parse the given bpmn model
        doc = builder.parse(path);

        // create nodelist that contains all BusinessRuleTasks with the namespace
        NodeList list = doc.getElementsByTagName(businessRuleTask_new);

        // if list is empty check for BusinessRuleTask without namespace (older models)
        if (list.getLength() == 0) {
            list = doc.getElementsByTagName(businessRuleTask_old);
        }

        // iterate over list and check child of each node
        for (int i = 0; i < list.getLength(); i++) {

            Element businessRuleTask_Element = (Element) list.item(i);
            NamedNodeMap businessRuleTask_Element_Attr = businessRuleTask_Element.getAttributes();

            // check if the ids are corresponding            
            if (id.equals(businessRuleTask_Element.getAttribute("id"))) {
                
                // check if more than 1 inner attribute exists
                // first attribute is id, second is the camunda implementation               
                if (businessRuleTask_Element_Attr.getLength() > 1) {

                    // node_name equals an implementation
                    // node_value equals the reference for the respective implementation
                    Node attr = businessRuleTask_Element_Attr.item(0);
                    node_name = attr.getNodeName();
                    node_value = attr.getNodeValue();

                    // no references for an implementation specified
                    if (!node_name.isEmpty() && node_value.isEmpty()) {
                        switch (node_name) {
                            case c_class:                                
                                return_values.add(c_class);
                                break;
                            case c_exp:                               
                                return_values.add(c_exp);
                                break;
                            case c_dexp:                               
                                return_values.add(c_dexp);
                                break;
                            case c_dmn:                                
                                return_values.add(c_dmn);
                                break;
                        }
                    }
                    
                    // if inner attributes only consist of id, then return
                } else if (businessRuleTask_Element_Attr.getLength() == 1) {                    
                    return_values.add(imp);
                }
            }
        }     
        return return_values;
    }
}