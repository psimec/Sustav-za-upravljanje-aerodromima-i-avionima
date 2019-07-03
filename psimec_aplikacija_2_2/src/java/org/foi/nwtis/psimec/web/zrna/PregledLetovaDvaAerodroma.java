package org.foi.nwtis.psimec.web.zrna;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.rest.klijenti.AIRP2RESTKlijent;
import org.foi.nwtis.psimec.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.pomocni.JsonRestModel;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AIRP2WSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;

@Named(value = "pregledLetovaDvaAerodroma")
@SessionScoped
public class PregledLetovaDvaAerodroma implements Serializable {

    String korime;
    String lozinka;
    Konfiguracija konfiguracija;
    private int brojRedovaTablice;
    private int brojRedovaIzbornika;
    private float udaljenost;
    private AIRP2RESTKlijent aIRP2RESTKlijent;
    private List<Aerodrom> aerodromi;
    private List<AvionLeti> letovi;
    private String odabraniPolazisniAerodrom;
    private String odabraniOdredisniAerodrom;
    DefaultMapModel simpleModel;
    Gson gson;
    private double lat;
    private double lon;

    public PregledLetovaDvaAerodroma() {
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

    public void dohvatiLetove() {
        letovi = new ArrayList();
        if (odabraniOdredisniAerodrom != null && odabraniPolazisniAerodrom != null) {
            List<AvionLeti> sviLetovi = AIRP2WSKlijent.dajAvionePoletjeleSAerodroma(korime, lozinka, odabraniPolazisniAerodrom, 0, Integer.MAX_VALUE);
            System.err.println(sviLetovi.size());
            for (AvionLeti let : sviLetovi) {
                if (let.getEstArrivalAirport().equals(odabraniOdredisniAerodrom)) {
                    letovi.add(let);
                }
            }
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Potrebno je odabrati polazišni i odredišni aerodrom");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void dohvatiAerodrome() {
        udaljenost = -1;
        aerodromi = new ArrayList();
        String odgovor = aIRP2RESTKlijent.getJson(lozinka, korime);
        JsonArray jsonArray;
        try (JsonReader reader = Json.createReader(new StringReader(odgovor))) {
            jsonArray = reader.readObject().getJsonArray("odgovor");
        }

        for (JsonValue jsonValue : jsonArray) {
            JsonObject jsonObject = (JsonObject) jsonValue;
            JsonObject joLokacija = jsonObject.getJsonObject("lokacija");
            aerodromi.add(new Aerodrom(
                    jsonObject.getString("icao").trim(),
                    jsonObject.getString("naziv").trim(),
                    jsonObject.getString("drzava").trim(),
                    new Lokacija(joLokacija.getString("latitude").trim(),
                            joLokacija.getString("longitude").trim())));
        }
    }

    public void dohvatiUdaljenost() {
        if (odabraniOdredisniAerodrom != null && odabraniPolazisniAerodrom != null) {
            udaljenost = (float) AIRP2WSKlijent.dajUdaljenostDvaAerodroma(korime, lozinka, odabraniPolazisniAerodrom, odabraniOdredisniAerodrom);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Potrebno je odabrati polazišni i odredišni aerodrom");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void prikaziAerodrome() {

        if (odabraniOdredisniAerodrom != null && odabraniPolazisniAerodrom != null) {
            Aerodrom a1 = null, a2 = null;
            for (Aerodrom a : aerodromi) {
                if (a.getIcao().equals(odabraniPolazisniAerodrom)) {
                    a1 = a;
                }
                if (a.getIcao().equals(odabraniOdredisniAerodrom)) {
                    a2 = a;
                }
            }
            simpleModel = new DefaultMapModel();
            String podaci1 = "Aerodrom: " + a1.getIcao() + ", " + a1.getNaziv() + "\n";
            Marker marker1 = new Marker(new LatLng(Double.parseDouble(
                    a1.getLokacija().getLatitude()), Double.parseDouble(a1.getLokacija().getLongitude())), podaci1);
            simpleModel.addOverlay(marker1);
            String podaci2 = "Aerodrom: " + a2.getIcao() + ", " + a2.getNaziv() + "\n";
            Marker marker2 = new Marker(new LatLng(Double.parseDouble(
                    a2.getLokacija().getLatitude()), Double.parseDouble(a2.getLokacija().getLongitude())), podaci2);
            simpleModel.addOverlay(marker2);
            this.lat = Double.parseDouble(a1.getLokacija().getLatitude());
            this.lon = Double.parseDouble(a1.getLokacija().getLongitude());
            Polygon polygon = new Polygon();
            polygon.getPaths().add(new LatLng(Double.parseDouble(a1.getLokacija().getLatitude()),
                    Double.parseDouble(a1.getLokacija().getLongitude())));
            polygon.getPaths().add(new LatLng(Double.parseDouble(a2.getLokacija().getLatitude()),
                    Double.parseDouble(a2.getLokacija().getLongitude())));
            polygon.setFillColor("#ff0000");
            polygon.setStrokeColor("#ff0000");
            polygon.setStrokeWeight(5);
            simpleModel.addOverlay(polygon);

        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Greška!", "Potrebno je odabrati polazišni i odredišni aerodrom");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
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

    public List<Aerodrom> getAerodromi() {
        return aerodromi;
    }

    public void setAerodromi(List<Aerodrom> aerodromi) {
        this.aerodromi = aerodromi;
    }

    public List<AvionLeti> getLetovi() {
        return letovi;
    }

    public void setLetovi(List<AvionLeti> letovi) {
        this.letovi = letovi;
    }

    public String getOdabraniPolazisniAerodrom() {
        return odabraniPolazisniAerodrom;
    }

    public void setOdabraniPolazisniAerodrom(String odabraniPolazisniAerodrom) {
        this.odabraniPolazisniAerodrom = odabraniPolazisniAerodrom;
    }

    public String getOdabraniOdredisniAerodrom() {
        return odabraniOdredisniAerodrom;
    }

    public void setOdabraniOdredisniAerodrom(String odabraniOdredisniAerodrom) {
        this.odabraniOdredisniAerodrom = odabraniOdredisniAerodrom;
    }

    public double getUdaljenost() {
        return udaljenost;
    }

    public void setUdaljenost(float udaljenost) {
        this.udaljenost = udaljenost;
    }

    public DefaultMapModel getSimpleModel() {
        return simpleModel;
    }

    public void setSimpleModel(DefaultMapModel simpleModel) {
        this.simpleModel = simpleModel;
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

}
