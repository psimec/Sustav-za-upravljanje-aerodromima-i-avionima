package org.foi.nwtis.psimec.web;

import org.foi.nwtis.psimec.ejb.podaci.SlusacKomandi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Klasa Informator putnika je krajnja točka za websockete
 * Služi za upravljanje porukama
 * 
 * @author Paskal
 */
@ServerEndpoint("/infoKomande")
public class InformatorPorukaKomande implements SlusacKomandi{ 

    static List<Session> sjednice = new ArrayList<>();

    @OnMessage
    public void onMessage(String message) {
        for (Session s : sjednice) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException ex) {
                    Logger.getLogger(InformatorPorukaKomande.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig conf) {
        sjednice.add(session);
        System.out.println("WS open: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, EndpointConfig conf) {
        System.out.println("WS close: " + session.getId());
        sjednice.remove(session);
    }

    /**
     * Metoda salje poruku svim otvorenim sjednicama
     * 
     * @param poruka Poruka koja se šalje
     */
    @Override
    public void saljiPoruku(String poruka) { 
        for (Session s : sjednice) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(poruka);
                } catch (IOException ex) {
                    Logger.getLogger(InformatorPorukaKomande.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
