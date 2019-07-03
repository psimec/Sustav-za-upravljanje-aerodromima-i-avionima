package org.foi.nwtis.psimec.podaci;

import org.foi.nwtis.rest.podaci.Lokacija;




/**
 * Klasa Aerodrom služi isključivo za pohranu podataka
 * Klasa se sastoji od privatnih varijabli, konstruktora te get i set metoda za sve varijable
 * 
 * @author Paskal Šimec
 */
public class Aerodrom {
    private String icao;
    private String naziv;
    private String drzava;
    private Lokacija lokacija;

    public Aerodrom() {
    }

    public Aerodrom(String icao, String naziv, String drzava, Lokacija lokacija) {
        this.icao = icao;
        this.naziv = naziv;
        this.drzava = drzava;
        this.lokacija = lokacija;
    }

    public Lokacija getLokacija() {
        return lokacija;
    }

    public void setLokacija(Lokacija lokacija) {
        this.lokacija = lokacija;
    }

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDrzava() {
        return drzava;
    }

    public void setDrzava(String drzava) {
        this.drzava = drzava;
    }
}
