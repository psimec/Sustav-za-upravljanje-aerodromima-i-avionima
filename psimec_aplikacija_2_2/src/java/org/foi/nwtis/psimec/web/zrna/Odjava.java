package org.foi.nwtis.psimec.web.zrna;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.NavigationHandler;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.psimec.ejb.sb.CentarKorisnika;

/**
 *
 * @author Paskal
 */
@ManagedBean
@RequestScoped
public class Odjava implements Serializable {
    
    @EJB
    private CentarKorisnika centarKorisnika;
    
    
    
    public Odjava() {
    }
    
    public String odjava() {
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija != null) {
            centarKorisnika.odjaviKorisnika((String) sesija.getAttribute("korime"));
            sesija.removeAttribute("korime");
            sesija.removeAttribute("lozinka");
            sesija.invalidate();
            UpravljanjeAerodromima upravljanjeAerodromima = FacesContext.getCurrentInstance().getApplication()
                    .evaluateExpressionGet(FacesContext.getCurrentInstance(), "#{upravljanjeAerodromima}", UpravljanjeAerodromima.class);
            upravljanjeAerodromima.odjavaSPrijemaPoruka();
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String redirect = "odjava";
        NavigationHandler myNav = facesContext.getApplication().getNavigationHandler();
        myNav.handleNavigation(facesContext, null, redirect);
        return "";
    }
    
    public String autentifikacija() {
        HttpSession sesija = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesija == null || sesija.getAttribute("korime") == null || sesija.getAttribute("lozinka") == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            String redirect = "odjava";
            NavigationHandler myNav = facesContext.getApplication().getNavigationHandler();
            myNav.handleNavigation(facesContext, null, redirect);
        }
        return "";
    }
}
