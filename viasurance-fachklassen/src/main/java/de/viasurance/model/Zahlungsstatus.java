package de.viasurance.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "zahlungsstatus")
@XmlEnum
public enum Zahlungsstatus {

    OK, MAHNUNG;

    public String value() {
        return name();
    }

    public static Zahlungsstatus fromValue(String v) {
        return valueOf(v);
    }
}