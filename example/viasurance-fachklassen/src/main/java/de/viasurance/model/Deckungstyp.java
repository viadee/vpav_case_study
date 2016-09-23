package de.viasurance.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "deckungstyp")
@XmlEnum
public enum Deckungstyp {

    HAFTPFLICHT, TEILKASKO, VOLLKASKO;

    public String value() {
        return name();
    }

    public static Deckungstyp fromValue(String v) {
        return valueOf(v);
    }
}