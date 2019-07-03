package org.foi.nwtis.psimec.web.zrna;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.eb.MqttPoruke;
import org.foi.nwtis.psimec.ejb.sb.MqttPorukeFacade;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

/**
 *
 * @author Paskal
 */
@Named(value = "pregledMqttPoruka")
@SessionScoped
public class PregledMqttPoruka implements Serializable {

    @EJB
    private MqttPorukeFacade mqttPorukeFacade;

    Konfiguracija konfiguracija;
    String korime = null;
    String lozinka;
    private int brojRedovaTablice;
    private List<MqttPoruke> poruke;

    public PregledMqttPoruka() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija != null && sesija.getAttribute("korime") != null && sesija.getAttribute("lozinka") != null) {
            korime = (String) sesija.getAttribute("korime");
            lozinka = (String) sesija.getAttribute("lozinka");
        }
    }

    @PostConstruct
    public void init() {
        if (korime != null) {
            poruke = mqttPorukeFacade.preuzmiKorisnikovePoruke(korime);
        }
    }

    public void obrisiPoruke() {
        mqttPorukeFacade.obrisiKorisnikovePoruke(korime);
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public void setBrojRedovaTablice(int brojRedovaTablice) {
        this.brojRedovaTablice = brojRedovaTablice;
    }

    public List<MqttPoruke> getPoruke() {
        poruke = mqttPorukeFacade.preuzmiKorisnikovePoruke(korime);
        return poruke;
    }

    public void setPoruke(List<MqttPoruke> poruke) {
        this.poruke = poruke;
    }

}
