package org.foi.nwtis.psimec.rest.serveri;

import com.google.gson.Gson;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;
import org.foi.nwtis.psimec.web.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.podaci.Korisnik;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AerodromiWSKlijent;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

@Path("aerodromi")
public class AIRP2REST {

    @Context
    private UriInfo context;
    private Konfiguracija konfiguracija;
    private UpraviteljBazomPodataka ubp;
    private String NWTIS_user;
    private String NWTIS_password;
    private Gson gson;
    private String token;
    private LIQKlijent lIQKlijent;

    public AIRP2REST() {
    }

    private void init() {
        ubp = new UpraviteljBazomPodataka();
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        NWTIS_user = konfiguracija.dajPostavku("Mqtt.korisnik");
        NWTIS_password = konfiguracija.dajPostavku("Mqtt.lozinka");
        gson = new Gson();
        token = konfiguracija.dajPostavku("LocationIQ.token");
        lIQKlijent = new LIQKlijent(token);
    }

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        ubp = new UpraviteljBazomPodataka();
        List<Korisnik> korisnici = ubp.dohvatiSveKorisnike();
        for (Korisnik k : korisnici) {
            if (k.getKorime().equals(korisnickoIme)
                    && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            List<Aerodrom> aerodromi = ubp.dohvatiSveKorisnikoveAerodrome(korisnickoIme);
            //List<org.foi.nwtis.psimec.ws.klijenti.Aerodrom> aerodromiGrupe = AerodromiWSKlijent.dajSveAerodromeGrupe(NWTIS_user, NWTIS_password);
            ubp.dodajZapisUDnevikRest(korisnickoIme, "daj sve aerodrome", "1");
            return "{\"odgovor\": " + gson.toJson(aerodromi) + ", \"status\": \"OK\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka, String data) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            String icao = gson.fromJson(data, Properties.class).getProperty("icao");
            if (icao != null) {
                if (ubp.dohvatiAerodromPremaIcao(icao) != null) {
                    Aerodrom aerodrom = ubp.dohvatiMojAerodromPremaIcao(icao);
                    if (aerodrom == null) {
                        aerodrom = ubp.dohvatiAerodromPremaIcao(icao);
                        aerodrom.setLokacija(lIQKlijent.getGeoLocation(aerodrom.getNaziv()));
                        ubp.dodajMojAerodrom(aerodrom);
                    }
                    boolean rezultat = ubp.dodajAerodromKorisniku(aerodrom, korisnickoIme);
                    if (rezultat) {
                        AerodromiWSKlijent.dodajNoviAerodromGrupi(NWTIS_user, NWTIS_password,
                                aerodrom.getIcao(), aerodrom.getNaziv(), aerodrom.getDrzava(),
                                aerodrom.getLokacija().getLatitude(),
                                aerodrom.getLokacija().getLongitude());
                        ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj aerodroma " + icao, "1");
                        return " {\"odgovor\": [], \"status\": \"OK\"}";
                    }
                    ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj aerodroma " + icao, "0");
                    return "{\"status\": \"ERR\", \"poruka\": \"Pogreska korisnik vec ima aerodrom s icao-om: " + icao + "\"}";
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj aerodroma " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogresan icao\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj aerodroma " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Kljuc nije naziva 'icao'\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonId(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka, @PathParam("id") String icao) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            Aerodrom aerodrom = ubp.dohvatiMojAerodromPremaIcao(icao);
            if (aerodrom != null) {
                ubp.dodajZapisUDnevikRest(korisnickoIme, "daj podatke aerodroma " + icao, "1");
                return "{\"odgovor\": [" + gson.toJson(aerodrom) + "], \"status\": \"OK\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "daj podatke aerodroma " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Ne postoji aerodrom s icao-om: " + icao + "\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJsonId(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao, String data) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            String naziv = gson.fromJson(data, Properties.class).getProperty("naziv");
            String adresa = gson.fromJson(data, Properties.class).getProperty("adresa");

            if (naziv != null && adresa != null && adresa.trim().length() > 0) {
                Aerodrom aerodrom = ubp.dohvatiAerodromPremaIcao(icao);
                if (aerodrom != null) {
                    aerodrom.setLokacija(lIQKlijent.getGeoLocation(naziv));
                    boolean rezultat = ubp.azurirajAerodrom(aerodrom, icao);
                    if (rezultat) {
                        ubp.dodajZapisUDnevikRest(korisnickoIme, "azuriraj aerodrom " + icao, "1");
                        return "{\"odgovor\": [], \"status\": \"OK\"}";
                    } else {
                        ubp.dodajZapisUDnevikRest(korisnickoIme, "azuriraj aerodrom " + icao, "0");
                        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod azuriranja podataka\"}";
                    }
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "azuriraj aerodrom " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogresan icao\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "azuriraj aerodrom " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Pogresni parametri, "
                    + "json se treba sastojati od naziva i adresa te pripadnih vrijednosti\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJsonId(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao) {

        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            Aerodrom aerodrom = ubp.dohvatiKorisnikovAerodromPremaIcao(korisnickoIme, icao);
            if (aerodrom != null) {
                String upit = "DELETE FROM KORISNIKAERODROM WHERE IDENT='" + icao + "' AND KORIME='" + korisnickoIme + "'";
                boolean rezultat = ubp.izvrsiInsertUpit(upit);
                if (rezultat) {
                    ubp.dodajZapisUDnevikRest(korisnickoIme, "obrisi aerodrom " + icao, "1");
                    return "{\"odgovor\": [], \"status\": \"OK\"}";
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "obrisi aerodrom " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogreska brisanja aerodroma iz baze podataka\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "obrisi aerodrom " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Korisnik nema traženi aerodrom\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}/avion")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAvion(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            List<AvionLeti> avionLetovi = ubp.dohvatiAvione(icao);
            if (!avionLetovi.isEmpty()) {
                ubp.dodajZapisUDnevikRest(korisnickoIme, "daj avione za aerodrom " + icao, "1");
                return "{\"odgovor\": " + gson.toJson(avionLetovi) + ", \"status\": \"OK\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "daj avione za aerodrom " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Nema podataka o avionima\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}/avion")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String postJsonIdAvion(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao, String data) {
        init();
        AvionLeti[] avioni;
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            if (icao != null) {
                if (ubp.dohvatiMojAerodromPremaIcao(icao) != null) {
                    try {
                        avioni = gson.fromJson(data, AvionLeti[].class);
                    } catch (Exception ex) {
                        return "{\"status\": \"ERR\", \"poruka\": \"Pogresno uneseni avion(i)\"}";
                    }
                    List<AvionLeti> pohranjeniLetovi = ubp.dohvatiAvione(icao);
                    for (int i = 0; i < avioni.length; i++) {
                        if (avioni[i].getCallsign() == null || avioni[i].getIcao24() == null
                                || avioni[i].getFirstSeen() == 0 || avioni[i].getLastSeen() == 0
                                || avioni[i].getEstArrivalAirport() == null) {
                            return "{\"status\": \"ERR\", \"poruka\": \"Pogresno uneseni avion(i)\"}";
                        }
                        avioni[i].setEstDepartureAirport(icao);
                        boolean naden = false;
                        for (AvionLeti l : pohranjeniLetovi) {
                            if (l.getIcao24().compareToIgnoreCase(avioni[i].getIcao24().trim()) == 0
                                    && l.getLastSeen() == avioni[i].getLastSeen()) {
                                naden = true;
                                break;
                            }
                        }
                        if (!naden) {
                            ubp.dodajAvion(avioni[i]);
                            org.foi.nwtis.psimec.ws.klijenti.Avion a = new org.foi.nwtis.psimec.ws.klijenti.Avion();
                            a.setCallsign(avioni[i].getCallsign());
                            a.setEstarrivalairport(avioni[i].getEstArrivalAirport());
                            a.setEstdepartureairport(avioni[i].getEstDepartureAirport());
                            a.setIcao24(avioni[i].getIcao24());
                            a.setId(1);
                        }
                    }
                    ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj avion za aerodrom " + icao, "1");
                    return "{\"odgovor\": [], \"status\": \"OK\"}";
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj avion za aerodrom " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogresan icao\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "dodaj avion za aerodrom " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Kljuc nije naziva 'icao'\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}/avion")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJsonIdAvion(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            if (icao != null) {
                if (ubp.dohvatiMojAerodromPremaIcao(icao) != null) {
                    String upit = "DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "'";
                    boolean rezultat = ubp.izvrsiInsertUpit(upit);
                    if (rezultat) {
                        ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi avione aerodroma " + icao, "1");
                        return "{\"odgovor\": [], \"status\": \"OK\"}";
                    }
                    ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi avione aerodroma " + icao, "0");
                    return "{\"status\": \"ERR\", \"poruka\": \"Problem kod baze podataka\"}";
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi avione aerodroma " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogresan icao\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi avione " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Kljuc nije naziva 'icao'\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @Path("{id}/avion/{aid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJsonIdAvionAid(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka,
            @PathParam("id") String icao,
            @PathParam("aid") String icao24) {
        init();
        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            if (icao != null && icao24 != null) {
                if (ubp.dohvatiMojAerodromPremaIcao(icao) != null) {
                    String upit = "DELETE FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "' AND ICAO24='" + icao24 + "'";
                    boolean rezultat = ubp.izvrsiInsertUpit(upit);
                    if (rezultat) {
                        ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi aviona " + icao24 + " aerodroma " + icao, "1");
                        return "{\"odgovor\": [], \"status\": \"OK\"}";
                    }
                    ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi aviona " + icao24 + " aerodroma " + icao, "0");
                    return "{\"status\": \"ERR\", \"poruka\": \"Problem kod baze podataka\"}";
                }
                ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi aviona " + icao24 + " aerodroma " + icao, "0");
                return "{\"status\": \"ERR\", \"poruka\": \"Pogresan icao\"}";
            }
            ubp.dodajZapisUDnevikRest(korisnickoIme, "birisi aviona " + icao24 + " aerodroma " + icao, "0");
            return "{\"status\": \"ERR\", \"poruka\": \"Kljuc nije naziva 'icao' / 'icao24'\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson() {
        return "{\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public String postJson(@PathParam("id") String id
    ) {
        return "{\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson() {
        return "{\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }
}
