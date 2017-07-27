package de.viasurance.kfzglasbruch.rest;

import org.camunda.bpm.engine.variable.value.ObjectValue;

import de.viadee.bpm.vPAV.beans.InitialProcessVariablesBase;

public class InitialProcessVariables extends InitialProcessVariablesBase {

    ObjectValue ext_kunde;

    String ext_vsnr;

    ObjectValue ext_kfz;

    ObjectValue ext_schaden;

    Double ext_schadenshoehe;

    ObjectValue ext_anhang;

    String dateiname;

}
