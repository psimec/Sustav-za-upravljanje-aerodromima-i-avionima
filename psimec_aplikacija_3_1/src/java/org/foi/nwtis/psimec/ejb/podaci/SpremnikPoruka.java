package org.foi.nwtis.psimec.ejb.podaci;

import java.io.Serializable;
import java.util.List;

public class SpremnikPoruka implements Serializable{

    private List<KomandaPoruka> listaPorukaKomandi;
    private List<MQTTPoruka> listaPorukaMQTT;

    public SpremnikPoruka(List<KomandaPoruka> listaPorukaKomandi, List<MQTTPoruka> listaPorukaMQTT) {
        this.listaPorukaKomandi = listaPorukaKomandi;
        this.listaPorukaMQTT = listaPorukaMQTT;
    }

    public List<KomandaPoruka> getListaPorukaKomandi() {
        return listaPorukaKomandi;
    }

    public void setListaPorukaKomandi(List<KomandaPoruka> listaPorukaKomandi) {
        this.listaPorukaKomandi = listaPorukaKomandi;
    }

    public List<MQTTPoruka> getListaPorukaMQTT() {
        return listaPorukaMQTT;
    }

    public void setListaPorukaMQTT(List<MQTTPoruka> listaPorukaMQTT) {
        this.listaPorukaMQTT = listaPorukaMQTT;
    }
}
