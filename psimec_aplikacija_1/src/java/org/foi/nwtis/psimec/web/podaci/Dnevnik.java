package org.foi.nwtis.psimec.web.podaci;

public class Dnevnik {
    private int id;
    private String korime;
    private String zahtjev;
    private String aplikacija;
    private String datum;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getZahtjev() {
        return zahtjev;
    }

    public void setZahtjev(String zahtjev) {
        this.zahtjev = zahtjev;
    }

    public String getAplikacija() {
        return aplikacija;
    }

    public void setAplikacija(String aplikacija) {
        this.aplikacija = aplikacija;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}
