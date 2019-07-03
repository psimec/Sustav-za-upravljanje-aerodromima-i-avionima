package org.foi.nwtis.psimec.ejb.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.foi.nwtis.psimec.ejb.mdb.PrimateljPorukaKomandi;
import org.foi.nwtis.psimec.ejb.mdb.PrimateljPorukaMqtt;
import org.foi.nwtis.psimec.ejb.podaci.KomandaPoruka;
import org.foi.nwtis.psimec.ejb.podaci.MQTTPoruka;
import org.foi.nwtis.psimec.ejb.podaci.SlusacKomandi;

@Startup
@Singleton
@LocalBean
public class CentarPoruka {

    private List<KomandaPoruka> listaPorukaKomandi = new ArrayList();
    private List<MQTTPoruka> listaPorukaMQTT = new ArrayList();

    public void pripremiCentar(SlusacKomandi sk) {
        if (PrimateljPorukaKomandi.slusacKomandi == null) {
            PrimateljPorukaKomandi.slusacKomandi = sk;
        }

        if (PrimateljPorukaMqtt.slusacKomandi == null) {
            PrimateljPorukaMqtt.slusacKomandi = sk;
        }
    }

    public void dodajKomandu(KomandaPoruka komandaPoruka) {
        listaPorukaKomandi.add(komandaPoruka);
    }

    public void dodajMQTT(MQTTPoruka mQTTPoruka) {
        listaPorukaMQTT.add(mQTTPoruka);
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

    public void obrisiKomandu(int id) {
        List<KomandaPoruka> novaLista = new ArrayList();
        for (KomandaPoruka komandaPoruka : getListaPorukaKomandi()) {
            if (komandaPoruka.getId() == id) {
                continue;
            }
            novaLista.add(komandaPoruka);
        }

        this.listaPorukaKomandi = novaLista;
    }

    public void obrisiMqtt(int id) {
        List<MQTTPoruka> novaLista = new ArrayList();
        for (MQTTPoruka mqttPoruka : getListaPorukaMQTT()) {
            if (mqttPoruka.getId() == id) {
                continue;
            }
            novaLista.add(mqttPoruka);
        }

        this.listaPorukaMQTT = novaLista;
    }

}
