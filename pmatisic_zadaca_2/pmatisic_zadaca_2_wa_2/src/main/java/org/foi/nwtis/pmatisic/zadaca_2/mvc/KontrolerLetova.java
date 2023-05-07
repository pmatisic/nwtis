package org.foi.nwtis.pmatisic.zadaca_2.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_2.rest.RestKlijentLetova;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response spremiLet(String payload) {
    Gson gson = new Gson();
    JsonObject jsonObject = gson.fromJson(payload, JsonObject.class);
    String letJson = jsonObject.get("let").getAsString();
    LetAviona let = gson.fromJson(letJson, LetAviona.class);
    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    Response response = rcl.dodajLet(let);
    return response;
  }

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

  @GET
  @Path("spremljeni")
  @View("spremljeniLetovi.jsp")
  public void pregledSpremljenihLetova() {
    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    List<LetAvionaID> spremljeniLetovi = rcl.dohvatiSpremljeneLetove();

    model.put("spremljeniLetovi", spremljeniLetovi);
  }

  @DELETE
  @Path("{id}")
  public Response obrisiSpremljeniLet(@PathParam("id") int id) {
    RestKlijentLetova rcl = new RestKlijentLetova(konfig);
    Response response = rcl.obrisiLet(id);
    return response;
  }

}
