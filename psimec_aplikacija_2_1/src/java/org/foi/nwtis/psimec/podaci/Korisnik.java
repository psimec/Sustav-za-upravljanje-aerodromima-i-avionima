package org.foi.nwtis.psimec.podaci;

/**
 * Klasa Korisnik služi isključivo za pohranu podataka Klasa se sastoji od privatnih varijabli, konstruktora te get i set metoda
 * za sve varijable
 *
 * @author Paskal Šimec
 */
public class Korisnik {

    private String korime;
    private String lozinka;
    private String ime;
    private String prezime;
    private String email;

    public Korisnik() {
    }

    public Korisnik(String korime, String lozinka, String ime, String prezime, String email) {
        this.korime = korime;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
    }

    public String getKorime() {
        return korime;
    }

    public void setKorime(String korime) {
        this.korime = korime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
