package de.viasurance.model;

public class Schadensmeldung {

    private Kunde kunde = new Kunde();

    private Kfz kfz = new Kfz();

    private Schaden schaden = new Schaden();

    private Anhang anhang = new Anhang();

    private String versicherungsscheinnummer;

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Kfz getKfz() {
        return kfz;
    }

    public void setKfz(Kfz kfz) {
        this.kfz = kfz;
    }

    public Schaden getSchaden() {
        return schaden;
    }

    public void setSchaden(Schaden schaden) {
        this.schaden = schaden;
    }

    public Anhang getAnhang() {
        return anhang;
    }

    public void setAnhang(Anhang anhang) {
        this.anhang = anhang;
    }

    public String getVersicherungsscheinnummer() {
        return versicherungsscheinnummer;
    }

    public void setVersicherungsscheinnummer(String versicherungsscheinnummer) {
        this.versicherungsscheinnummer = versicherungsscheinnummer;
    }
}