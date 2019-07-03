package org.foi.nwtis.psimec.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.podaci.Korisnik;
import org.foi.nwtis.psimec.ws.klijenti.AerodromiWSKlijent;
import org.foi.nwtis.psimec.ws.klijenti.Avion;
import org.foi.nwtis.psimec.ws.klijenti.StatusKorisnika;

public class DretvaZahtjeva extends Thread {

    private static int id = 0;

    private Socket socket;
    public boolean radi = true;
    private UpraviteljBazomPodataka ubp;
    private String odgovorKorisniku;
    private boolean pauza = false;
    private PozadinskaDretva pozadinskaDretva;
    private Server server;
    private String NWTIS_user;
    private String NWTIS_password;
    private Konfiguracija konfiguracija;
    private int idJMSPoruke = 0;

    public DretvaZahtjeva(PozadinskaDretva pozadinskaDretva, Server server, Konfiguracija konfiguracija) {
        this.pozadinskaDretva = pozadinskaDretva;
        this.server = server;
        this.konfiguracija = konfiguracija;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        while (radi) {
            try {
                InputStreamReader is = new InputStreamReader(socket.getInputStream(), "UTF-8");
                OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
                StringBuilder parametri = dohvatiParametre(is);
                System.err.println(parametri.toString());
                provjeriSintaksu(parametri);
                os.append(odgovorKorisniku);
                os.flush();
                socket.shutdownOutput();
                socket.shutdownInput();
                socket.close();
                if (radi) {
                    synchronized (this) {
                        wait();
                    }
                }
            } catch (IOException ex) {
                System.out.println("Problem kod spajanja na socket");;
            } catch (InterruptedException ex) {
                System.out.println("Problem kod cekanja / interrupt");
            }
        }
    }

    @Override
    public synchronized void start() {
        ubp = new UpraviteljBazomPodataka();
        NWTIS_user = konfiguracija.dajPostavku("Mqtt.korisnik");
        NWTIS_password = konfiguracija.dajPostavku("Mqtt.lozinka");
        super.start();
    }

    public StringBuilder dohvatiParametre(InputStreamReader is) throws IOException {
        int znak;
        StringBuilder parametri = new StringBuilder();
        while ((znak = is.read()) != -1) {
            parametri.append((char) znak);
        }
        return parametri;
    }

    public boolean provjeriSintaksu(StringBuilder parametri) {
        String sintaksa = "KORISNIK ([\\w|-]+); LOZINKA ([\\w|-|#|!]+);("
                + " \\{(PAUZA; |KRENI; |PASIVNO; |AKTIVNO; |STANI; |STANJE; )\\})?";
        Pattern pattern = Pattern.compile(sintaksa);
        Matcher matcher = pattern.matcher(parametri);
        if (matcher.matches()) {
            if (pauza && matcher.group(4) == null) {
                odgovorKorisniku = "POSLUZITELJ NA PAUZI";
                ubp.dodajZapisUDnevik(matcher.group(1), "provjeri korisnika");
                try {
                    sendJMSMessageToNWTiS_psimec_1(kreirajPoruku(parametri.toString()));
                } catch (JMSException | NamingException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }

            if (pauza && !(matcher.group(4).contains("KRENI")
                    || matcher.group(4).contains("STANJE")
                    || matcher.group(4).contains("PAUZA")
                    || matcher.group(4).contains("STANI"))) {
                odgovorKorisniku = "POSLUZITELJ NA PAUZI";
                return true;
            }
            if (matcher.group(4) == null) { // samo provjera korisnika
                akcijaKorisnik(matcher.group(1), matcher.group(2));
                ubp.dodajZapisUDnevik(matcher.group(1), "provjeri korisnika");
                try {
                    sendJMSMessageToNWTiS_psimec_1(kreirajPoruku(parametri.toString()));
                } catch (JMSException | NamingException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
                return true;
            }

            if (!provjeriKorisnika(matcher.group(1), matcher.group(2))) {
                odgovorKorisniku = GreskeZahtjeva.error11;
                return true;
            }
            ubp.dodajZapisUDnevik(matcher.group(1), matcher.group(4));

            izvrsiAkciju(matcher.group(4));
            try {
                sendJMSMessageToNWTiS_psimec_1(kreirajPoruku(parametri.toString()));
            } catch (JMSException | NamingException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;

        }

        //GRUPA
        sintaksa = "KORISNIK ([\\w|-]+); LOZINKA ([\\w|-|#|!]+); GRUPA"
                + " \\{(DODAJ; |PREKID; |KRENI; |PAUZA; |STANJE; )\\}";
        pattern = Pattern.compile(sintaksa);
        matcher = pattern.matcher(parametri);
        if (matcher.matches()) {
            if (pauza) {
                odgovorKorisniku = "POSLUZITELJ NA PAUZI";
                return true;
            }

            if (!provjeriKorisnika(matcher.group(1), matcher.group(2))) {
                odgovorKorisniku = GreskeZahtjeva.error11;
                return true;
            }
            ubp.dodajZapisUDnevik(matcher.group(1), matcher.group(3));

            izvrsiAkcijuGrupe(("GRUPA_" + matcher.group(3)));
            try {
                sendJMSMessageToNWTiS_psimec_1(kreirajPoruku(parametri.toString()));
            } catch (JMSException | NamingException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
        return true;
    }

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        List<Korisnik> korisnici = ubp.dohvatiSveKorisnike();
        for (Korisnik k : korisnici) {
            if (k.getKorime().equals(korisnickoIme)
                    && k.getLozinka().equals(lozinka)) {
                return true;
            }
        }
        return false;
    }

    private void akcijaKorisnik(String korisnickoIme, String lozinka) {
        if (!provjeriKorisnika(korisnickoIme, lozinka)) {
            odgovorKorisniku = GreskeZahtjeva.error11;
        } else {
            odgovorKorisniku = "OK 10;";

        }
    }

    private void izvrsiAkciju(String akcija) {
        if (akcija.contains("PAUZA")) {
            akcijaPauza();
        } else if (akcija.contains("KRENI")) {
            akcijaKreni();
        } else if (akcija.contains("PASIVNO")) {
            akcijaPasivno();
        } else if (akcija.contains("AKTIVNO")) {
            akcijaAktivno();
        } else if (akcija.contains("STANI")) {
            akcijaStani();
        } else if (akcija.contains("STANJE")) {
            akcijaStanje();
        }
    }

    private void akcijaPauza() {
        if (pauza) {
            odgovorKorisniku = GreskeZahtjeva.error12;
        } else {
            pauza = true;
            odgovorKorisniku = "OK 10;";
        }
    }

    private void akcijaKreni() {
        if (pauza) {
            pauza = false;
            odgovorKorisniku = "OK 10;";
        } else {
            odgovorKorisniku = GreskeZahtjeva.error13;
        }
    }

    private void akcijaPasivno() {
        if (pozadinskaDretva.isPasivno()) {
            odgovorKorisniku = GreskeZahtjeva.error14;
        } else {
            pozadinskaDretva.setPasivno(true);
            odgovorKorisniku = "OK 10;";
        }
    }

    private void akcijaAktivno() {
        if (pozadinskaDretva.isPasivno()) {
            pozadinskaDretva.setPasivno(false);
            odgovorKorisniku = "OK 10;";
        } else {
            odgovorKorisniku = GreskeZahtjeva.error15;
        }
    }

    private void akcijaStani() {
        if (server.isPokrenutZavrsetak()) {
            odgovorKorisniku = GreskeZahtjeva.error16;
        } else {
            odgovorKorisniku = "OK 10;";
            server.setPokrenutZavrsetak(true);
            server.zavrsetakRada();
        }
    }

    private void akcijaStanje() {
        if (!pauza && !pozadinskaDretva.isPasivno()) {
            odgovorKorisniku = "OK 11;";
        } else if (!pauza && pozadinskaDretva.isPasivno()) {
            odgovorKorisniku = "OK 12;";
        } else if (pauza && !pozadinskaDretva.isPasivno()) {
            odgovorKorisniku = "OK 13;";
        } else if (pauza && pozadinskaDretva.isPasivno()) {
            odgovorKorisniku = "OK 14;";
        }
    }

    private void izvrsiAkcijuGrupe(String akcija) {
        if (akcija.contains("DODAJ")) {
            akcijaDodajGrupa();
        } else if (akcija.contains("PREKID")) {
            akcijaPrekidGrupa();
        } else if (akcija.contains("KRENI")) {
            akcijaKreniGrupa();
        } else if (akcija.contains("PAUZA")) {
            akcijaPauzaGrupa();
        } else if (akcija.contains("STANJE")) {
            akcijaStanjeGrupa();
        }
    }

    private void akcijaDodajGrupa() {
        StatusKorisnika statusKorisnika = AerodromiWSKlijent.dajStatusGrupe(NWTIS_user, NWTIS_password);
        if (statusKorisnika == StatusKorisnika.REGISTRIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error20;
        } else {
            AerodromiWSKlijent.registrirajGrupu(NWTIS_user, NWTIS_password);
            odgovorKorisniku = "OK 20;";
        }
    }

    private void akcijaPrekidGrupa() {
        StatusKorisnika statusKorisnika = AerodromiWSKlijent.dajStatusGrupe(NWTIS_user, NWTIS_password);
        if (statusKorisnika == StatusKorisnika.DEREGISTRIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error21;
        } else {
            AerodromiWSKlijent.deregistrirajGrupu(NWTIS_user, NWTIS_password);
            odgovorKorisniku = "OK 20;";
        }
    }

    private void akcijaKreniGrupa() {
        StatusKorisnika statusKorisnika = AerodromiWSKlijent.dajStatusGrupe(NWTIS_user, NWTIS_password);
        if (statusKorisnika == StatusKorisnika.DEREGISTRIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error21;
        } else if (statusKorisnika == StatusKorisnika.AKTIVAN) {
            odgovorKorisniku = GreskeZahtjeva.error22;
        } else if (statusKorisnika == StatusKorisnika.REGISTRIRAN) {
            AerodromiWSKlijent.postaviAvioneGrupe(NWTIS_user, NWTIS_password, ubp.dohvatiSveAvione());
            AerodromiWSKlijent.aktivirajGrupu(NWTIS_user, NWTIS_password);
            odgovorKorisniku = "OK 20;";
        }
    }

    private void akcijaPauzaGrupa() {
        StatusKorisnika statusKorisnika = AerodromiWSKlijent.dajStatusGrupe(NWTIS_user, NWTIS_password);
        if (statusKorisnika == StatusKorisnika.DEREGISTRIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error21;
        } else if (statusKorisnika == StatusKorisnika.BLOKIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error23;
        } else {
            AerodromiWSKlijent.blokirajGrupu(NWTIS_user, NWTIS_password);
            odgovorKorisniku = "OK 20;";
        }
    }

    private void akcijaStanjeGrupa() {
        StatusKorisnika statusKorisnika = AerodromiWSKlijent.dajStatusGrupe(NWTIS_user, NWTIS_password);
        if (statusKorisnika == StatusKorisnika.DEREGISTRIRAN) {
            odgovorKorisniku = GreskeZahtjeva.error21;
        } else if (statusKorisnika == StatusKorisnika.AKTIVAN) {
            odgovorKorisniku = "OK 21;";
        } else if (statusKorisnika == StatusKorisnika.BLOKIRAN) {
            odgovorKorisniku = "OK 22;";
        } else if (statusKorisnika == StatusKorisnika.REGISTRIRAN) {
            odgovorKorisniku = "OK 21;";
        }
    }

    private String kreirajPoruku(String komanda) {
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSSS");
        jo.addProperty("id", id);
        jo.addProperty("komanda", komanda);
        jo.addProperty("vrijeme", sdf.format(new Date()));
        id++;
        return gson.toJson(jo);
    }

    private Message createJMSMessageForjmsNWTiS_psimec_1(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToNWTiS_psimec_1(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/NWTiS_QF_projekt");
        Connection conn = null;
        Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("jms/NWTiS_psimec_1");
            MessageProducer mp = s.createProducer(destination);
            mp.send(createJMSMessageForjmsNWTiS_psimec_1(s, messageData));
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

}
