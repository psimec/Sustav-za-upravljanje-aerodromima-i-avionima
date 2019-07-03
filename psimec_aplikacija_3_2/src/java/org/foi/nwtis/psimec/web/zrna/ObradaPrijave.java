package org.foi.nwtis.psimec.web.zrna;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.sb.AutentifikacijaKorisnika;

/**
 *
 * @author Paskal
 */
@Named(value = "obradaPrijave")
@SessionScoped
public class ObradaPrijave implements Serializable {

    @EJB
    private AutentifikacijaKorisnika autentifikacijaKorisnika;

    private String korime;
    private String lozinka;

    public ObradaPrijave() {
    }

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
        return "prijava";
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
    
    public void kreirajSesiju(){
     HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        sesija.setAttribute("korime", korime);
        sesija.setAttribute("lozinka", lozinka);
    }

}
