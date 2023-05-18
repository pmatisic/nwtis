package org.foi.nwtis.pmatisic.zadaca_3.mvc;

import org.foi.nwtis.pmatisic.zadaca_3.ws.WsAerodromi.endpoint.Aerodromi;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.xml.ws.WebServiceRef;

/**
 *
 * @author NWTiS
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {
  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_zadaca_3_wa_1/aerodromi?wsdl")
  private Aerodromi service;

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  @GET
  @Path("svi")
  @View("aerodromi.jsp")
  public void getAerodromi() {
    try {

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("{icao}")
  @View("aerodrom.jsp")
  public void getAerodrom(@PathParam("icao") String icao) {
    try {
      var port = service.getWsAerodromiPort();
      var aerodrom = port.dajAerodrom(icao);
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
