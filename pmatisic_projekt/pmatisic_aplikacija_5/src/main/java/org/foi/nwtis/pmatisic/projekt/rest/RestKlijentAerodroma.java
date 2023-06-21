package org.foi.nwtis.pmatisic.projekt.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Udaljenost;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodrom;
import org.foi.nwtis.podaci.Aerodrom;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

public class RestKlijentAerodroma {

  @Context
  private ServletContext konfig;

  public RestKlijentAerodroma(ServletContext konfig) {
    this.konfig = konfig;
  }

  public List<Aerodrom> dohvatiAerodrome(int odBroja, int broj, String traziNaziv,
      String traziDrzavu) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom[] jsonAerodromi = rc.getAerodromi(odBroja, broj, traziNaziv, traziDrzavu);
    List<Aerodrom> aerodromi;
    if (jsonAerodromi == null) {
      aerodromi = new ArrayList<>();
    } else {
      aerodromi = Arrays.asList(jsonAerodromi);
    }
    rc.close();
    return aerodromi;
  }

  public List<Aerodrom> dohvatiAerodrome() {
    return this.dohvatiAerodrome(1, 20, "", "");
  }

  public Aerodrom dohvatiAerodrom(String icao) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Aerodrom a = rc.getAerodrom(icao);
    rc.close();
    return a;
  }

  public List<Udaljenost> dohvatiUdaljenostiDvajuAerodroma(String icaoOd, String icaoDo) {
    RestKKlijent rc = new RestKKlijent(konfig);
    Udaljenost[] jsonUdaljenosti = rc.getUdaljenostiDvajuAerodroma(icaoOd, icaoDo);
    List<Udaljenost> udaljenosti;
    if (jsonUdaljenosti == null) {
      udaljenosti = new ArrayList<>();
    } else {
      udaljenosti = Arrays.asList(jsonUdaljenosti);
    }
    rc.close();
    return udaljenosti;
  }

  public String dohvatiIzracun(String icaoOd, String icaoDo) {
    RestKKlijent rc = new RestKKlijent(konfig);
    String izracun = rc.getIzracun(icaoOd, icaoDo);
    rc.close();
    return izracun;
  }

  public List<UdaljenostAerodrom> dohvatiPrvuUdaljenost(String icaoOd, String icaoDo) {
    RestKKlijent rc = new RestKKlijent(konfig);
    UdaljenostAerodrom[] jsonUdaljenosti = rc.getPrvaUdaljenost(icaoOd, icaoDo);
    List<UdaljenostAerodrom> udaljenosti;
    if (jsonUdaljenosti == null) {
      udaljenosti = new ArrayList<>();
    } else {
      udaljenosti = Arrays.asList(jsonUdaljenosti);
    }
    rc.close();
    return udaljenosti;
  }

  public List<UdaljenostAerodrom> dohvatiDruguUdaljenost(String icaoOd, String drzava, String km) {
    RestKKlijent rc = new RestKKlijent(konfig);
    UdaljenostAerodrom[] jsonUdaljenosti = rc.getDrugaUdaljenost(icaoOd, drzava, km);
    List<UdaljenostAerodrom> udaljenosti;
    if (jsonUdaljenosti == null) {
      udaljenosti = new ArrayList<>();
    } else {
      udaljenosti = Arrays.asList(jsonUdaljenosti);
    }
    rc.close();
    return udaljenosti;
  }

  static class RestKKlijent {

    private final WebTarget webTarget;
    private final Client client;

    public RestKKlijent(ServletContext konfig) {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String uri = (konfiguracija.dajPostavku("rest.url")).toString();
      client = ClientBuilder.newClient();
      webTarget = client.target(uri).path("aerodromi");
    }

    public Aerodrom[] getAerodromi(int odBroja, int broj, String traziNaziv, String traziDrzavu)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.queryParam("odBroja", odBroja).queryParam("broj", broj);

      if (traziNaziv != null && !traziNaziv.isEmpty()) {
        resource = resource.queryParam("traziNaziv", traziNaziv);
      }

      if (traziDrzavu != null && !traziDrzavu.isEmpty()) {
        resource = resource.queryParam("traziDrzavu", traziDrzavu);
      }

      return resource.request(MediaType.APPLICATION_JSON).get(Aerodrom[].class);
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

    public Udaljenost[] getUdaljenostiDvajuAerodroma(String icaoOd, String icaoDo)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/{1}", new Object[] {icaoOd, icaoDo}));

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      return resource.request(MediaType.APPLICATION_JSON).get(Udaljenost[].class);
    }

    public String getIzracun(String icaoOd, String icaoDo) throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource
          .path(java.text.MessageFormat.format("{0}/izracunaj/{1}", new Object[] {icaoOd, icaoDo}));

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      Gson gson = new Gson();
      String izracun = gson.fromJson(request.get(String.class), String.class);
      return izracun;
    }

    public UdaljenostAerodrom[] getPrvaUdaljenost(String icaoOd, String icaoDo)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource = resource.path(
          java.text.MessageFormat.format("{0}/udaljenost1/{1}", new Object[] {icaoOd, icaoDo}));

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      return resource.request(MediaType.APPLICATION_JSON).get(UdaljenostAerodrom[].class);
    }

    public UdaljenostAerodrom[] getDrugaUdaljenost(String icaoOd, String drzava, String km)
        throws ClientErrorException {
      WebTarget resource = webTarget;
      resource =
          resource.path(java.text.MessageFormat.format("{0}/udaljenost2", new Object[] {icaoOd}));
      resource = resource.queryParam("drzava", drzava).queryParam("km", km);

      Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);

      if (request.get(String.class).isEmpty()) {
        return null;
      }

      return resource.request(MediaType.APPLICATION_JSON).get(UdaljenostAerodrom[].class);
    }

    public void close() {
      client.close();
    }

  }

}
