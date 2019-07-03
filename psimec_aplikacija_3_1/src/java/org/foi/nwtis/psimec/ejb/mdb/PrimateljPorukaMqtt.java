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
import org.foi.nwtis.psimec.ejb.podaci.MQTTPoruka;
import org.foi.nwtis.psimec.ejb.podaci.SlusacKomandi;
import org.foi.nwtis.psimec.ejb.sb.CentarPoruka;

/**
 *
 * @author Paskal
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_psimec_2"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class PrimateljPorukaMqtt implements MessageListener {

    @EJB
    private CentarPoruka centarPoruka;
    public static SlusacKomandi slusacKomandi;

    public PrimateljPorukaMqtt() {
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            Gson gson = new Gson();
            MQTTPoruka mqttPoruka = gson.fromJson(textMessage.getText(), MQTTPoruka.class);
            if (centarPoruka.getListaPorukaMQTT() == null) {
                centarPoruka.setListaPorukaMQTT(new ArrayList());
            }
            if (slusacKomandi != null) {
                JsonObjectBuilder job = Json.createObjectBuilder()
                        .add("mqtt", "mqtt");
                slusacKomandi.saljiPoruku(job.build().toString());
                //System.err.println("da");
            }

            centarPoruka.dodajMQTT(mqttPoruka);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
