package org.foi.nwtis.psimec.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class AIRP2RESTKlijent {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/psimec_aplikacija_1/webresources";

    public AIRP2RESTKlijent() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }

    public String getJsonIdAvion(String id, String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        resource = resource.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id}));
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public String postJsonIdAvion(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request().post(null, String.class);
    }

    public String putJson(Object requestEntity) throws ClientErrorException {
        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String postJson(Object requestEntity, String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String deleteJsonIdAvionAid(String id, String aid) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}/avion/{1}", new Object[]{id, aid})).request().delete(String.class);
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

    public String deleteJsonId(String id, String lozinka, String korisnickoIme) throws ClientErrorException {
        WebTarget resource = webTarget;
        if (lozinka != null) {
            resource = resource.queryParam("lozinka", lozinka);
        }
        if (korisnickoIme != null) {
            resource = resource.queryParam("korisnickoIme", korisnickoIme);
        }
        return resource.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request().delete(String.class);
    }

    public String deleteJson() throws ClientErrorException {
        return webTarget.request().delete(String.class);
    }

    public String putJsonId(Object requestEntity, String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}", new Object[]{id})).request(javax.ws.rs.core.MediaType.APPLICATION_JSON).put(javax.ws.rs.client.Entity.entity(requestEntity, javax.ws.rs.core.MediaType.APPLICATION_JSON), String.class);
    }

    public String deleteJsonIdAvion(String id) throws ClientErrorException {
        return webTarget.path(java.text.MessageFormat.format("{0}/avion", new Object[]{id})).request().delete(String.class);
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
