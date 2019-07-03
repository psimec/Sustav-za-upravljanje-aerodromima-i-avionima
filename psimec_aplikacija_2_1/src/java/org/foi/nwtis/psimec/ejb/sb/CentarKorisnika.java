package org.foi.nwtis.psimec.ejb.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;

@Singleton
public class CentarKorisnika {

    private List<String> aktivniKorisnici;

    public void prijaviKorisnika(String korisnik) {
        if (aktivniKorisnici == null) {
            aktivniKorisnici = new ArrayList();
        }

        aktivniKorisnici.add(korisnik);
    }

    public void odjaviKorisnika(String korisnik) {
        List<String> novaLista = new ArrayList();
        for (String k : aktivniKorisnici) {
            if (k.equals(korisnik)) {
                continue;
            }
            novaLista.add(k);
        }

        this.aktivniKorisnici = novaLista;
    }

    public List<String> getAktivniKorisnici() {
        return aktivniKorisnici;
    }


}
