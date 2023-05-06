package org.foi.nwtis.pmatisic.zadaca_2.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_2.rest.RestKlijentLetova;
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

@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetova {

  @Context
  private ServletContext konfig;

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  @GET
  @Path("{icao}")
  @View("letovi.jsp")
  public void getLetovi(@PathParam("icao") String icao, @QueryParam("dan") String dan,
      @QueryParam("odBroja") Integer odBroja) {

    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");

    if (odBroja == null) {
      odBroja = 1;
    }

    int broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var letovi = rcl.getLetovi(icao, dan, odBroja, broj);

    try {
      model.put("letovi", letovi);
      model.put("odBroja", odBroja);
      model.put("icao", icao);
      model.put("dan", dan);
      model.put("broj", broj);
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
