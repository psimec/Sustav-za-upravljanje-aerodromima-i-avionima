package org.foi.nwtis.psimec.web.zrna;

import com.google.gson.Gson;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.foi.nwtis.psimec.podaci.Korisnik;
import org.foi.nwtis.psimec.rest.klijenti.KorisniciRESTKlijent;

/**
 *
 * @author Paskal
 */
@Named(value = "obradaRegistracije")
@SessionScoped
public class ObradaRegistracije implements Serializable {

    private String korime;
    private String lozinka;
    private String ponovljenaLoznika;
    private String ime;
    private String prezime;
    private String email;

    public ObradaRegistracije() {
    }

    public void registracija() {
        Korisnik korisnik = new Korisnik();
        if (validacija()) {
            korisnik.setIme(ime);
            korisnik.setPrezime(prezime);
            korisnik.setLozinka(lozinka);
            korisnik.setKorime(korime);
            korisnik.setEmail(email);

            KorisniciRESTKlijent korisniciREST = new KorisniciRESTKlijent();
            Gson gson = new Gson();
            String odgovor = korisniciREST.postJson(gson.toJson(korisnik));

            if (odgovor.contains("Korisnik vec postoji")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogrešni podaci registracije!", "Korisnik već postoji");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            if (odgovor.contains("OK")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Registracija!", "Korisnik uspješno dodan");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        }

    }

    private boolean validacija() {
        String poruka = "";
        boolean status = true;
        if (korime.length() < 3) {
            poruka += "Korisnicko ime mora imati barem 3 znaka; ";
            status = false;
        }
        if (ime.length() < 2) {
            poruka += "Ime mora imati barem 2 znaka; ";
            status = false;
        }
        if (prezime.length() < 2) {
            poruka += "Prezime mora imati barem 2 znaka; ";
            status = false;
        }
        if (lozinka.length() < 3) {
            poruka += "Lozinka mora imati barem 3 znaka; ";
            status = false;
        }
        if (!lozinka.equals(ponovljenaLoznika)) {
            poruka += "Lozinke nisu iste; ";
            status = false;
        }
        Pattern pattern = Pattern.compile("\\w+.?\\w+@\\w+\\.[a-zA-Z]{2,}");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            poruka += "Email nije dobrog formata; ";
            status = false;
        }
        if (!status) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogrešni podaci registracije!", poruka);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return status;
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

    public String getPonovljenaLoznika() {
        return ponovljenaLoznika;
    }

    public void setPonovljenaLoznika(String ponovljenaLoznika) {
        this.ponovljenaLoznika = ponovljenaLoznika;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
