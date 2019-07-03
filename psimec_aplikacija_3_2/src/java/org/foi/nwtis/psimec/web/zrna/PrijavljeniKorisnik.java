package org.foi.nwtis.psimec.web.zrna;


import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named(value = "prijavljeniKorisnik")
@SessionScoped
public class PrijavljeniKorisnik implements Serializable {

    private String korime;

    public PrijavljeniKorisnik() {
    }

//    public String provjeriKorisnika() { //TODO ne radi
//        HttpSession sesija;
//
//        sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
//        if (sesija == null || sesija.getAttribute("korisnik") == null) {
//            System.err.println("da");
//            return odjava();
//        }
//        return "";
//    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

}
