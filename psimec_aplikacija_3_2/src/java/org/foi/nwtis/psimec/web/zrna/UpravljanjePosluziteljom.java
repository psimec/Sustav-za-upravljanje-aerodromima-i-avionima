package org.foi.nwtis.psimec.web.zrna;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

@ManagedBean(name = "upravljanjePosluziteljom")
@SessionScoped
public class UpravljanjePosluziteljom {

    private Konfiguracija konfiguracija;
    private HttpSession sesija;
    private int port;
    private String server;
    private String korime;
    private String lozinka;
    private String akcija;

    public UpravljanjePosluziteljom() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        port = Integer.parseInt(konfiguracija.dajPostavku("port"));
        server = konfiguracija.dajPostavku("server");
        sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        korime = (String) sesija.getAttribute("korime");
        lozinka = (String) sesija.getAttribute("lozinka");
        System.err.println(korime + " " + lozinka);
    }

    public void pripremiKomadnu(String akcija) {
        this.akcija = akcija;
        String komanda = "KORISNIK " + korime + "; LOZINKA " + lozinka + ";";
        if (!akcija.contains("AUTH")) {
            komanda += " {" + akcija + "; }";
        }
        izrsiKomandu(komanda, akcija, false);
    }

    public void pripremiKomadnuGrupa(String akcija) {
        this.akcija = akcija;
        String komanda = "KORISNIK " + korime + "; LOZINKA " + lozinka + "; GRUPA {" + akcija + "; }";

        izrsiKomandu(komanda, akcija, true);
    }

    public void izrsiKomandu(String komanda, String akcija, boolean grupa) {

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
            if (!grupa) {
                pripremiStatus(odgovor);
            } else {
                pripremiStatusGrupa(odgovor);
            }
        } catch (IOException ex) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "GREŠKA!", "Poslužitelj ne radi");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    private void pripremiStatus(String odgovor) {
        if (odgovor.contains("POSLUZITELJ NA PAUZI")) {
            prikaziStatus("ERR", "POSLUZITELJ NA PAUZI");
            return;
        }

        switch (akcija) {
            case "AUTH":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "autentifikacija uspjesna");
                } else {
                    prikaziStatus("ERR 11", "korisnik ili ne lozinka odgovara");
                }
                break;
            case "PAUZA":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "posluzitelj pauziran");
                } else {
                    prikaziStatus("ERR 12", "posluzitelj je u pauzi");
                }
                break;
            case "KRENI":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "posluzitelj pokrenut");
                } else {
                    prikaziStatus("ERR 13", "posluzitelj nije u pauzi");
                }
                break;
            case "PASIVNO":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "posluzitelj postavljen u pasivan rad");
                } else {
                    prikaziStatus("ERR 14", " preuzimanje je u pasivnom radu");
                }
                break;
            case "AKTIVNO":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "posluzitelj aktivan");
                } else {
                    prikaziStatus("ERR 15", " preuzimanje je u aktivnom radu");
                }
                break;
            case "STANI":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 10", "posluzitelj staje s radom");
                } else {
                    prikaziStatus("ERR 15", " postupak prekida je vec pokrenut");
                }
                break;
            case "STANJE":
                if (odgovor.contains("OK 11")) {
                    prikaziStatus("OK 12", "posluzitelj preuzima sve komande i preuzima podatke za aerodrome");
                } else if (odgovor.contains("OK 12")) {
                    prikaziStatus("OK 12", "posluzitelj preuzima sve komanda i ne preuzima podatke za aerodrome");
                } else if (odgovor.contains("OK 13")) {
                    prikaziStatus("OK 13", "posluzitelj preuzima samo poslužiteljske komanda i preuzima podatke za aerodrome");
                } else if (odgovor.contains("OK 14")) {
                    prikaziStatus("OK 14", "posluzitelj  preuzima samo poslužiteljske komanda i ne preuzima podatke za aerodrome");
                }
                break;
        }
    }

    private void prikaziStatus(String status, String poruka) {
        if (status.contains("OK")) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, status, poruka);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, status, poruka);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    private void pripremiStatusGrupa(String odgovor) {
        if (odgovor.contains("POSLUZITELJ NA PAUZI")) {
            prikaziStatus("ERR", "POSLUZITELJ NA PAUZI");
            return;
        }
        switch (akcija) {
            case "DODAJ":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 20", "grupa dodana");
                } else {
                    prikaziStatus("ERR 20", "grupa je registirana");
                }
                break;
            case "PREKID":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 20", "grupa deregistrirana");
                } else {
                    prikaziStatus("ERR 21", "grupa nije registirana");
                }
                break;
            case "KRENI":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 20", "grupa pokrenuta");
                } else if (odgovor.contains("ERR 21")) {
                    prikaziStatus("ERR 21", " grupa nije registirana");
                } else if (odgovor.contains("ERR 22")) {
                    prikaziStatus("ERR 22", " grupa je aktivna");
                } else if (odgovor.contains("ERR 23")) {
                    prikaziStatus("ERR 23", " grupa je blokirana");
                }
                break;
            case "PAUZA":
                if (odgovor.contains("OK")) {
                    prikaziStatus("OK 20", "grupa pauzirana");
                } else if (odgovor.contains("ERR 21")) {
                    prikaziStatus("ERR 21", " grupa nije registirana");
                } else if (odgovor.contains("ERR 23")) {
                    prikaziStatus("ERR 23", " grupa je vec blokirana");
                }
                break;
            case "STANJE":
                if (odgovor.contains("ERR 21")) {
                    prikaziStatus("ERR 21", " grupa nije registirana");
                } else if (odgovor.contains("OK 21")) {
                    prikaziStatus("OK 21", "grupa je aktivna");
                } else if (odgovor.contains("OK 22")) {
                    prikaziStatus("OK 22", " grupa blokirana");
                }
                break;
        }
    }

}
