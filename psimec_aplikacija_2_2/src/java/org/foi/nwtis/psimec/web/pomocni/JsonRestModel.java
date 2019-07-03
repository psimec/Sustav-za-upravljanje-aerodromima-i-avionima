package org.foi.nwtis.psimec.web.pomocni;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JsonRestModel {
    @SerializedName("odgovor")
    private List<Object> odgovor;
    @SerializedName("status")
    private String status;
    @SerializedName("poruka")
    private String poruka;

    public List<Object> getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(List<Object> odgovor) {
        this.odgovor = odgovor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPoruka() {
        return poruka;
    }

    public void setPoruka(String poruka) {
        this.poruka = poruka;
    }
    
    
}
