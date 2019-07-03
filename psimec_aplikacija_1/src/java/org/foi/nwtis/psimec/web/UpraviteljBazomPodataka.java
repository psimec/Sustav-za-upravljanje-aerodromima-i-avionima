package org.foi.nwtis.psimec.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.psimec.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.psimec.web.podaci.Korisnik;
import org.foi.nwtis.psimec.web.slusaci.SlusacAplikacije;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.psimec.web.podaci.Aerodrom;
import org.foi.nwtis.psimec.web.podaci.Dnevnik;
import org.foi.nwtis.psimec.ws.klijenti.Avion;

public class UpraviteljBazomPodataka {
    
    private final BP_Konfiguracija bpk;
    private final String server;
    private final String korisnik;
    private final String lozinka;
    private final String baza;
    private final String driver;
    
    public UpraviteljBazomPodataka() {
        bpk = SlusacAplikacije.getBpk();
        server = bpk.getServerDatabase();
        korisnik = bpk.getUserUsername();
        lozinka = bpk.getUserPassword();
        baza = bpk.getUserDatabase();
        driver = bpk.getDriverDatabase();
    }
    
    public boolean izvrsiInsertUpit(String upit) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();) {
            s.execute(upit);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public List<Korisnik> dohvatiSveKorisnike() {
        List<Korisnik> korisnici = new ArrayList();
        String upit = "SELECT * FROM KORISNICI";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            
            while (rs.next()) {
                Korisnik korisnik = new Korisnik(
                        rs.getString("korime"),
                        rs.getString("lozinka"),
                        rs.getString("ime"),
                        rs.getString("prezime"),
                        rs.getString("email")
                );
                korisnici.add(korisnik);
            }
            
            return korisnici;
            
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<Aerodrom> dohvatiSveMojeAerodrome() {
        List<Aerodrom> aerodromi = new ArrayList();
        
        String upit = "SELECT * FROM MYAIRPORTS";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            
            while (rs.next()) {
                String lokacija[] = rs.getString("coordinates").split(",");
                String lat = lokacija[0];
                String lon = lokacija[1];
                
                Aerodrom aerodrom = new Aerodrom();
                aerodrom.setNaziv(rs.getString("name"));
                aerodrom.setIcao(rs.getString("ident"));
                aerodrom.setDrzava(rs.getString("iso_country"));
                aerodrom.setLokacija(new Lokacija(lat, lon));
                
                aerodromi.add(aerodrom);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return aerodromi;
    }
    
    public List<Aerodrom> dohvatiSveKorisnikoveAerodrome(String korime) {
        List<Aerodrom> aerodromi = new ArrayList();
        List<String> popisIcao = new ArrayList();
        String upit = "SELECT * FROM KORISNIKAERODROM WHERE korime = '" + korime + "'";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            
            while (rs.next()) {
                popisIcao.add(rs.getString("ident"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (String iata : popisIcao) {
            aerodromi.add(dohvatiMojAerodromPremaIcao(iata));
        }
        return aerodromi;
    }
    
    public Aerodrom dohvatiKorisnikovAerodromPremaIcao(String korime, String icao) {
        List<Aerodrom> aerodromi = dohvatiSveKorisnikoveAerodrome(korime);
        for (Aerodrom a : aerodromi) {
            if (a.getIcao() == null ? icao == null : a.getIcao().equals(icao)) {
                return a;
            }
        }
        return null;
    }
    
    public Aerodrom dohvatiMojAerodromPremaIcao(String icao) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String upit = "SELECT * FROM MYAIRPORTS WHERE ident='" + icao + "'";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            if (rs.next()) {
                String lokacija[] = rs.getString("coordinates").split(",");
                String lat = lokacija[0];
                String lon = lokacija[1];
                
                Aerodrom aerodrom = new Aerodrom();
                aerodrom.setNaziv(rs.getString("name"));
                aerodrom.setIcao(rs.getString("ident"));
                aerodrom.setDrzava(rs.getString("iso_country"));
                aerodrom.setLokacija(new org.foi.nwtis.rest.podaci.Lokacija(lat, lon));
                
                return aerodrom;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public List<AvionLeti> dohvatiAvione(String icao) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<AvionLeti> letovi = new ArrayList();
        String upit = "SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "'";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                AvionLeti let = new AvionLeti();
                let.setLastSeen(rs.getInt("lastSeen"));
                
                let.setIcao24(rs.getString("icao24"));
                let.setFirstSeen(rs.getInt("firstSeen"));
                let.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                let.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                let.setCallsign(rs.getString("callsign"));
                let.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                let.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                let.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                let.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                let.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                let.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                letovi.add(let);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return letovi;
    }
    
    public AvionLeti dajZadnjiPreuzetiAvion(String icao) {
        AvionLeti let = null;
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String upit = "SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "' ORDER BY stored DESC LIMIT 1 ";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            
            rs.next();
            let = new AvionLeti();
            let.setLastSeen(rs.getInt("lastSeen"));
            
            let.setIcao24(rs.getString("icao24"));
            let.setFirstSeen(rs.getInt("firstSeen"));
            let.setEstDepartureAirport(rs.getString("estDepartureAirport"));
            let.setEstArrivalAirport(rs.getString("estArrivalAirport"));
            let.setCallsign(rs.getString("callsign"));
            let.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
            let.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
            let.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
            let.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
            let.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
            let.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
            
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return let;
    }
    
    public List<AvionLeti> dajZadnjihNPreuzetihAviona(String icao, int n) {
        List<AvionLeti> letovi = new ArrayList();
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String upit = "SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "' ORDER BY stored DESC LIMIT " + n + " ";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                AvionLeti let = new AvionLeti();
                let = new AvionLeti();
                let.setLastSeen(rs.getInt("lastSeen"));
                
                let.setIcao24(rs.getString("icao24"));
                let.setFirstSeen(rs.getInt("firstSeen"));
                let.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                let.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                let.setCallsign(rs.getString("callsign"));
                let.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                let.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                let.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                let.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                let.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                let.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                letovi.add(let);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return letovi;
    }
    
    public List<AvionLeti> dohvatiLetoveAviona(String icao24, int odVremena, int doVremena) {
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<AvionLeti> letovi = new ArrayList();
        String upit = "SELECT * FROM AIRPLANES WHERE icao24 ='" + icao24 + "' AND "
                + "LASTSEEN BETWEEN " + odVremena + " AND " + doVremena + ""
                + " ORDER BY firstSeen ";
        System.err.println(upit);
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                AvionLeti let = new AvionLeti();
                let.setLastSeen(rs.getInt("lastSeen"));
                let.setIcao24(rs.getString("icao24"));
                let.setFirstSeen(rs.getInt("firstSeen"));
                let.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                let.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                let.setCallsign(rs.getString("callsign"));
                let.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                let.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                let.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                let.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                let.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                let.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                letovi.add(let);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return letovi;
    }
    
    public List<AvionLeti> dohvatiAvionePoletjeleSAerodroma(String icao, int odVremena, int doVremena) {
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<AvionLeti> letovi = new ArrayList();
        String upit = "SELECT * FROM AIRPLANES WHERE ESTDEPARTUREAIRPORT='" + icao + "' AND "
                + "LASTSEEN BETWEEN " + odVremena + " AND " + doVremena + ""
                + " ORDER BY firstSeen ";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                AvionLeti let = new AvionLeti();
                let.setLastSeen(rs.getInt("lastSeen"));
                let.setIcao24(rs.getString("icao24"));
                let.setFirstSeen(rs.getInt("firstSeen"));
                let.setEstDepartureAirport(rs.getString("estDepartureAirport"));
                let.setEstArrivalAirport(rs.getString("estArrivalAirport"));
                let.setCallsign(rs.getString("callsign"));
                let.setEstDepartureAirportHorizDistance(rs.getInt("estDepartureAirportHorizDistance"));
                let.setEstDepartureAirportVertDistance(rs.getInt("estDepartureAirportVertDistance"));
                let.setEstArrivalAirportHorizDistance(rs.getInt("estArrivalAirportHorizDistance"));
                let.setEstArrivalAirportVertDistance(rs.getInt("estArrivalAirportVertDistance"));
                let.setDepartureAirportCandidatesCount(rs.getInt("departureAirportCandidatesCount"));
                let.setArrivalAirportCandidatesCount(rs.getInt("arrivalAirportCandidatesCount"));
                letovi.add(let);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return letovi;
    }
    
    public List<Avion> dohvatiSveAvione() {
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<Avion> letovi = new ArrayList();
        String upit = "SELECT * FROM AIRPLANES";
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                Avion let = new Avion();
                let.setIcao24(rs.getString("icao24"));
                let.setEstdepartureairport(rs.getString("estDepartureAirport"));
                let.setEstarrivalairport(rs.getString("estArrivalAirport"));
                let.setCallsign(rs.getString("callsign"));
                let.setId(1);
                letovi.add(let);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return letovi;
    }
    
    public Aerodrom dohvatiAerodromPremaIcao(String icao) {
        String upit = "SELECT name, ident, iso_country FROM AIRPORTS";
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            
            while (rs.next()) {
                String icaoAerodroma = rs.getString("ident");
                if (icaoAerodroma.compareTo(icao) == 0) {
                    Aerodrom aerodrom = new Aerodrom();
                    aerodrom.setNaziv(rs.getString("name"));
                    aerodrom.setIcao(icao);
                    aerodrom.setDrzava(rs.getString("iso_country"));
                    
                    return aerodrom;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public boolean dodajKorisnika(Korisnik korisnik) {
        String upit = "INSERT INTO korisnici VALUES("
                + "'" + korisnik.getKorime()
                + "','" + korisnik.getLozinka()
                + "','" + korisnik.getIme()
                + "','" + korisnik.getPrezime()
                + "','" + korisnik.getEmail() + "')";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajAvion(AvionLeti let) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        
        String upit = "INSERT INTO AIRPLANES VALUES(default,"
                + "'" + let.getIcao24() + "',"
                + let.getFirstSeen() + ","
                + "'" + let.getEstDepartureAirport() + "',"
                + let.getLastSeen() + ","
                + "'" + let.getEstArrivalAirport() + "',"
                + "'" + let.getCallsign().trim() + "',"
                + let.getEstDepartureAirportHorizDistance() + ","
                + let.getEstDepartureAirportVertDistance() + ","
                + let.getEstArrivalAirportHorizDistance() + ","
                + let.getEstArrivalAirportVertDistance() + ","
                + let.getDepartureAirportCandidatesCount() + ","
                + let.getArrivalAirportCandidatesCount() + ","
                + "'" + datum + "')";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean azurirajKorisnika(Korisnik korisnik) {
        String upit = "UPDATE korisnici"
                + " SET lozinka = '" + korisnik.getLozinka()
                + "', ime = '" + korisnik.getIme()
                + "', prezime = '" + korisnik.getPrezime()
                + "', email = '" + korisnik.getEmail()
                + "' WHERE korime = '" + korisnik.getKorime() + "';";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean azurirajAerodrom(Aerodrom aerodrom, String icao) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "UPDATE MYAIRPORTS SET COORDINATES='"
                + aerodrom.getLokacija().getLatitude() + ","
                + aerodrom.getLokacija().getLongitude() + "', STORED="
                + "'" + datum + "' WHERE IDENT='" + icao + "'";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajZapisUDnevik(String korime, String zahtjev) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "INSERT INTO dnevnik  (id, korime, zahtjev, aplikacija, stored, status)"
                + " VALUES(null,'" + korime + "','" + zahtjev + "','Posluzitelj','" + datum + "', 1)";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajZapisUDnevikSoap(String korime, String zahtjev) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "INSERT INTO dnevnik  (id, korime, zahtjev, aplikacija, stored, status)"
                + " VALUES(null,'" + korime + "','" + zahtjev + "','SOAP WS','" + datum + "', 1)";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajZapisUDnevikRest(String korime, String zahtjev, String status) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "INSERT INTO dnevnik (id, korime, zahtjev, aplikacija, stored, status)"
                + " VALUES(null,'" + korime + "','" + zahtjev + "','REST WS','" + datum + "', " + status + ")";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajAerodromKorisniku(Aerodrom aerodrom, String korime) {
        String upit = "INSERT INTO KORISNIKAERODROM VALUES("
                + "'" + aerodrom.getIcao() + "',"
                + "'" + korime + "')";
        return izvrsiInsertUpit(upit);
    }
    
    public boolean dodajMojAerodrom(Aerodrom aerodrom) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String datum = sdf.format(new Date());
        String upit = "INSERT INTO MYAIRPORTS VALUES("
                + "'" + aerodrom.getIcao() + "',"
                + "'" + aerodrom.getNaziv() + "',"
                + "'" + aerodrom.getDrzava() + "',"
                + "'" + aerodrom.getLokacija().getLatitude() + ","
                + aerodrom.getLokacija().getLongitude() + "',"
                + "'" + datum + "')";
        
        return izvrsiInsertUpit(upit);
    }
    
    public List<Dnevnik> dohvatiSveZapiseDnevnika(String uvjeti) {
        
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        List<Dnevnik> zapisiDnevnika = new ArrayList();
        String upit = "SELECT * FROM DNEVNIK " + uvjeti;
        try (Connection con = DriverManager.getConnection(server + baza, korisnik, lozinka);
                Statement s = con.createStatement();
                ResultSet rs = s.executeQuery(upit);) {
            while (rs.next()) {
                Dnevnik zapis = new Dnevnik();
                zapis.setId(rs.getInt("id"));
                zapis.setKorime(rs.getString("korime"));
                zapis.setZahtjev(rs.getString("zahtjev"));
                zapis.setAplikacija(rs.getString("aplikacija"));
                zapis.setDatum(rs.getString("stored"));
                zapis.setStatus(Integer.parseInt(rs.getString("status")));
                zapisiDnevnika.add(zapis);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpraviteljBazomPodataka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return zapisiDnevnika;
    }
}
