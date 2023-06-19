package org.foi.nwtis.pmatisic.projekt.rest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Udaljenost;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodromDrzava;
import org.foi.nwtis.podaci.Aerodrom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentAerodroma {

  private ServletContext konfig;

  public RestKlijentAerodroma(ServletContext konfig) {
    this.konfig = konfig;
  }

  public List<Aerodrom> getAerodromi(int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom[] jsonAerodromi = rc.getAerodromi(odBroja, broj);
    List<Aerodrom> aerodromi;
    if (jsonAerodromi == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(jsonAerodromi);
    }
    rc.close();
    return aerodromi;
  }

  public List<Aerodrom> getAerodromi() {
    return this.getAerodromi(1, 20);
  }

  public Aerodrom getAerodrom(String icao) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom a = rc.getAerodrom(icao);
    rc.close();
    return a;
  }

  public List<Udaljenost> getUdaljenostiAerodroma(String icaoFrom, String icaoTo) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<Udaljenost> udaljenosti = rc.getUdaljenostiAerodroma(icaoFrom, icaoTo);
    rc.close();
    return udaljenosti;
  }

  public List<UdaljenostAerodrom> getUdaljenostiZaAerodome(String icao, int odBroja, int broj) {
    RestKKlijent rc = new RestKKlijent(konfig);
    List<UdaljenostAerodrom> udaljenosti = rc.getUdaljenostiZaAerodome(icao, odBroja, broj);
    rc.close();
    return udaljenosti;
  }

  public List<UdaljenostAerodrom> getUdaljenostiZaAerodome(String icao) {
    return this.getUdaljenostiZaAerodome(icao, 1, 20);
  }

  public UdaljenostAerodromDrzava getNajduljiPutDrzave(String icao) {
    RestKKlijent rc = new RestKKlijent(konfig);
    UdaljenostAerodromDrzava najduljiPut = rc.getNajduljiPutDrzave(icao);
    rc.close();
    return najduljiPut;
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;

    public RestKKlijent(ServletContext konfig) {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String uri = (konfiguracija.dajPostavku("adresa.wa_1")).toString();
      client = ClientBuilder.newClient();
      webTarget = client.target(uri).path("aerodromi");
    }

    public Aerodrom[] getAerodromi(int odBroja, int broj) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.queryParam("odBroja", odBroja).queryParam("broj", broj);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Aerodrom[] aerodromi = gson.fromJson(request.get(String.class), Aerodrom[].class);
      return aerodromi;
    }

    public Aerodrom getAerodrom(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Aerodrom aerodrom = gson.fromJson(request.get(String.class), Aerodrom.class);
      return aerodrom;
    }

    public List<Udaljenost> getUdaljenostiAerodroma(String icaoFrom, String icaoTo)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] {icaoFrom, icaoTo}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<Udaljenost>>() {}.getType();
      List<Udaljenost> udaljenosti = gson.fromJson(request.get(String.class), listType);
      return udaljenosti;
    }

    public List<UdaljenostAerodrom> getUdaljenostiZaAerodome(String icao, int odBroja, int broj)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/udaljenosti", new Object[] {icao}))
              .queryParam("odBroja", odBroja).queryParam("broj", broj);
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      Type listType = new TypeToken<ArrayList<UdaljenostAerodrom>>() {}.getType();
      List<UdaljenostAerodrom> udaljenosti = gson.fromJson(request.get(String.class), listType);
      return udaljenosti;
    }

    public UdaljenostAerodromDrzava getNajduljiPutDrzave(String icao) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource
          .path(java.text.MessageFormat.format("{0}/najduljiPutDrzave", new Object[] {icao}));
      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      UdaljenostAerodromDrzava najduljiPut =
          gson.fromJson(request.get(String.class), UdaljenostAerodromDrzava.class);
      return najduljiPut;
    }

    public void close() {
      client.close();
    }

  }

}
