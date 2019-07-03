package org.foi.nwtis.psimec.ws.serveri;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;
import org.foi.nwtis.psimec.web.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.podaci.Korisnik;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AerodromStatus;
import org.foi.nwtis.psimec.ws.klijenti.AerodromiWSKlijent;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

@WebService(serviceName = "AIRP2WS")
public class AIRP2WS {

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
        List<Korisnik> korisnici = ubp.dohvatiSveKorisnike();
        for (Korisnik k : korisnici) {
            if (k.getKorime().equals(korisnickoIme)
                    && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }

    @WebMethod(operationName = "dajZadnjiPreuzetiAvion")
    public AvionLeti dajZadnjiPreuzetiAvion(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao) { //daj podatke za zadnji preuzeti let
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajZadnjiPreuzetiAvion");
            return ubp.dajZadnjiPreuzetiAvion(icao);
        }
        return null;
    }

    @WebMethod(operationName = "dajZadnjihNPreuzetihAviona")
    public List<AvionLeti> dajZadnjihNPreuzetihAviona(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka, @WebParam(name = "icao") final String icao, //daj podatke za zadnjih n preuzetih letova
            @WebParam(name = "n") final int n) {
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajZadnjihNPreuzetihAviona");
            return ubp.dajZadnjihNPreuzetihAviona(icao, n);
        }
        return null;
    }

    @WebMethod(operationName = "dajAvionePoletjeleSAerodroma") // daj avione u vremenskom rasponu s odredenog aerodroma u vremenskom slijedu kako je avion prolazio (firstSeen)
    public List<AvionLeti> dajAvionePoletjeleSAerodroma(
            @WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao,
            @WebParam(name = "odVremena") final int odVremena,
            @WebParam(name = "doVremena") final int doVremena) {
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajAvionePoletjeleSAerodroma");
            return ubp.dohvatiAvionePoletjeleSAerodroma(icao, odVremena, doVremena);
        }
        return null;
    }

    @WebMethod(operationName = "dajLetoveIzabranogAviona")
    public List<AvionLeti> dajLetoveIzabranogAviona(
            @WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao24") final String icao24,
            @WebParam(name = "odVremena") final int odVremena,
            @WebParam(name = "doVremena") final int doVremena) {
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajLetoveIzabranogAviona");
            return ubp.dohvatiLetoveAviona(icao24, odVremena, doVremena);
        }
        return null;
    }

    @WebMethod(operationName = "dajNazivePolazisnihAerodromaAviona") // daj nazive aviona u vremenskom rasponu s odredenog aerodroma u vremenskom slijedu kako je avion prolazio (firstSeen)
    public List<String> dajNazivePolazisnihAerodromaAviona(
            @WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao24") final String icao24,
            @WebParam(name = "odVremena") int odVremena,
            @WebParam(name = "doVremena") int doVremena) {
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();

            List<AvionLeti> avioni = ubp.dohvatiLetoveAviona(icao24, odVremena, doVremena);
            List<String> naziviAviona = new ArrayList<>();
            for (AvionLeti let : avioni) {
                naziviAviona.add(let.getEstDepartureAirport());
            }
            ubp.dodajZapisUDnevikSoap(korime, "dajNazivePolazisnihAerodromaAviona");
            return naziviAviona;
        }
        return null;
    }

    @WebMethod(operationName = "dajMeteoPodatkeAerodroma")
    public MeteoPodaci dajMeteoPodatkeAerodroma(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao) { //daj meteo podatke za aerodrom
        if (provjeriKorisnika(korime, lozinka)) {
            Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();

            String apiKey = konfiguracija.dajPostavku("OpenWeatherMap.apikey");
            OWMKlijent oWMKlijent = new OWMKlijent(apiKey);

            String token = konfiguracija.dajPostavku("LocationIQ.token");
            LIQKlijent lIQKlijent = new LIQKlijent(token);

            Aerodrom aerodrom = ubp.dohvatiAerodromPremaIcao(icao);
            aerodrom.setLokacija(lIQKlijent.getGeoLocation(aerodrom.getNaziv()));
            if (aerodrom != null) {
                MeteoPodaci meteoPodaci = oWMKlijent.getRealTimeWeather(aerodrom.getLokacija().getLatitude(),
                        aerodrom.getLokacija().getLongitude());
                ubp.dodajZapisUDnevikSoap(korime, "dajMeteoPodatkeAerodroma");
                return meteoPodaci;
            }
            return null;
        }
        return null;
    }

    @WebMethod(operationName = "dodajKorisnika")
    public boolean dodajKorisnika(@WebParam(name = "korisnik") final Korisnik korisnik) { //dodaje korisnika u bazu
        UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
        ubp.dodajZapisUDnevikSoap("nepoznato", "dodajKorisnika");
        return ubp.dodajKorisnika(korisnik);
    }

    @WebMethod(operationName = "azurirajKorisnika")
    public boolean azurirajKorisnika(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "korisnik") final Korisnik korisnik) { //azurira korisnika u bazi
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "azurirajKorisnika");
            return ubp.azurirajKorisnika(korisnik);
        }
        return false;
    }

    @WebMethod(operationName = "dajSveKorisnike")
    public List<Korisnik> dajSveKorisnike(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka) { //daj sve korisnike u bazi
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajSveKorisnike");
            return ubp.dohvatiSveKorisnike();
        }
        return null;
    }

    @WebMethod(operationName = "dajStatusAerodroma")
    public AerodromStatus dajStatusAerodroma(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao) { 
        if (provjeriKorisnika(korime, lozinka)) {
            Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
            String NWTIS_user = konfiguracija.dajPostavku("Mqtt.korisnik");
            String NWTIS_password = konfiguracija.dajPostavku("Mqtt.lozinka");
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajStatusAerodroma " + icao);
            return AerodromiWSKlijent.dajStatusAerodromaGrupe(NWTIS_user, NWTIS_password, icao);
        }
        return null;
    }

    @WebMethod(operationName = "blokirajAerodrom")
    public boolean blokirajAerodrom(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao) { 
        if (provjeriKorisnika(korime, lozinka)) {
            Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
            String NWTIS_user = konfiguracija.dajPostavku("Mqtt.korisnik");
            String NWTIS_password = konfiguracija.dajPostavku("Mqtt.lozinka");
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "blokirajAerodrom " + icao);
            return AerodromiWSKlijent.blokirajAerodromGrupe(NWTIS_user, NWTIS_password, icao);
        }
        return false;
    }

    @WebMethod(operationName = "aktivirajAerodrom")
    public boolean aktivirajAerodrom(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao) { 
        if (provjeriKorisnika(korime, lozinka)) {
            Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
            String NWTIS_user = konfiguracija.dajPostavku("Mqtt.korisnik");
            String NWTIS_password = konfiguracija.dajPostavku("Mqtt.lozinka");
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "aktivirajAerodrom " + icao);
            return AerodromiWSKlijent.aktivirajAerodromGrupe(NWTIS_user, NWTIS_password, icao);
        }
        return false;
    }

    @WebMethod(operationName = "dajUdaljenostDvaAerodroma")
    public double dajUdaljenostDvaAerodroma(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao1") final String icao1,
            @WebParam(name = "icao2") final String icao2) {
        if (provjeriKorisnika(korime, lozinka)) {
            Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();

            String token = konfiguracija.dajPostavku("LocationIQ.token");
            LIQKlijent lIQKlijent = new LIQKlijent(token);

            Aerodrom aerodrom1 = ubp.dohvatiMojAerodromPremaIcao(icao1);
            aerodrom1.setLokacija(lIQKlijent.getGeoLocation(aerodrom1.getNaziv()));

            Aerodrom aerodrom2 = ubp.dohvatiMojAerodromPremaIcao(icao2);
            aerodrom2.setLokacija(lIQKlijent.getGeoLocation(aerodrom2.getNaziv()));
            if (aerodrom1 != null && aerodrom2 != null) {
                double lat1 = Double.parseDouble(aerodrom1.getLokacija().getLatitude());
                double lon1 = Double.parseDouble(aerodrom1.getLokacija().getLongitude());
                double lat2 = Double.parseDouble(aerodrom2.getLokacija().getLatitude());
                double lon2 = Double.parseDouble(aerodrom2.getLokacija().getLongitude());
                ubp.dodajZapisUDnevikSoap(korime, "dajUdaljenostDvaAerodroma");
                return odrediUdaljenost(lat1, lon1, lat2, lon2);
            }
            return 0;
        }
        return 0;
    }

    @WebMethod(operationName = "dajUdaljeneAerodrome")
    public List<Aerodrom> dajUdaljeneAerodrome(@WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "icao") final String icao,
            @WebParam(name = "udaljeniOd") final int udaljeniOd,
            @WebParam(name = "udaljeniDo") final int udaljeniDo) {
        List<Aerodrom> odabraniAerodromi = new ArrayList();
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajUdaljeneAerodrome");
            List<Aerodrom> aerodromi = ubp.dohvatiSveMojeAerodrome();
            Aerodrom aerodrom = ubp.dohvatiMojAerodromPremaIcao(icao);
            if (aerodrom != null && aerodromi.size() > 1) {
                for (Aerodrom a : aerodromi) {
                    double udaljenost = odrediUdaljenost(
                            Double.parseDouble(aerodrom.getLokacija().getLatitude()),
                            Double.parseDouble(aerodrom.getLokacija().getLongitude()),
                            Double.parseDouble(a.getLokacija().getLatitude()),
                            Double.parseDouble(a.getLokacija().getLongitude()));
                    if (udaljenost > udaljeniOd && udaljenost < udaljeniDo) {
                        odabraniAerodromi.add(a);
                    }
                }
            }
        }
        return odabraniAerodromi;
    }

    @WebMethod(operationName = "dajPresejednje") 
    public List<AvionLeti> dajPresejednje(
            @WebParam(name = "korime") final String korime,
            @WebParam(name = "lozinka") final String lozinka,
            @WebParam(name = "aerodromPolaziste") final String aerodromPolaziste,
            @WebParam(name = "aerodromOdrediste") final String aerodromOdrediste,
            @WebParam(name = "odVremena") final Date odVremena,
            @WebParam(name = "doVremena") final Date doVremena) {
        if (provjeriKorisnika(korime, lozinka)) {
            UpraviteljBazomPodataka ubp = new UpraviteljBazomPodataka();
            ubp.dodajZapisUDnevikSoap(korime, "dajPresejednje");
            //TODO implementiraj
            return null;
        }
        return null;
    }

    private double odrediUdaljenost(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344; //KM

        return dist;
    }

}
