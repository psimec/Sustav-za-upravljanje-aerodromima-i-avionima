package org.foi.nwtis.psimec.web.zrna;

import com.google.gson.Gson;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.rest.klijenti.AIRP2RESTKlijent;
import org.foi.nwtis.psimec.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.pomocni.JsonRestModel;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AIRP2WSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.AvionLeti;

/**
 *
 * @author Paskal
 */
@Named(value = "pregledLetova")
@SessionScoped
public class PregledLetova implements Serializable {

    private List<Aerodrom> aerodromi;
    private List<AvionLeti> letovi;
    private List<AvionLeti> letoviAviona;
    String korime;
    String lozinka;
    Konfiguracija konfiguracija;
    private int brojRedovaTablice;
    private int brojRedovaIzbornika;
    private List<String> avioni;
    private AIRP2RESTKlijent aIRP2RESTKlijent;
    Gson gson;
    private String odabraniAerodrom;
    private Date odVremenaDatum;
    private Date doVremenaDatum;
    private String odVremena;
    private String doVremena;

    public PregledLetova() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        brojRedovaIzbornika = Integer.parseInt(konfiguracija.dajPostavku("izbornik.brojRedaka"));
        aIRP2RESTKlijent = new AIRP2RESTKlijent();
        gson = new Gson();
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija != null && sesija.getAttribute("korime") != null && sesija.getAttribute("lozinka") != null) {
            korime = (String) sesija.getAttribute("korime");
            lozinka = (String) sesija.getAttribute("lozinka");
        }
        dohvatiAerodrome();
    }

    public boolean provjeriIKreirajDatum(String odV, String doV) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            odVremenaDatum = sdf.parse(odV);
            doVremenaDatum = sdf.parse(doV);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    public void dohvatiAerodrome() {
        String odgovor = aIRP2RESTKlijent.getJson(lozinka, korime);
        aerodromi = (List<Aerodrom>) (Object) gson.fromJson(odgovor, JsonRestModel.class).getOdgovor();
    }

    public void dohvatiLetove() {
        if (odabraniAerodrom != null) {
            if (provjeriIKreirajDatum(odVremena, doVremena)) {
                letovi = AIRP2WSKlijent.dajAvionePoletjeleSAerodroma(korime, lozinka, odabraniAerodrom,
                        (int) (odVremenaDatum.getTime() / 1000),
                        (int) (doVremenaDatum.getTime() / 1000));
                prikaziAvione();
                if (letovi.isEmpty()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Greška!", "Nema letova");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Datum je krivog formata / nije upisan, potreban format dd.MM.yyyy HH:mm:ss");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Potrebno je odabrati aerodrom");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void prikaziAvione() {
        avioni = new ArrayList();
        for (AvionLeti let : letovi) {
            avioni.add(let.getIcao24());
        }
    }

    public void dohvatiAvione(String odabraniAvion) {
        if (odabraniAvion != null) {
            if (provjeriIKreirajDatum(odVremena, doVremena)) {
                letoviAviona = AIRP2WSKlijent.dajLetoveIzabranogAviona(korime, lozinka, odabraniAvion,
                        (int) (odVremenaDatum.getTime() / 1000),
                        (int) (doVremenaDatum.getTime() / 1000));
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Avioni!", "Avioni prikazani");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Datum je krivog formata / nije upisan, potreban format dd.MM.yyyy HH:mm:ss");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Potrebno je odabrati aerodrom");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public List<Aerodrom> getAerodromi() {
        dohvatiAerodrome();
        return aerodromi;
    }

    public void setAerodromi(List<Aerodrom> aerodromi) {
        this.aerodromi = aerodromi;
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public void setBrojRedovaTablice(int brojRedovaTablice) {
        this.brojRedovaTablice = brojRedovaTablice;
    }

    public int getBrojRedovaIzbornika() {
        return brojRedovaIzbornika;
    }

    public void setBrojRedovaIzbornika(int brojRedovaIzbornika) {
        this.brojRedovaIzbornika = brojRedovaIzbornika;
    }

    public String getOdVremena() {
        return odVremena;
    }

    public void setOdVremena(String odVremena) {
        this.odVremena = odVremena;
    }

    public String getDoVremena() {
        return doVremena;
    }

    public void setDoVremena(String doVremena) {
        this.doVremena = doVremena;
    }

    public String getOdabraniAerodrom() {
        return odabraniAerodrom;
    }

    public void setOdabraniAerodrom(String odabraniAerodrom) {
        this.odabraniAerodrom = odabraniAerodrom;
    }

    public List<AvionLeti> getLetovi() {
        return letovi;
    }

    public void setLetovi(List<AvionLeti> letovi) {
        this.letovi = letovi;
    }

    public List<String> getAvioni() {
        return avioni;
    }

    public void setAvioni(List<String> avioni) {
        this.avioni = avioni;
    }

    public List<AvionLeti> getLetoviAviona() {
        return letoviAviona;
    }

    public void setLetoviAviona(List<AvionLeti> letoviAviona) {
        this.letoviAviona = letoviAviona;
    }

}
