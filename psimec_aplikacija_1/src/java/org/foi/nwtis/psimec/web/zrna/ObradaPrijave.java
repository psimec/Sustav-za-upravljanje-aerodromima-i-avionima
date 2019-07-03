/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimsdec.web.zrna;

import java.io.IOException;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Paskal
 */
@ManagedBean(name = "obradaPrijave")
@RequestScoped
public class ObradaPrijave implements Serializable {

    private String korime;
    private String lozinka;

    public ObradaPrijave() {
    }

    public String prijava() {

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();

        try {
            request.login(korime, lozinka);
        } catch (ServletException ex) {
            return "error";
        }
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
        try {
            request.authenticate(response);
        } catch (IOException | ServletException ex) {
            Logger.getLogger(ObradaPrijave.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "j_security_check";
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

}
