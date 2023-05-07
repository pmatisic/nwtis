package org.foi.nwtis.pmatisic.zadaca_2.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_2.rest.RestKlijentLetova;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import com.google.gson.Gson;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 
 * Klasa kontroler za upravljanje letovima. Omogućuje prikaz i upravljanje letovima kroz različite
 * HTTP metode.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 *
 */
@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetova {

  @Context
  private ServletContext konfig;

  @Inject
  private Models model;

  /**
   * Prikazuje početnu stranicu.
   */
  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  /**
   * Prikazuje letove za zadani ICAO kod i dan. Prikazuje letove u paginiranoj formi.
   * 
   * @param icao ICAO kod aerodroma
   * @param dan Datum u formatu "yyyy-MM-dd"
   * @param odBroja Broj stranice za paginaciju
   */
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

  /**
   * Sprema let u bazu podataka.
   * 
   * @param letJson JSON reprezentacija leta
   * @return HTTP Response objekt
   */
  @POST
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public Response spremiLet(@FormParam("let") String letJson) {
    Gson gson = new Gson();
    LetAviona let = gson.fromJson(letJson, LetAviona.class);
    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    rcl.dodajLet(let);
    return Response.status(Response.Status.CREATED).build();
  }

  /**
   * Prikazuje letove između dva aerodroma za zadani dan.
   * 
   * @param icaoOd ICAO kod polaznog aerodroma
   * @param icaoDo ICAO kod odredišnog aerodroma
   * @param dan Datum u formatu "yyyy-MM-dd"
   */
  @GET
  @Path("{icaoOd}/{icaoDo}")
  @View("letoviAerodroma.jsp")
  public void getLetoviDvaAerodroma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo, @QueryParam("dan") String dan) {

    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    List<LetAviona> letovi = rcl.getLetoviDvaAerodroma(icaoOd, icaoDo, dan);
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    try {
      model.put("letovi", letovi);
      model.put("icaoOd", icaoOd);
      model.put("icaoDo", icaoDo);
      model.put("dan", dan);
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Prikazuje sve spremljene letove.
   */
  @GET
  @Path("spremljeni")
  @View("spremljeniLetovi.jsp")
  public void getSpremljeniLetovi() {

    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    List<LetAvionaID> spremljeniLetovi = rcl.dohvatiSpremljeneLetove();
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    try {
      model.put("spremljeniLetovi", spremljeniLetovi);
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Briše spremljeni let prema ID-u.
   * 
   * @param id ID spremljenog letaa
   * @return HTTP Response objekt
   */
  @DELETE
  @Path("{id}")
  public Response obrisiSpremljeniLet(@PathParam("id") int id) {
    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    rcl.obrisiLet(id);
    return Response.status(Response.Status.NO_CONTENT).build();
  }

}
