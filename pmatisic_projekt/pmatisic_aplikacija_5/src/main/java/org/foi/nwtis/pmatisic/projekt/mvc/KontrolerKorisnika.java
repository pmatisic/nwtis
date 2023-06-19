package org.foi.nwtis.pmatisic.projekt.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnici;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnik;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceRef;

@Controller
@Path("korisnici")
@RequestScoped
public class KontrolerKorisnika {

  @WebServiceRef(wsdlLocation = "http://localhost:8080/pmatisic_aplikacija_4/korisnici?wsdl")
  private Korisnici service;

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  @Context
  private HttpServletRequest request;

  @GET
  @View("pogled_5_2.jsp")
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
  @Path("registracija")
  @View("pogled_5_2_1.jsp")
  public void registracija() {

  }

  @GET
  @Path("prijava")
  @View("pogled_5_2_2.jsp")
  public void prijava() {

  }

  @POST
  @Path("reg")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response registracija(Korisnik noviKorisnik) {
    try {
      var port = service.getWsKorisniciPort();
      boolean uspjesnoDodan = port.dodajKorisnika(noviKorisnik);

      if (uspjesnoDodan) {
        return Response.status(Response.Status.CREATED).build();
      } else {
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("pri")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response prijava(String jsonBody) {
    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(jsonBody, JsonObject.class);
      String korime = jsonObject.get("korime").getAsString();
      String lozinka = jsonObject.get("lozinka").getAsString();

      var port = service.getWsKorisniciPort();
      Korisnik korisnik = port.dajKorisnika(korime, lozinka, korime);

      if (korisnik != null) {
        HttpSession session = request.getSession();
        session.setAttribute("korisnik", korisnik);

        model.put("korisnik", korisnik);
        model.put("uspjesnoPrijavljen", true);
        return Response.ok().build();
      } else {
        model.put("uspjesnoPrijavljen", false);
        return Response.status(Response.Status.UNAUTHORIZED).build();
      }
    } catch (Exception e) {
      model.put("greska", "Došlo je do pogreške prilikom prijave: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("pregled")
  @View("pogled_5_2_3.jsp")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response pregled(@QueryParam("traziImeKorisnika") String traziImeKorisnika,
      @QueryParam("traziPrezimeKorisnika") String traziPrezimeKorisnika) {
    try {
      HttpSession session = request.getSession(false);
      if (session == null) {
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity("Morate biti prijavljeni za pristup ovoj stranici.").build();
      }
      Korisnik korisnik = (Korisnik) session.getAttribute("korisnik");
      String korime = korisnik.getKorime();
      String lozinka = korisnik.getLozinka();

      var port = service.getWsKorisniciPort();
      List<Korisnik> filtriraniKorisnici =
          port.dajKorisnike(korime, lozinka, traziImeKorisnika, traziPrezimeKorisnika);
      model.put("filtriraniKorisnici", filtriraniKorisnici);
      return Response.ok().build();
    } catch (Exception e) {
      model.put("greska",
          "Došlo je do pogreške prilikom dohvaćanja filtriranih korisnika: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
