package org.foi.nwtis.psimec.ejb.podaci;

import java.io.Serializable;

public class MQTTPoruka implements Serializable {

    private int id;
    private String poruka;
    private String vrijeme;
    
    public MQTTPoruka(int id, String poruka, String vrijeme) {
        this.id = id;
        this.poruka = poruka;
        this.vrijeme = vrijeme;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }
}
