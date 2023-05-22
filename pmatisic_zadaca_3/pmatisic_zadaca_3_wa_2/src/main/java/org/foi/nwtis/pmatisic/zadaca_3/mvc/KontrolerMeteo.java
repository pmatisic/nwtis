package org.foi.nwtis.pmatisic.zadaca_3.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.ws.WsMeteo.endpoint.Meteo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * 
 * Klasa kontroler za upravljanje aerodromima u aplikaciji.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 *
 */
@Controller
@Path("meteo")
@RequestScoped
public class KontrolerMeteo {

  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_zadaca_3_wa_1/meteo?wsdl")
  private Meteo service;

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  @GET
  @Path("{icao}")
  @View("meteo.jsp")
  public void getMeteo(@PathParam("icao") String icao) {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var port = service.getWsMeteoPort();
    var meteo = port.dajMeteo(icao);

    try {
      model.put("meteo", meteo);
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @GET
  @View("meteoAdresa.jsp")
  public void getMeteoAdresa(@QueryParam("adresa") String adresa) {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var port = service.getWsMeteoPort();
    var meteoAdresa = port.dajMeteoAdresa(adresa);

    try {
      model.put("meteoAdresa", meteoAdresa);
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
