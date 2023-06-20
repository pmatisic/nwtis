package org.foi.nwtis.pmatisic.projekt.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.web.SakupljacJmsPoruka;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetova {

  @Inject
  SakupljacJmsPoruka sakupljacJmsPoruka;

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  @GET
  @Path("poruke")
  @View("pogled_5_4.jsp")
  public void dohvatiPoruke(@QueryParam("odBroja") Integer odBroja) {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    int broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    if (odBroja == null) {
      odBroja = 1;
    }

    List<String> poruke = sakupljacJmsPoruka.dohvatiPoruke(odBroja, broj);

    try {
      model.put("poruke", poruke);
      model.put("odBroja", odBroja);
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

  @DELETE
  @Path("por")
  public Response obrisiPoruke() {
    try {
      sakupljacJmsPoruka.obrisiSvePoruke();
      return Response.status(Response.Status.NO_CONTENT).build();
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
