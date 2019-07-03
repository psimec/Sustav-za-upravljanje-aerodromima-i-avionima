package org.foi.nwtis.psimec.web.slusaci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.psimec.ejb.podaci.SpremnikPoruka;
import org.foi.nwtis.psimec.ejb.sb.CentarPoruka;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.psimec.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.psimec.konfiguracije.NemaKonfiguracije;

public class SlusacAplikacije implements ServletContextListener {
    
    CentarPoruka centarPoruka = lookupCentarPorukaBean();
    
    private static ServletContext sc;
    private static Konfiguracija konfiguracija;
    private String datotekaPoruka;
    
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
            datotekaPoruka = konfiguracija.dajPostavku("datoteka.serijalizacije");
            System.out.println("[3_2] Konfiguracija ucitana");
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File dat = new File(datotekaPoruka);
        SpremnikPoruka spremnikPoruka = null;
        if (dat.exists()) {
            try (FileInputStream in = new FileInputStream(dat);
                    ObjectInputStream ois = new ObjectInputStream(in)) {
                spremnikPoruka = (SpremnikPoruka) ois.readObject();
                centarPoruka.setListaPorukaKomandi(spremnikPoruka.getListaPorukaKomandi());
                centarPoruka.setListaPorukaMQTT(spremnikPoruka.getListaPorukaMQTT());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {      
        File datoteka = new File(datotekaPoruka);     
        try (FileOutputStream out = new FileOutputStream(datoteka);
                ObjectOutputStream oos = new ObjectOutputStream(out)) {       
            SpremnikPoruka spremnikPoruka = new SpremnikPoruka(
                    centarPoruka.getListaPorukaKomandi(),
                    centarPoruka.getListaPorukaMQTT());
            oos.writeObject(spremnikPoruka);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private CentarPoruka lookupCentarPorukaBean() {
        try {
            Context c = new InitialContext();
            return (CentarPoruka) c.lookup("java:global/psimec_aplikacija_3/psimec_aplikacija_3_1/CentarPoruka!org.foi.nwtis.psimec.ejb.sb.CentarPoruka");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
