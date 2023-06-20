package org.foi.nwtis.pmatisic.projekt.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.rest.RestKlijentDnevnika;
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
@Path("dnevnik")
@RequestScoped
public class KontrolerDnevnika {

  @Context
  private ServletContext konfig;

  @Inject
  private Models model;

  @GET
  @View("pogled_5_7.jsp")
  public void pocetak(@QueryParam("odBroja") Integer odBroja, @QueryParam("vrsta") String vrsta) {

    if (odBroja == null) {
      odBroja = 1;
    }

    RestKlijentDnevnika rcd = new RestKlijentDnevnika(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    int broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var zapisi = rcd.dohvatiZapise(odBroja, broj, vrsta);

    try {
      model.put("zapisi", zapisi);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("vrsta", vrsta);
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
