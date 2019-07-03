package org.foi.nwtis.psimec.ws.klijenti;

public class AIRP2WSKlijent {

    public static org.foi.nwtis.psimec.ws.klijenti.MeteoPodaci dajMeteoPodatkeAerodroma(java.lang.String korime, java.lang.String lozinka, java.lang.String icao) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajMeteoPodatkeAerodroma(korime, lozinka, icao);
    }

    public static java.util.List<org.foi.nwtis.psimec.ws.klijenti.AvionLeti> dajAvionePoletjeleSAerodroma(java.lang.String korime, java.lang.String lozinka, java.lang.String icao, int odVremena, int doVremena) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajAvionePoletjeleSAerodroma(korime, lozinka, icao, odVremena, doVremena);
    }

    public static java.util.List<org.foi.nwtis.psimec.ws.klijenti.AvionLeti> dajLetoveIzabranogAviona(java.lang.String korime, java.lang.String lozinka, java.lang.String icao24, int odVremena, int doVremena) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajLetoveIzabranogAviona(korime, lozinka, icao24, odVremena, doVremena);
    }

    public static double dajUdaljenostDvaAerodroma(java.lang.String korime, java.lang.String lozinka, java.lang.String icao1, java.lang.String icao2) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajUdaljenostDvaAerodroma(korime, lozinka, icao1, icao2);
    }

    public static boolean aktivirajAerodrom(java.lang.String korime, java.lang.String lozinka, java.lang.String icao) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.aktivirajAerodrom(korime, lozinka, icao);
    }

    public static boolean blokirajAerodrom(java.lang.String korime, java.lang.String lozinka, java.lang.String icao) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.blokirajAerodrom(korime, lozinka, icao);
    }

    public static AerodromStatus dajStatusAerodroma(java.lang.String korime, java.lang.String lozinka, java.lang.String icao) {
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service service = new org.foi.nwtis.psimec.ws.klijenti.AIRP2WS_Service();
        org.foi.nwtis.psimec.ws.klijenti.AIRP2WS port = service.getAIRP2WSPort();
        return port.dajStatusAerodroma(korime, lozinka, icao);
    }

    
    
    
    
}
