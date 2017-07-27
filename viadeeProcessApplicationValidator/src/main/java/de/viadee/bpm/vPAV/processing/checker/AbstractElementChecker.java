package de.viadee.bpm.vPAV.processing.checker;

import de.viadee.bpm.vPAV.config.model.Rule;

public abstract class AbstractElementChecker implements ElementChecker {
    
    protected final Rule rule;
    
    public AbstractElementChecker(final Rule rule) {
        this.rule = rule;
    }
    
    
/**
 *  TODO:
 *  Method to generate centralized error messages
 *       
 */
    
//    public Collection<CheckerIssue> addIssue(final BaseElement baseElement, final Rule rule, final BpmnElement element, final String errorMsg, Collection<CheckerIssue> issues){    
//        
//        final String name = baseElement.getAttributeValue("name"); 
//        
//        issues.add(new CheckerIssue(rule.getName(), CriticalityEnum.ERROR,
//                element.getProcessdefinition(), null, baseElement.getAttributeValue("id"),
//                baseElement.getAttributeValue("name"), null, null, null,
//                errorMsg+ " "+ name));   
//    
//        return issues;
//        
//    }

}
