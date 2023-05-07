package org.foi.nwtis.pmatisic.zadaca_2.mvc;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_2.rest.RestKlijentAerodroma;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
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

/**
 * 
 * Klasa kontroler za upravljanje aerodromima u aplikaciji.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 *
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  @Context
  private ServletContext konfig;

  @Inject
  private Models model;

  /**
   * Prikazuje početnu stranicu aplikacije s autorovim informacijama.
   */
  @GET
  @Path("pocetak")
  @View("index.jsp")
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

  /**
   * Dohvaća popis aerodroma i prikazuje ih na stranici.
   *
   * @param odBroja Broj reda od kojeg započinje prikaz aerodroma.
   */
  @GET
  @View("aerodromi.jsp")
  public void getAerodromi(@QueryParam("odBroja") Integer odBroja) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
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
    var aerodromi = rca.getAerodromi(odBroja, broj);

    try {
      model.put("aerodromi", aerodromi);
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

  /**
   * Dohvaća informacije o pojedinom aerodromu i prikazuje ih na stranici.
   *
   * @param icao ICAO kod aerodroma.
   */
  @GET
  @Path("{icao}")
  @View("aerodrom.jsp")
  public void getAerodrom(@PathParam("icao") String icao) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var aerodrom = rca.getAerodrom(icao);

    try {
      model.put("aerodrom", aerodrom);
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
   * Dohvaća udaljenosti između dva aerodroma i prikazuje ih na stranici.
   *
   * @param icaoFrom ICAO kod prvog aerodroma.
   * @param icaoTo ICAO kod drugog aerodroma.
   */
  @GET
  @Path("{icaoOd}/{icaoDo}")
  @View("udaljenostiAerodroma.jsp")
  public void getUdaljenostiAerodroma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    var udaljenostiAerodroma = rca.getUdaljenostiAerodroma(icaoFrom, icaoTo);

    try {
      model.put("udaljenostiAerodroma", udaljenostiAerodroma);
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
   * Dohvaća udaljenosti za aerodrome i prikazuje ih na stranici.
   *
   * @param icao ICAO kod aerodroma.
   * @param odBroja Broj reda od kojeg započinje prikaz udaljenosti.
   */
  @GET
  @Path("{icao}/udaljenosti")
  @View("udaljenosti.jsp")
  public void getUdaljenostiZaAerodome(@PathParam("icao") String icao,
      @QueryParam("odBroja") Integer odBroja) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
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
    var udaljenosti = rca.getUdaljenostiZaAerodome(icao, odBroja, broj);

    try {
      model.put("udaljenosti", udaljenosti);
      model.put("icao", icao);
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

  /**
   * Dohvaća najdulji put između aerodroma u državi i prikazuje ga na stranici.
   *
   * @param icao ICAO kod aerodroma.
   */
  @GET
  @Path("{icao}/najduljiPutDrzave")
  @View("najduljiPutDrzave.jsp")
  public void getNajduljiPutDrzave(@PathParam("icao") String icao) {

    RestKlijentAerodroma rca = new RestKlijentAerodroma(konfig);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();
    UdaljenostAerodromDrzava najduljiPut = rca.getNajduljiPutDrzave(icao);

    try {
      model.put("najduljiPut", najduljiPut);
      model.put("icao", icao);
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
