package org.foi.nwtis.psimec.slusaci;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.psimec.ejb.eb.MqttPoruke;
import org.foi.nwtis.psimec.ejb.sb.MqttPorukeFacade;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.podaci.Aerodrom;
import org.foi.nwtis.psimec.pomocni.JsonMqttModel;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class MqttSlusac extends Thread {

    MqttPorukeFacade mqttPorukeFacade = lookupMqttPorukeFacadeBean();
    private static int brojPoruke = 1;

    private final Konfiguracija konfiguracija;
    private final String user;
    private final String password;
    private final String host;
    private final int port;
    private final String destination;
    private List<Aerodrom> korisnikoviAerodromi;
    private String korisnikPoruka;
    private MQTT mqtt;
    private Gson gson;
    boolean kraj;
    Runnable runnable;
    private String korime;

    public MqttSlusac(Konfiguracija konfiguracija, List<Aerodrom> aerodromi, String korime) {
        this.konfiguracija = konfiguracija;
        port = Integer.parseInt(konfiguracija.dajPostavku("Mqtt.port"));
        host = konfiguracija.dajPostavku("Mqtt.server");
        user = konfiguracija.dajPostavku("Mqtt.korisnik");
        password = konfiguracija.dajPostavku("Mqtt.lozinka");
        destination = konfiguracija.dajPostavku("Mqtt.destinacija");
        korisnikPoruka = konfiguracija.dajPostavku("Mqtt.korisnik.poruka");
        korisnikoviAerodromi = aerodromi;
        this.korime = korime;
        kraj = false;
        gson = new Gson();
    }

    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }

    @Override
    public void run() {
        final CallbackConnection connection = mqtt.callbackConnection();
        connection.listener(new org.fusesource.mqtt.client.Listener() {
            @Override
            public void onConnected() {
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Prekinuta veza na MQTT");
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Problem u vezi na MQTT");
            }

            @Override
            public void onPublish(UTF8Buffer utfb, org.fusesource.hawtbuf.Buffer buffer, Runnable r) {
                String poruka = buffer.utf8().toString();
                //System.err.println(poruka);
                JsonMqttModel jmm = gson.fromJson(poruka, JsonMqttModel.class);
                try {
                    if (jmm.getKorisnik().equalsIgnoreCase(korisnikPoruka)) {
                        for (LinkedTreeMap a : (List<LinkedTreeMap>) (Object) korisnikoviAerodromi) {
                            System.err.println(poruka);
                            if (a.get("icao").equals(jmm.getAerodrom())) {
                                spremiPoruku(jmm);
                                sendJMSMessageToNWTiS_psimec_2(kreirajPoruku(poruka));
                                brojPoruke++;
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public synchronized void spremiPoruku(JsonMqttModel jmm) {
                MqttPoruke mqttPoruka = new MqttPoruke();
                mqttPoruka.setAerodrom(jmm.getAerodrom());
                mqttPoruka.setAvion(jmm.getAvion());
                mqttPoruka.setKorime(korime);
                mqttPoruka.setOznaka(jmm.getOznaka());
                mqttPoruka.setPoruka(jmm.getPoruka());
                mqttPoruka.setStored(new java.sql.Timestamp(new java.util.Date().getTime()));
                mqttPorukeFacade.create(mqttPoruka);
            }

        });

        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void value) {
                Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] qoses) {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable value) {
                        System.out.println("Problem kod pretplate na: " + destination);
                    }
                });
            }

            @Override
            public void onFailure(Throwable value) {
                System.out.println("Neuspjela pretplata na: " + destination);
            }
        });
        synchronized (MqttSlusac.class) {
            while (!kraj) {
                try {
                    MqttSlusac.class.wait();
                } catch (InterruptedException ex) {
                    System.out.println("Prekinuta veza na MQTT");
                    kraj = true;
                    UTF8Buffer[] topics = {new UTF8Buffer(destination)};
                    connection.unsubscribe(topics, new Callback<Void>() {
                        @Override
                        public void onSuccess(Void t) {
                            System.out.println("Odjava s: " + destination);
                        }

                        @Override
                        public void onFailure(Throwable thrwbl) {
                            System.out.println("Problem kod odjave s: " + destination);
                        }

                    });
                }
            }
        }
    }

    @Override
    public synchronized void start() {
        try {
            mqtt = new MQTT();
            mqtt.setHost(host, port);
            mqtt.setUserName(user);
            mqtt.setPassword(password);
            super.start();
        } catch (URISyntaxException ex) {
            Logger.getLogger(MqttSlusac.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setKraj(boolean kraj) {
        this.kraj = kraj;
    }

    private MqttPorukeFacade lookupMqttPorukeFacadeBean() {
        try {
            Context c = new InitialContext();
            return (MqttPorukeFacade) c.lookup("java:global/psimec_aplikacija_2/psimec_aplikacija_2_1/MqttPorukeFacade!org.foi.nwtis.psimec.ejb.sb.MqttPorukeFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private String kreirajPoruku(String poruka) {
        Gson gson = new Gson();
        JsonObject jo = new JsonObject();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.SSSS");
        jo.addProperty("id", brojPoruke);
        jo.addProperty("poruka", poruka);
        jo.addProperty("vrijeme", sdf.format(new Date()));
        return gson.toJson(jo);
    }

    private Message createJMSMessageForjmsNWTiS_psimec_2(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToNWTiS_psimec_2(Object messageData) throws JMSException, NamingException {
        Context c = new InitialContext();
        ConnectionFactory cf = (ConnectionFactory) c.lookup("jms/NWTiS_QF_projekt_2");
        Connection conn = null;
        Session s = null;
        try {
            conn = cf.createConnection();
            s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
            Destination destination = (Destination) c.lookup("jms/NWTiS_psimec_2");
            MessageProducer mp = s.createProducer(destination);
            mp.send(createJMSMessageForjmsNWTiS_psimec_2(s, messageData));
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

}
