package org.foi.nwtis.psimec.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psimec.konfiguracije.Konfiguracija;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;

public class Server extends Thread {
    
    private Konfiguracija konfiguracija;
    private ServerSocket serverSocket;
    public List<DretvaZahtjeva> dretveZahtjeva = new ArrayList();
    private PozadinskaDretva pozadinskaDretva;
    private boolean pokrenutZavrsetak = false;
    public boolean radi = true;
    private int port;
    
    

    @Override
    public void interrupt() {
        
        if(serverSocket != null)
            try {
                serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt();
    }

    @Override
    public void run() {
        if (provjeriPort(port)) {
            pokreniServer();
        }
    }

    @Override
    public synchronized void start() {
        konfiguracija = SlusacAplikacije.getKonfiguracija();
        port = Integer.parseInt(konfiguracija.dajPostavku("port"));
        pripremiDretve();
        pozadinskaDretva.start();
        super.start();
    }

    public void pripremiDretve() {
        int maxBrojDretvi = Integer.parseInt(konfiguracija.dajPostavku("maks.dretvi"));

        pozadinskaDretva = new PozadinskaDretva();
        for (int i = 1; i <= maxBrojDretvi; i++) {
            DretvaZahtjeva dz = new DretvaZahtjeva(
                    pozadinskaDretva,
                    this,
                    konfiguracija);
            this.dretveZahtjeva.add(dz);
        }
    }

    public void pokreniServer() {

        int brojCekaca = Integer.parseInt(konfiguracija.dajPostavku("maks.cekaca"));

        try {
            this.serverSocket = new ServerSocket(port, brojCekaca);
            while (radi) {
                Socket socket = serverSocket.accept();
                DretvaZahtjeva dretvaZahtjeva = dajSlobodnuDretvu();
                if (dretvaZahtjeva == null) {
                    nemaSlobodnihDretvi(socket);
                    continue;
                }
                pokreniDretvuZahtjeva(dretvaZahtjeva, socket);
            }
        } catch (IOException ex) {
            System.out.println("Gašenje servera!");
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    public void pokreniDretvuZahtjeva(DretvaZahtjeva dretvaZahtjeva, Socket socket) {
        dretvaZahtjeva.setSocket(socket);
        if (dretvaZahtjeva.getState() == Thread.State.WAITING) {
            synchronized (dretvaZahtjeva) {
                dretvaZahtjeva.notify();
            }
        } else {
            dretvaZahtjeva.start();
        }
    }

    public void nemaSlobodnihDretvi(Socket socket) throws IOException {
        OutputStreamWriter os = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        os.append("ERROR 01; nema raspoložive dretve");//TODO provjeri
        os.flush();
        socket.shutdownOutput();
    }

    public DretvaZahtjeva dajSlobodnuDretvu() {
        for (DretvaZahtjeva dz : dretveZahtjeva) {
            if (dz.getState() == Thread.State.WAITING
                    || !dz.isAlive()) {
                return dz;
            }
        }
        return null;
    }

    public boolean provjeriPort(int port) {
        boolean rezultat = true;

        if (provjeriZauzetostPorta(port)) {
            System.out.println("Port je već zauzet, pokušajte kasnije!");
            rezultat = false;
        }
        if (port < 8000 || port > 9999) {
            System.out.println("Port mora biti između 8000 i 9999");
            rezultat = false;
        }
        return rezultat;
    }

    public boolean provjeriZauzetostPorta(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return false;
        } catch (IOException ex) {
            return true;
        }
    }

    public void zavrsetakRada() {
        try {
            dretveZahtjeva.forEach((dz) -> {
                if (dz.getState() == Thread.State.TIMED_WAITING) {
                    dz.interrupt();
                } else {
                    dz.radi = false;
                }
            });
            if (pozadinskaDretva != null) {
                pozadinskaDretva.interrupt();
            }
            this.radi = false;
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PozadinskaDretva getPozadinskaDretva() {
        return pozadinskaDretva;
    }

    public boolean isPokrenutZavrsetak() {
        return pokrenutZavrsetak;
    }

    public void setPokrenutZavrsetak(boolean pokrenutZavrsetak) {
        this.pokrenutZavrsetak = pokrenutZavrsetak;
    }
}
