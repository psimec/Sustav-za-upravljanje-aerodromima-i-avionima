package org.foi.nwtis.psimec.ws.klijenti;


public class AIRP2WSKlijent {

    public static boolean dodajKorisnika(org.foi.nwtis.psimec.ws.klijenti.Korisnik korisnik) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dodajKorisnika(korisnik);
    }

    public static java.util.List<org.foi.nwtis.psimec.ws.klijenti.Korisnik> dajSveKorisnike(java.lang.String korime, java.lang.String lozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajSveKorisnike(korime, lozinka);
    }

    public static boolean azurirajKorisnika(java.lang.String korime, java.lang.String lozinka, org.foi.nwtis.psimec.ws.klijenti.Korisnik korisnik) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.azurirajKorisnika(korime, lozinka, korisnik);
    }
}
