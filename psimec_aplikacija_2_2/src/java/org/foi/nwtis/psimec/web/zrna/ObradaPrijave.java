/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.sb.AutentifikacijaKorisnika;
import org.foi.nwtis.psimec.ejb.sb.CentarKorisnika;

/**
 *
 * @author Paskal
 */
@Named(value = "obradaPrijave")
@SessionScoped
public class ObradaPrijave implements Serializable {

    @EJB
    private CentarKorisnika centarKorisnika;

    @EJB
    private AutentifikacijaKorisnika autentifikacijaKorisnika;
    

    private String korime;
    private String lozinka;
    private boolean prikazi;

    public String prijava() {
        if (korime.length() < 1 || lozinka.length() < 1) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Prijava neuspješna!", "Korisničko ime i lozinka moraju biti uneseni");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }

        boolean rezultat = autentifikacijaKorisnika.autentificiraj(korime, lozinka);
        if (!rezultat) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Prijava neuspješna!", "Podaci za prijavu nisu točni");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return "";
        }
        kreirajSesiju();
        centarKorisnika.prijaviKorisnika(korime);
        return "prijava";
    }

    public void kreirajSesiju() {
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sesija.setAttribute("korime", korime);
        sesija.setAttribute("lozinka", lozinka);
    }

    public ObradaPrijave() {
        setPrikazi();
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public boolean isPrikazi() {
        return prikazi;
    }

    public void setPrikazi() {
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        prikazi = (sesija == null || sesija.getAttribute("korime") == null || sesija.getAttribute("lozinka") == null);
    }

}
