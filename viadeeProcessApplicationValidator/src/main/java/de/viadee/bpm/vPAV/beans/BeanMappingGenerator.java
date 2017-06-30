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
package de.viadee.bpm.vPAV.beans;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Helper methods for Maven Plugin CamundaStaticValidator
 *
 */
public class BeanMappingGenerator {

    /**
     * Generates bean mapping file for a ApplicationContext
     * 
     * @param ctx
     */
    public static void generateBeanMappingFile(final ApplicationContext ctx) {

        final Map<String, String> beanNameToClassMap = new HashMap<String, String>();

        // read bean names
        final String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for (final String beanName : beanDefinitionNames) {
            // don't add spring own classes
            if (!beanName.startsWith("org.springframework")) {
                final Object obj = ctx.getBean(beanName);
                if (obj != null) {
                    beanNameToClassMap.put(beanName, obj.getClass().getName());
                }
            }
        }
        // write xml file
        writeBeanMappingXmlFile(beanNameToClassMap);
    }

    /**
     * Writes bean mapping file in xml format
     * 
     * @param beanNameToClassMap
     */
    private static void writeBeanMappingXmlFile(final Map<String, String> beanNameToClassMap) {
        if (beanNameToClassMap != null && beanNameToClassMap.size() > 0) {
            try {
                final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                // root elements
                final Document doc = docBuilder.newDocument();
                final Element rootElement = doc.createElement("beans");
                doc.appendChild(rootElement);

                for (final String beanName : beanNameToClassMap.keySet()) {
                    // bean elements
                    final Element bean = doc.createElement("bean");
                    rootElement.appendChild(bean);

                    // set name attribute to bean element
                    final Attr name = doc.createAttribute("name");
                    name.setValue(beanName);
                    bean.setAttributeNode(name);

                    // set value attribute to bean element
                    final Attr value = doc.createAttribute("value");
                    value.setValue(beanNameToClassMap.get(beanName));
                    bean.setAttributeNode(value);
                }

                // write the content into xml file
                final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                final Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                final DOMSource source = new DOMSource(doc);
                final StreamResult result = new StreamResult(new File("target/beanMapping.xml"));

                transformer.transform(source, result);

            } catch (ParserConfigurationException pce) {
                throw new RuntimeException("ParserConfigurationException", pce);
            } catch (TransformerException tfe) {
                throw new RuntimeException("TransformerException", tfe);
            }
        }
    }
}
