package org.foi.nwtis.psimec.web.zrna;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.sb.AutentifikacijaKorisnika;
import org.foi.nwtis.psimec.ejb.sb.CentarKorisnika;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.pomocni.JsonRestModel;
import org.foi.nwtis.psimec.rest.klijenti.AIRP2RESTKlijent;
import org.foi.nwtis.psimec.slusaci.MqttSlusac;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AIRP2WSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.AerodromStatus;
import org.foi.nwtis.psimec.ws.klijenti.Lokacija;
import org.foi.nwtis.psimec.ws.klijenti.MeteoPodaci;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

/**
 *
 * @author Paskal
 */
@Named(value = "upravljanjeAerodromima")
@SessionScoped
public class UpravljanjeAerodromima implements Serializable {

    @EJB
    private AutentifikacijaKorisnika autentifikacijaKorisnika;

    private List<Aerodrom> aerodromi;
    String korime;
    String lozinka;
    private String noviAerodromIcao;
    Konfiguracija konfiguracija;
    private int brojRedovaTablice;
    private int brojRedovaIzbornika;
    private AIRP2RESTKlijent aIRP2RESTKlijent;
    private MeteoPodaci meteoPodaci;
    private double lat;
    private double lon;
    DefaultMapModel simpleModel;
    private MqttSlusac mqttSlusac;

    Gson gson;

    public UpravljanjeAerodromima() {
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

    public void dohvatiAerodrome() {
        String odgovor = aIRP2RESTKlijent.getJson(lozinka, korime);
        aerodromi = (List<Aerodrom>) (Object) gson.fromJson(odgovor, JsonRestModel.class).getOdgovor();
    }

    public void dodajAerodrom() {
        Charset.forName("UTF-8").encode(noviAerodromIcao);
        javax.json.JsonObject jo = Json.createObjectBuilder().add("icao", noviAerodromIcao).build();
        String odgovor = aIRP2RESTKlijent.postJson(jo, lozinka, korime);
        dohvatiAerodrome();
        if (odgovor.contains("Pogreska korisnik vec ima aerodrom")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška kod dodavanja!", gson.fromJson(odgovor, JsonRestModel.class).getPoruka());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor.contains("Pogresan icao")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška kod dodavanja!", gson.fromJson(odgovor, JsonRestModel.class).getPoruka());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor.contains("OK")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Dodavanje!", "Aerodrom je uspješno dodan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void brisiAerodrom(String icao) {
        String odgovor = aIRP2RESTKlijent.deleteJsonId(icao, lozinka, korime);
        dohvatiAerodrome();
        if (odgovor.contains("Korisnik nema traženi aerodrom")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška kod brisanja!", gson.fromJson(odgovor, JsonRestModel.class).getPoruka());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor.contains("Pogreska brisanja aerodroma iz baze podataka")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Pogreška kod brisanja!", gson.fromJson(odgovor, JsonRestModel.class).getPoruka());
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor.contains("OK")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Brisanje!", "Aerodrom je uspješno obrisan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void prikaziAerodrom(String icao, String naziv, String lat, String lon) {
        simpleModel = new DefaultMapModel();
        meteoPodaci = AIRP2WSKlijent.dajMeteoPodatkeAerodroma(korime, lozinka, icao);
        String podaci = "Aerodrom: " + icao + ", " + naziv + "\n"
                + "Temperatura: " + meteoPodaci.getTemperatureValue()
                + " °C" + "\n" + "Vlaga: "
                + meteoPodaci.getHumidityValue()
                + " %" + "\n" + "Tlak: " + meteoPodaci.getPressureValue() + " hPa";

        Marker marker = new Marker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), podaci);
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
        simpleModel.addOverlay(marker);
    }

    public void prijemPoruka() {
        if (mqttSlusac == null) {
            mqttSlusac = new MqttSlusac(konfiguracija, aerodromi, korime);
            if (mqttSlusac.getState() == Thread.State.NEW) {
                mqttSlusac.start();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Prijava na MQTT!", "Upješno ste prijavljeni na MQTT poruke");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Prijava na MQTT!", "Već ste prijavljeni");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void odjavaSPrijemaPoruka() {
        if (mqttSlusac != null) {
            try {
                mqttSlusac.interrupt();
                mqttSlusac.join();
                mqttSlusac = null;
            } catch (InterruptedException ex) {
                Logger.getLogger(UpravljanjeAerodromima.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Odjava na MQTT!", "Niste prijavljeni na MQTT poruke");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void stanjeAerodroma(String icao) {
        AerodromStatus odgovor = AIRP2WSKlijent.dajStatusAerodroma(korime, lozinka, icao);
        dohvatiAerodrome();
        if (odgovor == AerodromStatus.AKTIVAN) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status aerodroma!", "Aerodrom je aktivan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor == AerodromStatus.BLOKIRAN) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status aerodroma!", "Aerodrom je blokiran");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor == AerodromStatus.PASIVAN) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Status aerodroma!", "Aerodrom je pasivan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else if (odgovor == AerodromStatus.NEPOSTOJI) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Status aerodroma!", "Aerodrom ne postoji");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void aktivirajAerodrom(String icao) {
        AerodromStatus odgovor = AIRP2WSKlijent.dajStatusAerodroma(korime, lozinka, icao);
        dohvatiAerodrome();
        if (odgovor == AerodromStatus.AKTIVAN) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greška!", "Aerodrom je već aktivan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        } else if (odgovor == AerodromStatus.NEPOSTOJI) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Greška!", "Greška aerodrom ne postoji");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        boolean status = AIRP2WSKlijent.aktivirajAerodrom(korime, lozinka, icao);
        if (status) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aktivacija!", "Aerodrom aktivan");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Greška!", "Došlo je do greške");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void blokirajAerodrom(String icao) {
        AerodromStatus odgovor = AIRP2WSKlijent.dajStatusAerodroma(korime, lozinka, icao);
        dohvatiAerodrome();
        if (odgovor == AerodromStatus.BLOKIRAN) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Greška!", "Aerodrom je već blokiran");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        } else if (odgovor == AerodromStatus.NEPOSTOJI) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Greška!", "Greška aerodrom ne postoji");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        }
        boolean status = AIRP2WSKlijent.blokirajAerodrom(korime, lozinka, icao);
        if (status) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Blokiranje!", "Aerodrom blokiran");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Greška!", "Došlo je do greške");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public List<Aerodrom> getAerodromi() {
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

    public String getNoviAerodromIcao() {
        return noviAerodromIcao;
    }

    public void setNoviAerodromIcao(String noviAerodromIcao) {
        this.noviAerodromIcao = noviAerodromIcao;
    }

    public int getBrojRedovaIzbornika() {
        return brojRedovaIzbornika;
    }

    public void setBrojRedovaIzbornika(int brojRedovaIzbornika) {
        this.brojRedovaIzbornika = brojRedovaIzbornika;
    }

    public MeteoPodaci getMeteoPodaci() {
        return meteoPodaci;
    }

    public void setMeteoPodaci(MeteoPodaci meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public DefaultMapModel getSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(DefaultMapModel simpleModel) {
        this.simpleModel = simpleModel;
    }

    public MqttSlusac getMqttSlusac() {
        return mqttSlusac;
    }

    public void setMqttSlusac(MqttSlusac mqttSlusac) {
        this.mqttSlusac = mqttSlusac;
    }

}
