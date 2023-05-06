package org.foi.nwtis.pmatisic.zadaca_2.rest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.rest.podaci.LetAviona;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

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

    public void close() {
      client.close();
    }

  }

}
