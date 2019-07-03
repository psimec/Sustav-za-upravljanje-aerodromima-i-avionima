package org.foi.nwtis.psimec.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.psimec.ejb.sb.CentarKorisnika;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.psimec.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.psimec.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.psimec.slusaci.MqttSlusac;

public class SlusacAplikacije implements ServletContextListener {
    
    private static ServletContext sc;
    private static Konfiguracija konfiguracija;

    public static ServletContext getSc() {
        return sc;
    }

    public static Konfiguracija getKonfiguracija() {
        return konfiguracija;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.sc = sce.getServletContext();
        String putanja = sc.getRealPath("/WEB-INF");
        String datoteka = putanja + File.separator + sc.getInitParameter("konfiguracija");
        try {
            konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            System.out.println("[2_2] Konfiguracija ucitana");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
