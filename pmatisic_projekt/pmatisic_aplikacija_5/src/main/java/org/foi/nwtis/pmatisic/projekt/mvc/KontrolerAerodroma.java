package org.foi.nwtis.pmatisic.projekt.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.rest.RestKlijentAerodroma;
import org.foi.nwtis.pmatisic.projekt.servis.WsAerodromi.endpoint.Aerodromi;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.pmatisic.projekt.servis.WsMeteo.endpoint.Meteo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceRef;

@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_aplikacija_4/aerodromi?wsdl")
  private Aerodromi a;

  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_aplikacija_4/meteo?wsdl")
  private Meteo m;

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  @Context
  private HttpServletRequest request;

  @GET
  @View("pogled_5_5.jsp")
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
  @Path("svi")
  @View("pogled_5_5_1.jsp")
  public Response sviAerodromi(@QueryParam("odBroja") Integer odBroja,
      @QueryParam("traziNaziv") String traziNaziv, @QueryParam("traziDrzavu") String traziDrzavu,
      @QueryParam("icao") String icao) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
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
      var aerodromi = rca.dohvatiAerodrome(odBroja, broj, traziNaziv, traziDrzavu);
      var port = a.getWsAerodromiPort();
      var dodan = port.dodajAerodromZaLetove(korime, lozinka, icao);

      model.put("aerodromi", aerodromi);
      model.put("dodan", dodan);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("traziNaziv", traziNaziv);
      model.put("traziDrzavu", traziDrzavu);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja aerodroma.").build();
    }

  }

  @GET
  @Path("{icao}")
  @View("pogled_5_5_2.jsp")
  public Response aerodrom(@PathParam("icao") String icao) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    try {
      var aerodrom = rca.dohvatiAerodrom(icao);
      var port = m.getWsMeteoPort();
      var meteo = port.dajMeteo(icao);

      model.put("aerodrom", aerodrom);
      model.put("meteo", meteo);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja aerodroma.").build();
    }

  }

  @GET
  @Path("polasci")
  @View("pogled_5_5_3.jsp")
  public Response podatciPolazaka(@QueryParam("icao") String icao,
      @QueryParam("action") String action) {

    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
    String korime = korisnik.getKorime();
    String lozinka = korisnik.getLozinka();

    try {
      var port = a.getWsAerodromiPort();

      if ("activate".equals(action)) {
        port.aktivirajAerodromZaLetove(korime, lozinka, icao);
      } else if ("deactivate".equals(action)) {
        port.pauzirajAerodromZaLetove(korime, lozinka, icao);
      }

      var podatci = port.dajAerodromeZaLetoveSaStatusom(korime, lozinka);

      model.put("podatci", podatci);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja podataka.").build();
    }

  }

  @GET
  @Path("{icaoOd}/{icaoDo}")
  @View("pogled_5_5_4.jsp")
  public Response udaljenostiDvajuAerodroma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    try {
      var udaljenost = rca.dohvatiUdaljenostiDvajuAerodroma(icaoOd, icaoDo);

      model.put("udaljenost", udaljenost);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja udaljenosti.").build();
    }

  }

  @GET
  @Path("{icaoOd}/izracun/{icaoDo}")
  @View("pogled_5_5_5.jsp")
  public Response izracunaj(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    try {
      var udaljenost = rca.dohvatiIzracun(icaoOd, icaoDo);

      model.put("udaljenost", udaljenost);
      model.put("icaoOd", icaoOd);
      model.put("icaoDo", icaoDo);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja udaljenosti.").build();
    }

  }

  @GET
  @Path("{icaoOd}/udaljenost1/{icaoDo}")
  @View("pogled_5_5_6.jsp")
  public Response prvaUdaljenost(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    try {
      var udaljenost = rca.dohvatiPrvuUdaljenost(icaoOd, icaoDo);

      model.put("udaljenost", udaljenost);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja udaljenosti.").build();
    }

  }

  @GET
  @Path("{icaoOd}/udaljenost2")
  @View("pogled_5_5_7.jsp")
  public Response drugaUdaljenost(@PathParam("icaoOd") String icaoOd,
      @QueryParam("drzava") String drzava, @QueryParam("km") String km) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    HttpSession session = request.getSession(false);

    if (session == null) {
      return Response.status(Response.Status.UNAUTHORIZED)
          .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
    }

    try {
      var udaljenost = rca.dohvatiDruguUdaljenost(icaoOd, drzava, km);

      model.put("udaljenost", udaljenost);

      return Response.ok().build();

    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Dogodila se pogreška prilikom dohvaćanja udaljenosti.").build();
    }

  }

}
