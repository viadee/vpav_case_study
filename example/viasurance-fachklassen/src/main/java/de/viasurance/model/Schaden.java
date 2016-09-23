package de.viasurance.model;

public class Schaden {

    private ReparaturTyp reparaturTyp;

    private String beschreibung;

    private long schadenshoehe; // in Cent

    public ReparaturTyp getReparaturTyp() {
        return reparaturTyp;
    }

    public void setReparaturTyp(ReparaturTyp reparaturTyp) {
        this.reparaturTyp = reparaturTyp;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public long getSchadenshoehe() {
        return schadenshoehe;
    }

    public void setSchadenshoehe(long schadenshoehe) {
        this.schadenshoehe = schadenshoehe;
    }
}