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

/**
 * Klasa klijent za rad s REST API-em za letove.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class RestKlijentLetova {

  private ServletContext konfig;

  /**
   * Konstruktor koji prima ServletContext objekt za konfiguraciju.
   */
  public RestKlijentLetova(ServletContext konfig) {
    this.konfig = konfig;
  }

  /**
   * Dohvaća listu letova za zadani ICAO kod, dan, počevši od zadanog broja i dohvaća zadanu
   * količinu letova.
   */
  public List<LetAviona> getLetovi(String icao, String dan, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAviona> letovi = rc.getLetovi(icao, dan, odBroja, broj);
    rc.close();
    return letovi;
  }

  /**
   * Dodaje novi let u sustav i vraća odgovor.
   */
  public Response dodajLet(LetAviona let) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Response response = rc.dodajLet(let);
    rc.close();
    return response;
  }

  /**
   * Dohvaća listu letova između dva aerodroma za zadane ICAO kodove i dan.
   */
  public List<LetAviona> getLetoviDvaAerodroma(String icaoOd, String icaoDo, String dan) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAviona> letovi = rc.getLetoviDvaAerodroma(icaoOd, icaoDo, dan);
    rc.close();
    return letovi;
  }

  /**
   * Dohvaća listu spremljenih letova s identifikatorima.
   */
  public List<LetAvionaID> dohvatiSpremljeneLetove() {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<LetAvionaID> spremljeniLetovi = rc.dohvatiSpremljeneLetove();
    rc.close();
    return spremljeniLetovi;
  }

  /**
   * Briše let s zadanom ID vrijednosti i vraća odgovor.
   */
  public Response obrisiLet(int id) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Response response = rc.obrisiLet(id);
    rc.close();
    return response;
  }

  /**
   * Unutarnja klasa za rad s REST API-em za letove.
   */
  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;

    /**
     * Konstruktor koji prima ServletContext objekt za konfiguraciju.
     */
    public RestKKlijent(ServletContext konfig) {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String uri = (konfiguracija.dajPostavku("adresa.wa_1")).toString();
      client = ClientBuilder.newClient();
      webTarget = client.target(uri).path("letovi");
    }

    /**
     * Dohvaća listu letova za zadani ICAO kod, dan, počevši od zadanog broja i dohvaća zadanu
     * količinu letova.
     */
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

    /**
     * Dodaje novi let u sustav i vraća odgovor.
     */
    public Response dodajLet(LetAviona let) throws ClientErrorException {
      WebTarget resource = webTarget;
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      return request.post(Entity.entity(let, MediaType.APPLICATION_JSON), Response.class);
    }

    /**
     * Dohvaća listu letova između dva aerodroma za zadane ICAO kodove i dan.
     */
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

    /**
     * Dohvaća listu spremljenih letova s identifikatorima.
     */
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

    /**
     * Briše let s zadanom ID vrijednosti i vraća odgovor.
     */
    public Response obrisiLet(int id) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(Integer.toString(id));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
      return request.delete(Response.class);
    }

    /**
     * Zatvara klijentsku vezu.
     */
    public void close() {
      client.close();
    }

  }

}
