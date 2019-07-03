/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik;
import org.foi.nwtis.psimec.ejb.sb.DnevnikFacade;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Paskal
 */
@Named(value = "pregledDnevnika")
@SessionScoped
public class pregledDnevnika implements Serializable {

    @EJB
    private DnevnikFacade dnevnikFacade;

    Konfiguracija konfiguracija;
    String korime = null;
    String lozinka;
    int brojRedovaTablice;
    private List<Dnevnik> zapisi;

    public pregledDnevnika() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija != null && sesija.getAttribute("korime") != null && sesija.getAttribute("lozinka") != null) {
            korime = (String) sesija.getAttribute("korime");
            lozinka = (String) sesija.getAttribute("lozinka");
        }
    }

    @PostConstruct
    public void init() {
        if (korime != null) {
            zapisi = dnevnikFacade.preuzmiKorisnikoveZapise(korime);
        }
    }

    public List<Dnevnik> getZapisi() {
        zapisi = dnevnikFacade.preuzmiKorisnikoveZapise(korime);
        return zapisi;
    }

    public void setZapisi(List<Dnevnik> zapisi) {
        this.zapisi = zapisi;
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public void setBrojRedovaTablice(int brojRedovaTablice) {
        this.brojRedovaTablice = brojRedovaTablice;
    }

}
