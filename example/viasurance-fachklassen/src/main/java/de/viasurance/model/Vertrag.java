package de.viasurance.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vertrag", propOrder = { "kundennummer", "versicherungsscheinnummer", "deckungstyp", "zahlungsstatus" })
public class Vertrag {

    private String kundennummer;

    private String versicherungsscheinnummer;

    @XmlSchemaType(name = "string")
    private Deckungstyp deckungstyp;

    @XmlSchemaType(name = "string")
    private Zahlungsstatus zahlungsstatus;

    public String getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(String kundennummer) {
        this.kundennummer = kundennummer;
    }

    public String getVersicherungsscheinnummer() {
        return versicherungsscheinnummer;
    }

    public void setVersicherungsscheinnummer(String versicherungsscheinnummer) {
        this.versicherungsscheinnummer = versicherungsscheinnummer;
    }

    public Deckungstyp getDeckungstyp() {
        return deckungstyp;
    }

    public void setDeckungstyp(Deckungstyp deckungstyp) {
        this.deckungstyp = deckungstyp;
    }

    public Zahlungsstatus getZahlungsstatus() {
        return zahlungsstatus;
    }

    public void setZahlungsstatus(Zahlungsstatus zahlungsstatus) {
        this.zahlungsstatus = zahlungsstatus;
    }
}