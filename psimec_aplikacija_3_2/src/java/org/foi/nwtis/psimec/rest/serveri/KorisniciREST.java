/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.rest.serveri;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.psimec.ws.klijenti.AIRP2WSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.Korisnik;

/**
 * REST Web Service
 *
 * @author Paskal
 */
@Path("korisnici")
public class KorisniciREST {

    @Context
    private UriInfo context;

    public KorisniciREST() {
    }

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        if (korisnickoIme == null || lozinka == null) {
            return false;
        }
        Konfiguracija konfiguracija = SlusacAplikacije.getKonfiguracija();
        int port = Integer.parseInt(konfiguracija.dajPostavku("port"));
        String server = konfiguracija.dajPostavku("server");
        String komanda = "KORISNIK " + korisnickoIme +"; LOZINKA "+ lozinka +";";
        
        try (Socket socket = new Socket(server, port)) {
            StringBuilder sb;
            try (InputStreamReader is = new InputStreamReader(socket.getInputStream(), "UTF-8");
                    OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream(), "UTF-8")) {
                os.write(komanda);
                os.flush();
                socket.shutdownOutput();

                int znak;
                sb = new StringBuilder();
                while ((znak = is.read()) != -1) {
                    sb.append((char) znak);
                }
            }
            String odgovor = sb.toString();
            if(odgovor.contains("OK"))
                 return true;
            else
                return false;
        } catch (IOException ex) {
            return false;
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka) {
        if (!provjeriKorisnika(korisnickoIme, lozinka)) {
            return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
        }

        List<Korisnik> korisnici = AIRP2WSKlijent.dajSveKorisnike(korisnickoIme, lozinka);
        Gson gson = new Gson();
        for (Korisnik k : korisnici) {
            k.setLozinka(null);
        }

        return "{\"odgovor\": " + gson.toJson(korisnici) + ", \"status\": \"OK\"}";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(String data) {

        Gson gson = new Gson();
        String korime;
        String loz;
        String ime;
        String prezime;
        String email;

        try {
            korime = gson.fromJson(data, Properties.class).getProperty("korime");
            loz = gson.fromJson(data, Properties.class).getProperty("lozinka");
            ime = gson.fromJson(data, Properties.class).getProperty("ime");
            prezime = gson.fromJson(data, Properties.class).getProperty("prezime");
            email = gson.fromJson(data, Properties.class).getProperty("email");
        } catch (Exception ex) {
            return "{\"status\": \"ERR\", \"poruka\": \"Doslo je do pogreske\"}";
        }

        if (korime != null && loz != null && ime != null && prezime != null) {
            Korisnik korisnik = new Korisnik();
            korisnik.setIme(ime);
            korisnik.setKorime(korime);
            korisnik.setLozinka(loz);
            korisnik.setPrezime(prezime);
            korisnik.setEmail(email);
            boolean rezultat = AIRP2WSKlijent.dodajKorisnika(korisnik);
            if (rezultat) {
                return "{\"odgovor\": [], \"status\": \"OK\"}";
            }
            return "{\"status\": \"ERR\", \"poruka\": \"Korisnik vec postoji\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogresno uneseni podaci, potrebno: korime, lozinka, ime, prezime\"}";
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON) 
    public String getJsonId(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka, @PathParam("id") String korime) {

        if (!provjeriKorisnika(korisnickoIme, lozinka)) {
            return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
        }

        List<Korisnik> korisnici = AIRP2WSKlijent.dajSveKorisnike(korisnickoIme, lozinka);
        Gson gson = new Gson();
        for (Korisnik k : korisnici) {
            if (k.getKorime().trim().compareTo(korime) == 0) {
                k.setLozinka(null);
                return "{\"odgovor\": " + gson.toJson(k) + ", \"status\": \"OK\"}";
            }
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Korisnik nije pronaden\"}";

    }

    @Path("{id}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getJsonIdAuth(@QueryParam("auth") String data, @PathParam("id") String korime, 
            @QueryParam("lozinka") String lozinka, @QueryParam("korisnickoIme") String korisnickoIme) {

        Gson gson = new Gson();
        String lozinkaAuth;
        try {
            lozinkaAuth = gson.fromJson(data, Properties.class).getProperty("lozinka");
        } catch (Exception ex) {
            return getJsonId(korime, lozinka, korisnickoIme);
        }
        if (lozinkaAuth != null) {
            if (provjeriKorisnika(korime, lozinkaAuth)) {
                return "{\"odgovor\": [], \"status\": \"OK\"}";
            }
            return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
        }
       return getJsonId(korime, lozinka, korisnickoIme);
    }

    @Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJsonId(@QueryParam("korisnickoIme") String korisnickoIme,
            @QueryParam("lozinka") String lozinka, String data) {
        if (!provjeriKorisnika(korisnickoIme, lozinka)) {
            return "{\"status\": \"ERR\", \"poruka\": \"Pogreska kod autentifikacija\"}";
        }

        Gson gson = new Gson();
        String korime;
        String loz;
        String ime;
        String prezime;
        String email;

        try {
            korime = gson.fromJson(data, Properties.class).getProperty("korime");
            loz = gson.fromJson(data, Properties.class).getProperty("lozinka");
            ime = gson.fromJson(data, Properties.class).getProperty("ime");
            prezime = gson.fromJson(data, Properties.class).getProperty("prezime");
            email = gson.fromJson(data, Properties.class).getProperty("email");
        } catch (Exception ex) {
            return "{\"status\": \"ERR\", \"poruka\": \"Doslo je do pogreske\"}";
        }
        if (loz != null && ime != null && prezime != null && email != null) {
            Korisnik korisnik = new Korisnik();
            korisnik.setIme(ime);
            korisnik.setKorime(korime);
            korisnik.setLozinka(loz);
            korisnik.setPrezime(prezime);
            korisnik.setEmail(email);
            boolean rezultat = AIRP2WSKlijent.azurirajKorisnika(korisnickoIme, lozinka, korisnik);
            if (rezultat) {
                return "{\"odgovor\": [], \"status\": \"OK\"}";
            }
            return "{\"status\": \"ERR\", \"poruka\": \"Korisnik ne postoji\"}";
        }
        return "{\"status\": \"ERR\", \"poruka\": \"Pogresno uneseni podaci, potrebno: korime, lozinka, ime, prezime, email\"}";
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson() {
        return "{\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteJson() {
        return "{\"status\": \"ERR\", \"poruka\": \"Nije dozvoljeno\"}";
    }
}
