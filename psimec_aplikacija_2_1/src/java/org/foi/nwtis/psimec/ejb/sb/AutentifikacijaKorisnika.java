package org.foi.nwtis.psimec.ejb.sb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

@Stateful
@LocalBean
public class AutentifikacijaKorisnika {


    public boolean autentificiraj(String korime, String lozinka) {
        try {
            KorisniciREST korisniciREST = new KorisniciREST();
            String json = "{\"lozinka\":\"" + lozinka + "\"}";
            String odgovor = korisniciREST.getJsonIdAuth(korime, URLEncoder.encode(json, "UTF-8"));
            if (odgovor.contains("ERR")) {
                return false;
            } else {
                return true;
            }
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }
    static class KorisniciREST {

        private WebTarget webTarget;
        private Client client;
        private static final String BASE_URI = "http://localhost:8084/psimec_aplikacija_3_2/webresources";

        public KorisniciREST() {
            client = javax.ws.rs.client.ClientBuilder.newClient();
            webTarget = client.target(BASE_URI).path("korisnici");
        }

        public String putJson(Object requestEntity) throws ClientErrorException {
            return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String postJson(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJsonId(String id, String lozinka, String korisnickoIme) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (lozinka != null) {
                resource = resource.queryParam("lozinka", lozinka);
            }
            if (korisnickoIme != null) {
                resource = resource.queryParam("korisnickoIme", korisnickoIme);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String getJsonIdAuth(String id, String auth) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (auth != null) {
                resource = resource.queryParam("auth", auth);
            }
            resource = resource.path(java.text.MessageFormat.format("{0}", new Object[]{id}));
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public String deleteJson() throws ClientErrorException {
            return webTarget.request().delete(String.class);
        }

        public String putJsonId(Object requestEntity, String id) throws ClientErrorException {
            return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
        }

        public String getJson(String lozinka, String korisnickoIme) throws ClientErrorException {
            WebTarget resource = webTarget;
            if (lozinka != null) {
                resource = resource.queryParam("lozinka", lozinka);
            }
            if (korisnickoIme != null) {
                resource = resource.queryParam("korisnickoIme", korisnickoIme);
            }
            return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
        }

        public void close() {
            client.close();
        }
    }

}
