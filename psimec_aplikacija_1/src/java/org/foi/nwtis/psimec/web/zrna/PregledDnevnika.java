package org.foi.nwtis.psimec.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.UpraviteljBazomPodataka;
import org.foi.nwtis.psimec.web.podaci.Dnevnik;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

@ManagedBean(name = "pregledDnevnika")
public class PregledDnevnika implements Serializable{

    private UpraviteljBazomPodataka ubp;
    private Konfiguracija konfiguracija;
    private List<Dnevnik> zapisiDnevnik;
    private final int brojRedovaTablice;
    private SimpleDateFormat simpleDateFormat;
    private String odVremena;
    private String doVremena;
    private String vrsta;

    public PregledDnevnika() {
        ubp = new UpraviteljBazomPodataka();
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        brojRedovaTablice = Integer.parseInt(konfiguracija.dajPostavku("tablica.brojRedaka"));
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    }

    @PostConstruct
    public void init() {
        dohvatiDnevnik();
    }

    public void dohvatiDnevnik() {
        vrsta = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("forma:vrsta");
        odVremena = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("forma:odVremena");
        doVremena = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("forma:doVremena");
        String upit = "";
        boolean prvi = true;
        if (vrsta != null) {
            if (!vrsta.isEmpty()) {
                prvi = false;
                upit = "WHERE aplikacija LIKE '" + vrsta + "%'"
                        + " OR korime LIKE '" + vrsta + "%' "
                        + " OR zahtjev LIKE '" + vrsta + "%'";
            }
        }

        if (odVremena != null) {
            if (!odVremena.isEmpty()) {
                if (provjeriDatum(odVremena, true)) {
                    if (prvi) {
                        upit = "WHERE stored > '" + odVremena + "'";
                        prvi = false;
                    } else {
                        upit += " AND stored > '" + odVremena + "'";
                    }
                }
            }
        }

        if (doVremena != null) {
            if (!doVremena.isEmpty()) {
                if (provjeriDatum(doVremena, false)) {
                    if (prvi) {
                        upit = "WHERE stored < '" + doVremena + "'";
                    } else {
                        upit += " AND stored < '" + doVremena + "'";
                    }
                }
            }
        }
        zapisiDnevnik = ubp.dohvatiSveZapiseDnevnika(upit);
    }

    public boolean provjeriDatum(String datum, boolean od) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            if(od)
                odVremena = simpleDateFormat.format(sdf.parse(datum).getTime());
            else
                doVremena = simpleDateFormat.format(sdf.parse(datum).getTime());
        } catch (ParseException ex) {
            FacesContext.getCurrentInstance().addMessage("forma:message", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Datum pogrešnog formata", null));		
            return false;

        }
        return true;
    }


    public List<Dnevnik> getZapisiDnevnik() {
        return zapisiDnevnik;
    }

    public void setZapisiDnevnik(List<Dnevnik> zapisiDnevnik) {
        this.zapisiDnevnik = zapisiDnevnik;
    }

    public String getOdVremena() {
        return odVremena;
    }

    public void setOdVremena(String odVremena) {
        this.odVremena = odVremena;
    }

    public String getDoVremena() {
        return doVremena;
    }

    public void setDoVremena(String doVremena) {
        this.doVremena = doVremena;
    }

    public int getBrojRedovaTablice() {
        return brojRedovaTablice;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

}
