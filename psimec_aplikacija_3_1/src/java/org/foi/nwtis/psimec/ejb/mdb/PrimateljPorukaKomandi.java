/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.ejb.mdb;

import com.google.gson.Gson;
import java.util.ArrayList;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import org.foi.nwtis.psimec.ejb.podaci.KomandaPoruka;
import org.foi.nwtis.psimec.ejb.podaci.SlusacKomandi;
import org.foi.nwtis.psimec.ejb.sb.CentarPoruka;
/**
 *
 * @author Paskal
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_psimec_1"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class PrimateljPorukaKomandi implements MessageListener {

    @EJB
    private CentarPoruka centarPoruka;
    public static SlusacKomandi slusacKomandi;
    

    public PrimateljPorukaKomandi() {
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            Gson gson = new Gson();
            KomandaPoruka komandaPoruka = gson.fromJson(textMessage.getText(), KomandaPoruka.class);
            komandaPoruka.setKorime(komandaPoruka.getKomanda().substring(9, komandaPoruka.getKomanda().indexOf("; LOZINKA")));
            if (centarPoruka.getListaPorukaKomandi() == null) {
                centarPoruka.setListaPorukaKomandi(new ArrayList());
            }
            if(slusacKomandi != null){
                JsonObjectBuilder job = Json.createObjectBuilder()
                        .add("korime", komandaPoruka.getKorime());
                slusacKomandi.saljiPoruku(job.build().toString());
            }
     
            centarPoruka.dodajKomandu(komandaPoruka);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
