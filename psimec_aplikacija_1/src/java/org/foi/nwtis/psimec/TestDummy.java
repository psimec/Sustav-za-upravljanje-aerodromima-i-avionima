package org.foi.nwtis.psimec;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;
import org.foi.nwtis.psimec.ws.klijenti.Aerodrom;
import org.foi.nwtis.psimec.ws.klijenti.AerodromiWSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.Avion;

public class TestDummy {

    static String losKorisnik = "KORISNIK marko; LOZINKA assad; {PAUZA; }";
    static String dobarKorisnik = "KORISNIK pkos; LOZINKA 123;";

    static String PAUZA = "KORISNIK pkos; LOZINKA 123; {PAUZA; }";
    static String KRENI = "KORISNIK pkos; LOZINKA 123; {KRENI; }";
    static String PASIVNO = "KORISNIK pkos; LOZINKA 123; {PASIVNO; }";
    static String AKTIVNO = "KORISNIK pkos; LOZINKA 123; {AKTIVNO; }";
    static String STANI = "KORISNIK pkos; LOZINKA 123; {STANI; }";
    static String STANJE = "KORISNIK pkos; LOZINKA 123; {STANJE; }";

    static String DODAJ_GRUPA = "KORISNIK pkos; LOZINKA 123; GRUPA {DODAJ; }";
    static String PREKID_GRUPA = "KORISNIK pkos; LOZINKA 123; GRUPA {PREKID; }";
    static String KRENI_GRUPA = "KORISNIK pkos; LOZINKA 123; GRUPA {KRENI; }";
    static String PAUZA_GRUPA = "KORISNIK pkos; LOZINKA 123; GRUPA {PAUZA; }";
    static String STANJE_GRUPA = "KORISNIK pkos; LOZINKA 123; GRUPA {STANJE; }";

    public static void main(String[] args) {
        AerodromiWSKlijent.promjeniTrajanjeCiklusa("psimec", "7UKVLJ7a", 10);
        AerodromiWSKlijent.promjeniBrojPoruka("psimec", "7UKVLJ7a", 1200);
       System.err.println(AerodromiWSKlijent.dajBrojPoruka("psimec", "7UKVLJ7a"));
//        for(Aerodrom a : AerodromiWSKlijent.dajSveAerodromeGrupe("psimec", "7UKVLJ7a"))
//            System.err.println(a.getIcao());
//        //System.err.println(AerodromiWSKlijent.dajStatusAerodromaGrupe("psimec", "7UKVLJ7a", "LDZA"));
        //System.err.println(AerodromiWSKlijent.ucitajUgradeneAerodromeGrupe("psimec", "7UKVLJ7a"));

//        try (Socket socket = new Socket("localhost", 8069)) {
//            StringBuilder sb;
//            try (InputStreamReader is = new InputStreamReader(socket.getInputStream(), "UTF-8");
//                    OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream(), "UTF-8")) {
//                os.write(dobarKorisnik);
//                os.flush();
//                socket.shutdownOutput();
//
//
//                System.err.println("poslano");
//                int znak;
//                sb = new StringBuilder();
//                while ((znak = is.read()) != -1) {
//                    sb.append((char) znak);
//                }
//            }
//            String odgovor = sb.toString();
//            System.out.println(odgovor);
//        } catch (IOException ex) {
//            System.out.println("Došlo je do pogreške");
//            Logger.getLogger(TestDummy.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

}
