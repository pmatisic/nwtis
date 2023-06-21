package org.foi.nwtis.pmatisic.projekt.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.pmatisic.projekt.servis.WsLetovi.endpoint.LetAviona;
import org.foi.nwtis.pmatisic.projekt.servis.WsLetovi.endpoint.Letovi;
import org.foi.nwtis.pmatisic.projekt.web.SakupljacJmsPoruka;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceRef;

@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetova {

  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_aplikacija_4/letovi?wsdl")
  private Letovi service;

  @Inject
  SakupljacJmsPoruka sakupljacJmsPoruka;

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  @Context
  private HttpServletRequest request;

  @GET
  @View("pogled_5_6.jsp")
  public void pocetak() {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    try {
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
  @Path("interval")
  @View("pogled_5_6_1.jsp")
  public Response dohvatiLetovePremaIntervalima(@QueryParam("icao") String icao,
      @QueryParam("datumOd") String datumOd, @QueryParam("datumDo") String datumDo,
      @QueryParam("odBroja") Integer odBroja) {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    HttpSession session = request.getSession(false);
    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }
    Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
    String korime = korisnik.getKorime();
    String lozinka = korisnik.getLozinka();
    int broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
    if (odBroja == null) {
      odBroja = 1;
    }

    try {
      var port = service.getWsLetoviPort();
      List<LetAviona> letovi =
          port.dajPolaskeInterval(korime, lozinka, icao, datumOd, datumDo, odBroja, broj);

      model.put("icao", icao);
      model.put("datumOd", datumOd);
      model.put("datumDo", datumDo);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("letovi", letovi);
      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja letova.").build();
    }

  }

  @GET
  @Path("dan")
  @View("pogled_5_6_2.jsp")
  public Response dohvatiSpremljeneLetoveNaDatum(@QueryParam("icao") String icao,
      @QueryParam("datum") String datum, @QueryParam("odBroja") Integer odBroja) {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    HttpSession session = request.getSession(false);
    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }
    Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
    String korime = korisnik.getKorime();
    String lozinka = korisnik.getLozinka();
    int broj = Integer.parseInt(konfiguracija.dajPostavku("stranica.brojRedova"));
    if (odBroja == null) {
      odBroja = 1;
    }

    try {
      var port = service.getWsLetoviPort();
      List<LetAviona> letovi = port.dajPolaskeNaDan(korime, lozinka, icao, datum, odBroja, broj);

      model.put("icao", icao);
      model.put("datum", datum);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("letovi", letovi);
      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja letova.").build();
    }

  }

  @GET
  @Path("os")
  @View("pogled_5_6_3.jsp")
  public Response dohvatiLetoveNaDatum(@QueryParam("icao") String icao,
      @QueryParam("datum") String datum) {

    HttpSession session = request.getSession(false);
    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }
    Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
    String korime = korisnik.getKorime();
    String lozinka = korisnik.getLozinka();

    try {
      var port = service.getWsLetoviPort();
      List<LetAviona> letovi = port.dajPolaskeNaDanOS(korime, lozinka, icao, datum);

      model.put("letovi", letovi);
      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja letova.").build();
    }

  }

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
