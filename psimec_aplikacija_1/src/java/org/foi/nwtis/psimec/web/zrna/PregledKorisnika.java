package org.foi.nwtis.psimec.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;
import org.foi.nwtis.psimec.web.podaci.Korisnik;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

@Named(value = "pregledKorisnika")
@SessionScoped
public class PregledKorisnika implements Serializable {

    private List<Korisnik> korisnici;
    private final UpraviteljBazomPodataka ubp;
    private Konfiguracija konfiguracija;
    private final int brojRedovaTablice;

    public PregledKorisnika() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        ubp = new UpraviteljBazomPodataka();
        korisnici = ubp.dohvatiSveKorisnike();
    }

    public List<Korisnik> getKorisnici() {
        return korisnici;
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

}
