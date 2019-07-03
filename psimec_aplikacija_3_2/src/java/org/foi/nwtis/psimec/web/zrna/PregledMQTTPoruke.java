/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.podaci.KomandaPoruka;
import org.foi.nwtis.psimec.ejb.podaci.MQTTPoruka;
import org.foi.nwtis.psimec.ejb.sb.CentarPoruka;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.InformatorPorukaKomande;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Paskal
 */
@Named(value = "pregledMQTTPoruke")
@SessionScoped
public class PregledMQTTPoruke implements Serializable {

    @EJB
    private CentarPoruka centarPoruka;

    private String korime;
    private List<MQTTPoruka> listaPorukaMQTT;
    private Konfiguracija konfiguracija;
    private int brojRedovaTablice;

    @PostConstruct
    void init() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        centarPoruka.pripremiCentar(new InformatorPorukaKomande());
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        korime = (String) sesija.getAttribute("korime");
    }

    public void osvjeziPoruke() {
        listaPorukaMQTT = getListaPorukaKomande();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "UPDATE", "Ažurirani podaci");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void brisiPoruku(int id) {
        centarPoruka.obrisiMqtt(id);
        InformatorPorukaKomande slusacKomande = new InformatorPorukaKomande();
        JsonObjectBuilder job = Json.createObjectBuilder();
        slusacKomande.saljiPoruku(job.build().toString());

        osvjeziPoruke();
    }

    public List<MQTTPoruka> getListaPorukaKomande() {
        listaPorukaMQTT = centarPoruka.getListaPorukaMQTT();
        return listaPorukaMQTT;
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }
}
