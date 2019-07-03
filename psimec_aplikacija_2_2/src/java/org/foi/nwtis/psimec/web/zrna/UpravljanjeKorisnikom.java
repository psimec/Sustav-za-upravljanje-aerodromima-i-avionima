package org.foi.nwtis.psimec.web.zrna;

import com.google.gson.Gson;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.pomocni.JsonRestModel;
import org.foi.nwtis.psimec.podaci.Korisnik;
import org.foi.nwtis.psimec.rest.klijenti.KorisniciRESTKlijent;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

@Named(value = "upravljanjeKorisnikom")
@SessionScoped
public class UpravljanjeKorisnikom implements Serializable {

    Konfiguracija konfiguracija;
    String korime;
    String lozinka;
    int brojRedovaTablice;
    KorisniciRESTKlijent korisniciREST;
    private List<Korisnik> korisnici;
    private String unosLozinka;
    private String unosPonovljenaLoznika;
    private String ime;
    private String prezime;
    private String email;

    public UpravljanjeKorisnikom() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        korisniciREST = new KorisniciRESTKlijent();
        dohvatiKorisnike();
    }

    public void dohvatiKorisnike() {
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija != null && sesija.getAttribute("korime") != null && sesija.getAttribute("lozinka") != null) {
            Gson gson = new Gson();
            korime = (String) sesija.getAttribute("korime");
            lozinka = (String) sesija.getAttribute("lozinka");
            String odgovor = korisniciREST.getJson(lozinka, korime);
            korisnici = (List<Korisnik>)(Object)gson.fromJson(odgovor, JsonRestModel.class).getOdgovor();
        }
    }

    public void azuriraj() {
        if (validacija()) {
            Charset.forName("UTF-8").encode(korime);
            Charset.forName("UTF-8").encode(unosLozinka);
            Charset.forName("UTF-8").encode(lozinka);
            Charset.forName("UTF-8").encode(ime);
            Charset.forName("UTF-8").encode(prezime);
            Charset.forName("UTF-8").encode(email);
            javax.json.JsonObject jo = Json.createObjectBuilder()
                    .add("korime", korime)
                    .add("lozinka", unosLozinka)
                    .add("ime", ime)
                    .add("prezime", prezime)
                    .add("email", email).build();
            korisniciREST = new KorisniciRESTKlijent();
            String odgovor = korisniciREST.putJsonId(jo.toString(), korime, korime, lozinka);

            if (odgovor.contains("ERR")) { 
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška kod ažuriranja!", "Došlo je do pogreške");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
            if (odgovor.contains("OK")) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Ažuriranje!", "Korisnik je uspješno ažurirana");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        }
    }

    private boolean validacija() {
        String poruka = "";
        boolean status = true;
        if (ime.length() < 2) {
            poruka += "Ime mora imati barem 2 znaka; ";
            status = false;
        }
        if (prezime.length() < 2) {
            poruka += "Prezime mora imati barem 2 znaka; ";
            status = false;
        }
        if (unosLozinka.length() < 3) {
            poruka += "Lozinka mora imati barem 3 znaka; ";
            status = false;
        }
        if (!unosLozinka.equals(unosPonovljenaLoznika)) {
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

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public void setBrojRedovaTablice(int brojRedovaTablice) {
        this.brojRedovaTablice = brojRedovaTablice;
    }

    public List<Korisnik> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }

    public String getUnosLozinka() {
        return unosLozinka;
    }

    public void setUnosLozinka(String unosLozinka) {
        this.unosLozinka = unosLozinka;
    }

    public String getUnosPonovljenaLoznika() {
        return unosPonovljenaLoznika;
    }

    public void setUnosPonovljenaLoznika(String unosPonovljenaLoznika) {
        this.unosPonovljenaLoznika = unosPonovljenaLoznika;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }
    
    

}
