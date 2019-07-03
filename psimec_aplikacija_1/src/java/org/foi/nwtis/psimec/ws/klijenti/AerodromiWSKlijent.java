package org.foi.nwtis.psimec.ws.klijenti;

public class AerodromiWSKlijent {

    public static StatusKorisnika dajStatusGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean blokirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean registrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean deregistrirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean aktivirajGrupu(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajGrupu(korisnickoIme, korisnickaLozinka);
    }

    public static java.util.List<org.foi.nwtis.psimec.ws.klijenti.Aerodrom> dajSveAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static Boolean dodajNoviAerodromGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom, java.lang.String nazivAerodrom, java.lang.String drzavaAerodrom, java.lang.String latAerodrom, java.lang.String lonAerodrom) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajNoviAerodromGrupi(korisnickoIme, korisnickaLozinka, idAerodrom, nazivAerodrom, drzavaAerodrom, latAerodrom, lonAerodrom);
    }

    public static boolean obrisiAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.obrisiAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static boolean postaviAvioneGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<org.foi.nwtis.psimec.ws.klijenti.Avion> avioniNovi) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.postaviAvioneGrupe(korisnickoIme, korisnickaLozinka, avioniNovi);
    }

    public static AerodromStatus dajStatusAerodromaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajStatusAerodromaGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static boolean aktivirajAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.aktivirajAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static boolean blokirajAerodromGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.lang.String idAerodrom) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.blokirajAerodromGrupe(korisnickoIme, korisnickaLozinka, idAerodrom);
    }

    public static boolean ucitajUgradeneAerodromeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.ucitajUgradeneAerodromeGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static boolean dodajAvionGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.psimec.ws.klijenti.Avion avionNovi) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dodajAvionGrupi(korisnickoIme, korisnickaLozinka, avionNovi);
    }

    public static boolean ucitajUgradeneAvioneGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.ucitajUgradeneAvioneGrupe(korisnickoIme, korisnickaLozinka);
    }

    public static int dajBrojPoruka(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajBrojPoruka(korisnickoIme, korisnickaLozinka);
    }

    public static void promjeniBrojPoruka(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int brojPoruka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        port.promjeniBrojPoruka(korisnickoIme, korisnickaLozinka, brojPoruka);
    }

    public static int dajTrajanjeCiklusa(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        return port.dajTrajanjeCiklusa(korisnickoIme, korisnickaLozinka);
    }

    public static void promjeniTrajanjeCiklusa(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int trajanjeCiklusa) {
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AerodromiWS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AerodromiWS port = service.getAerodromiWSPort();
        port.promjeniTrajanjeCiklusa(korisnickoIme, korisnickaLozinka, trajanjeCiklusa);
    }
    
    
    
    
    

}
