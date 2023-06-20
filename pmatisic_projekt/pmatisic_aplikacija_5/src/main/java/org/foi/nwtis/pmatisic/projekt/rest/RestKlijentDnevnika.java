package org.foi.nwtis.pmatisic.projekt.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Dnevnik;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentDnevnika {

  private ServletContext konfig;

  public RestKlijentDnevnika(ServletContext konfig) {
    this.konfig = konfig;
  }

  public List<Dnevnik> dohvatiZapise(int odBroja, int broj, String vrsta) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Dnevnik[] jsonZapisi = rc.getZapis(odBroja, broj, vrsta);
    List<Dnevnik> zapisi = null;
    if (jsonZapisi == null) {
      zapisi = new ArrayList<>();
    } else {
      zapisi = Arrays.asList(jsonZapisi);
    }
    rc.close();
    return zapisi;
  }

  public List<Dnevnik> dohvatiZapise() {
    return this.dohvatiZapise(1, 20, "");
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;

    public RestKKlijent(ServletContext konfig) {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String uri = (konfiguracija.dajPostavku("rest.url")).toString();
      client = ClientBuilder.newClient();
      webTarget = client.target(uri).path("dnevnik");
    }

    public Dnevnik[] getZapis(int odBroja, int broj, String vrsta) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.queryParam("odBroja", odBroja).queryParam("broj", broj)
          .queryParam("vrsta", vrsta);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Dnevnik[] zapisi = gson.fromJson(request.get(String.class), Dnevnik[].class);
      return zapisi;
    }

    public void close() {
      client.close();
    }

  }

}
