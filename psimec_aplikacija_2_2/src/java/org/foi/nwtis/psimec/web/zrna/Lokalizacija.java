package org.foi.nwtis.psimec.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 * Java zrno za implementaciju lokalizacije
 * 
 * @author Paskal Šimec
 */

@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private String jezik="hr";
    
    public Lokalizacija() {   
    }

    public String getJezik() {
        return jezik;
    }

    public void setJezik(String jezik) {
        this.jezik = jezik;
    }
    
    /**
     * Metoda vraća stranicu na koju se želi ići
     * 
     * @return String stranica
     */
    public Object odaberiJezik() {
        return "";
    }

}


