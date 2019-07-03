package org.foi.nwtis.psimec.web.slusaci;

import java.io.File;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.psimec.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.psimec.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.psimec.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.psimec.web.PozadinskaDretva;
import org.foi.nwtis.psimec.web.Server;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;


@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private static ServletContext sc;
    private static Konfiguracija konfiguracija;
    private static BP_Konfiguracija bpk;
    private Server server;

    public static ServletContext getSc() {
        return sc;
    }

    public static Konfiguracija getKonfiguracija() {
        return konfiguracija;
    }

    public static BP_Konfiguracija getBpk() {
        return bpk;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.sc = sce.getServletContext();
        String putanja = sc.getRealPath("WEB-INF");
        String datoteka = putanja + File.separator + sc.getInitParameter("konfiguracija");
        try {
            konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            bpk = new BP_Konfiguracija(datoteka);
            System.out.println("[1] Konfiguracija ucitana");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        server = new Server();
        server.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PozadinskaDretva pozadinskaDretva = server.getPozadinskaDretva();
        if (pozadinskaDretva != null) {
            pozadinskaDretva.interrupt();
        }
        try {
            pozadinskaDretva.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }     
        server.interrupt();
    }
}
