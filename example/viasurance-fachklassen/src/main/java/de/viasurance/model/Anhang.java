package de.viasurance.model;

public class Anhang {

    private AnhangTyp anhangTyp;

    private String dateiname;

    private String dateiBase64;

    public AnhangTyp getAnhangTyp() {
        return anhangTyp;
    }

    public void setAnhangTyp(AnhangTyp anhangTyp) {
        this.anhangTyp = anhangTyp;
    }

    public String getDateiname() {
        return dateiname;
    }

    public void setDateiname(String dateiname) {
        this.dateiname = dateiname;
    }

    public String getDateiBase64() {
        return dateiBase64;
    }

    public void setDateiBase64(String dateiBase64) {
        this.dateiBase64 = dateiBase64;
    }
}