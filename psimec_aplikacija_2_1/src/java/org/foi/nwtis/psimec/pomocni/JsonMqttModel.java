/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.pomocni;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Paskal
 */
public class JsonMqttModel {

    @SerializedName("avion")
    private String avion;
    @SerializedName("poruka")
    private String poruka;
    @SerializedName("korisnik")
    private String korisnik;
    @SerializedName("aerodrom")
    private String aerodrom;
    @SerializedName("oznaka")
    private String oznaka;
    @SerializedName("vrijeme")
    private String vrijeme;

    public String getAvion() {
        return avion;
    }

    public void setAvion(String avion) {
        this.avion = avion;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getAerodrom() {
        return aerodrom;
    }

    public void setAerodrom(String aerodrom) {
        this.aerodrom = aerodrom;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

}
