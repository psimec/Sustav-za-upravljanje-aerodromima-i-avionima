package org.foi.nwtis.psimec.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

public class PozadinskaDretva extends Thread {

    private long pocetakIntervala;
    private long krajIntervala;
    private int trajanjeIntervala;
    private int trajanjeCiklusaDretve;
    private int incijalniPocetakIntervala;
    private int redniBrojCiklusa;
    private Konfiguracija konfiguracija;
    private SimpleDateFormat simpleDateFormat;
    private boolean radi = true;
    private String datotekaRadaDretve;
    private boolean pasivno = false;
    private OSKlijent oSKlijent;
    private UpraviteljBazomPodataka ubp;
    private String username;
    private String password;

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        while (radi) {
            long poc = System.currentTimeMillis();

            if (!pasivno) {
                preuzmiAvione();
                System.err.println(simpleDateFormat.format((long) pocetakIntervala * 1000)
                        + "-" + simpleDateFormat.format((long) krajIntervala * 1000));
                pocetakIntervala = krajIntervala;
                krajIntervala = pocetakIntervala + trajanjeIntervala * 60 * 60;
                long trenutnoVrijeme = ((long) new Date().getTime() / 1000);

                if (pocetakIntervala >= trenutnoVrijeme) {
                    postaviInicijaliPocetak();
                }
                redniBrojCiklusa++;
            }
            try {
                spremiRadDrete();
                long trajanje = System.currentTimeMillis() - poc;
                Thread.sleep(trajanjeCiklusaDretve * 60 * 1000 - trajanje);
            } catch (InterruptedException ex) {
                radi = false;
            } catch (IOException ex) {
                Logger.getLogger(PozadinskaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        postaviPocetneVrijednosti();
        System.err.println("Pozidanska dretva - pocetak");
        super.start();
    }

    public void postaviPocetneVrijednosti() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

        username = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik");
        password = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka");
        oSKlijent = new OSKlijent(username, password);
        ubp = new UpraviteljBazomPodataka();

        trajanjeCiklusaDretve = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.ciklus"));
        incijalniPocetakIntervala = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.pocetak"));
        trajanjeIntervala = Integer.parseInt(konfiguracija.dajPostavku("preuzimanje.trajanje"));

        String putanja = SlusacAplikacije.getSc().getRealPath("WEB-INF"); 
        datotekaRadaDretve = putanja + File.separator + konfiguracija.dajPostavku("datoteka.dretve");

        File f = new File(datotekaRadaDretve);
        if (!f.exists()) {
            redniBrojCiklusa = 0;
            postaviInicijaliPocetak();
        } else {
            dohvatiZapisDatoteke();
        }
    }

    public void postaviInicijaliPocetak() {
        pocetakIntervala = ((long) new Date().getTime() / 1000) - (incijalniPocetakIntervala * 60 * 60);
        krajIntervala = pocetakIntervala + trajanjeIntervala * 60 * 60;
    }

    public void spremiRadDrete() throws FileNotFoundException, IOException {
        File file = new File(datotekaRadaDretve);
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            jo.addProperty("pocetakIntervala", simpleDateFormat.format((long) pocetakIntervala * 1000));
            jo.addProperty("redniBrojCiklusa", redniBrojCiklusa);
            bw.append(gson.toJson(jo));
        }
    }

    public void dohvatiZapisDatoteke() {
        Gson gson = new Gson();
        JsonObject jo;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(datotekaRadaDretve)))) {
            jo = gson.fromJson(br, JsonObject.class);
            String datum = gson.fromJson(jo.get("pocetakIntervala"), String.class);
            pocetakIntervala = ((long) simpleDateFormat.parse(datum).getTime() / 1000);
            krajIntervala = pocetakIntervala + trajanjeIntervala * 60 * 60;
            int broj = gson.fromJson(jo.get("redniBrojCiklusa"), Integer.class);
            redniBrojCiklusa = broj;
        } catch (ParseException | IOException ex) {
            Logger.getLogger(PozadinskaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void preuzmiAvione() {
        List<Aerodrom> aerodromi = ubp.dohvatiSveMojeAerodrome();
        for (Aerodrom aerodrom : aerodromi) {
            List<AvionLeti> avioni = oSKlijent.getDepartures(aerodrom.getIcao(),(int) pocetakIntervala,(int) krajIntervala);
            List<AvionLeti> pohranjeniLetovi = ubp.dohvatiAvione(aerodrom.getIcao());

            for (AvionLeti let : avioni) {
                boolean naden = false;
                for (AvionLeti l : pohranjeniLetovi) {
                    if (l.getIcao24().compareToIgnoreCase(let.getIcao24().trim()) == 0
                            && l.getLastSeen() == let.getLastSeen()) {
                        naden = true;
                        break;
                    }
                }
                if (let.getEstDepartureAirport() != null && let.getEstArrivalAirport() != null && !naden
                       && let.getCallsign() != null) {
                    String upit = kreirajUpit(let);
                    ubp.izvrsiInsertUpit(upit);
                }
            }
        }
    }

    public String kreirajUpit(AvionLeti let) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "INSERT INTO AIRPLANES VALUES(default,"
                + "'" + let.getIcao24() + "',"
                + let.getFirstSeen() + ","
                + "'" + let.getEstDepartureAirport() + "',"
                + let.getLastSeen() + ","
                + "'" + let.getEstArrivalAirport() + "',"
                + "'" + let.getCallsign().trim() + "',"
                + let.getEstDepartureAirportHorizDistance() + ","
                + let.getEstDepartureAirportVertDistance() + ","
                + let.getEstArrivalAirportHorizDistance() + ","
                + let.getEstArrivalAirportVertDistance() + ","
                + let.getDepartureAirportCandidatesCount() + ","
                + let.getArrivalAirportCandidatesCount() + ","
                + "'" + datum + "')";
        return upit;
    }

    public void setRadi(boolean radi) {
        this.radi = radi;
    }

    public boolean isPasivno() {
        return pasivno;
    }

    public void setPasivno(boolean pasivno) {
        this.pasivno = pasivno;
    }

}
