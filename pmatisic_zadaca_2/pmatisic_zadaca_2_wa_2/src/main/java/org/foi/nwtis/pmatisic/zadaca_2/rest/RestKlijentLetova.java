package org.foi.nwtis.pmatisic.zadaca_2.rest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestKlijentLetova {

  private ServletContext konfig;

  public RestKlijentLetova(ServletContext konfig) {
    this.konfig = konfig;
  }

  public List<LetAviona> getLetovi(String icao, String dan, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAviona> letovi = rc.getLetovi(icao, dan, odBroja, broj);
    rc.close();
    return letovi;
  }

  public Response dodajLet(LetAviona let) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Response response = rc.dodajLet(let);
    rc.close();
    return response;
  }

  public List<LetAviona> getLetoviDvaAerodroma(String icaoOd, String icaoDo, String dan) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAviona> letovi = rc.getLetoviDvaAerodroma(icaoOd, icaoDo, dan);
    rc.close();
    return letovi;
  }

  public List<LetAvionaID> dohvatiSpremljeneLetove() {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAvionaID> spremljeniLetovi = rc.dohvatiSpremljeneLetove();
    rc.close();
    return spremljeniLetovi;
  }

  public Response obrisiLet(int id) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Response response = rc.obrisiLet(id);
    rc.close();
    return response;
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;

    public RestKKlijent(ServletContext konfig) {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String uri = (konfiguracija.dajPostavku("adresa.wa_1")).toString();
      client = ClientBuilder.newClient();
      webTarget = client.target(uri).path("letovi");
    }

    public List<LetAviona> getLetovi(String icao, String dan, int odBroja, int broj)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(icao).queryParam("dan", dan).queryParam("odBroja", odBroja)
          .queryParam("broj", broj);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<LetAviona>>() {}.getType();
      List<LetAviona> letovi = gson.fromJson(request.get(String.class), listType);
      return letovi;
    }

    public Response dodajLet(LetAviona let) throws ClientErrorException {
      WebTarget resource = webTarget;
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      return request.post(Entity.entity(let, MediaType.APPLICATION_JSON), Response.class);
    }

    public List<LetAviona> getLetoviDvaAerodroma(String icaoOd, String icaoDo, String dan)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(icaoOd).path(icaoDo).queryParam("dan", dan);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<LetAviona>>() {}.getType();
      List<LetAviona> letovi = gson.fromJson(request.get(String.class), listType);
      return letovi;
    }

    public List<LetAvionaID> dohvatiSpremljeneLetove() throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path("spremljeni");
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<LetAvionaID>>() {}.getType();
      List<LetAvionaID> spremljeniLetovi = gson.fromJson(request.get(String.class), listType);
      return spremljeniLetovi;
    }

    public Response obrisiLet(int id) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(Integer.toString(id));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      return request.delete(Response.class);
    }

    public void close() {
      client.close();
    }

  }

}
