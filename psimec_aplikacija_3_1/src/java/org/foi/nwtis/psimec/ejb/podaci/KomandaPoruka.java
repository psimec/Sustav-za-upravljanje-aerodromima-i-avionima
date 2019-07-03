package org.foi.nwtis.psimec.ejb.podaci;

import java.io.Serializable;

public class KomandaPoruka implements  Serializable{
    private int id;
    private String komanda;
    private String vrijeme;
    private String korime;

    public KomandaPoruka(int id, String komanda, String vrijeme) {
        this.id = id;
        this.komanda = komanda;
        this.vrijeme = vrijeme;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKomanda() {
        return komanda;
    }

    public void setKomanda(String komanda) {
        this.komanda = komanda;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }
    
}
