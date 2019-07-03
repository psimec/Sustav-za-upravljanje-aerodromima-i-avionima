package org.foi.nwtis.psimec.web;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class PosiljateljPoruka {

    private static int id = 1;
    
    private Properties properties = new Properties();
    private String factory = "jms/NWTiS_QF_projekt"; //TODO konfiguracija
    private String queueName = "jms/NWTiS_psimec_1";
    private InitialContext initialContext;
    QueueConnectionFactory connectionFactory;
    private Queue queue;

    public PosiljateljPoruka() {
        try {
            initialContext = new InitialContext(properties);
            queue = (Queue) initialContext.lookup(queueName);
            connectionFactory = (QueueConnectionFactory) initialContext.lookup(factory);
        } catch (NamingException ex) {
            Logger.getLogger(PosiljateljPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salji(String poruka) {
        try {
            connectionFactory.createConnection();
        } catch (JMSException ex) {
            Logger.getLogger(PosiljateljPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (JMSContext jMSContext = connectionFactory.createContext()) {
            TextMessage textMessage = jMSContext.createTextMessage();
            try {
                textMessage.setText(kreirajPoruku(poruka));
            } catch (JMSException ex) {
                Logger.getLogger(PosiljateljPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
            jMSContext.createProducer().send(queue, textMessage);
            id++;
        }
    }

    private String kreirajPoruku(String komanda) {
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSSS");
        jo.addProperty("id", id);
        jo.addProperty("komanda", komanda);
        jo.addProperty("vrijeme", sdf.format(new Date()));
        return gson.toJson(jo);
    }

}
