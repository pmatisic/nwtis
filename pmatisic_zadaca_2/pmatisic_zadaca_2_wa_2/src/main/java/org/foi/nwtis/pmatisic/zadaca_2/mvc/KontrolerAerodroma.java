package org.foi.nwtis.pmatisic.zadaca_2.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_2.rest.RestKlijentAerodroma;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  @Context
  private ServletContext konfig;

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  @GET
  @View("aerodromi.jsp")
  public void getAerodromi(@QueryParam("odBroja") Integer odBroja) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String nekiBroj = konfiguracija.dajPostavku("stranica.brojRedova");
    int broj = Integer.parseInt(nekiBroj);
    if (odBroja == null) {
      odBroja = 1;
    }
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      var aerodromi = rca.getAerodromi(odBroja, broj);
      model.put("aerodromi", aerodromi);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("konf", konfig);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("{icao}")
  @View("aerodrom.jsp")
  public void getAerodrom(@QueryParam("icao") String icao) {
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      var aerodrom = rca.getAerodrom(icao);
      model.put("aerodrom", aerodrom);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("udaljenosti2aerodroma")
  @View("aerodromiUdaljenosti.jsp")
  public void getAerodromiUdaljenost(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {}

}
