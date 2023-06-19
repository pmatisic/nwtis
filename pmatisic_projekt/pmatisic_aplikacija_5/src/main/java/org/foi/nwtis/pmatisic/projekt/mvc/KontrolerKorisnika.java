package org.foi.nwtis.pmatisic.projekt.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnici;
import org.foi.nwtis.pmatisic.projekt.servis.WsKorisnici.endpoint.Korisnik;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
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

  @POST
  @Path("registracija")
  @View("pogled_5_2_1.jsp")
  public void registracija(@FormParam("ime") String ime, @FormParam("prezime") String prezime,
      @FormParam("korime") String korime, @FormParam("lozinka") String lozinka) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String imeAutora = konfiguracija.dajPostavku("autor.ime");
    String prezimeAutora = konfiguracija.dajPostavku("autor.prezime");
    String predmet = konfiguracija.dajPostavku("autor.predmet");
    String godina = konfiguracija.dajPostavku("aplikacija.godina");
    String verzija = konfiguracija.dajPostavku("aplikacija.verzija");

    var port = service.getWsKorisniciPort();

    Korisnik noviKorisnik = new Korisnik();
    noviKorisnik.setIme(ime);
    noviKorisnik.setPrezime(prezime);
    noviKorisnik.setKorime(korime);
    noviKorisnik.setLozinka(lozinka);

    boolean uspjesnoDodan = port.dodajKorisnika(noviKorisnik);

    try {
      model.put("uspjesnoDodan", uspjesnoDodan);
      model.put("ime", imeAutora);
      model.put("prezime", prezimeAutora);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @POST
  @Path("prijava")
  @View("pogled_5_2_2.jsp")
  public void prijava(@FormParam("korime") String korime, @FormParam("lozinka") String lozinka) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = konfiguracija.dajPostavku("autor.ime");
    String prezime = konfiguracija.dajPostavku("autor.prezime");
    String predmet = konfiguracija.dajPostavku("autor.predmet");
    String godina = konfiguracija.dajPostavku("aplikacija.godina");
    String verzija = konfiguracija.dajPostavku("aplikacija.verzija");

    var port = service.getWsKorisniciPort();

    try {
      Korisnik korisnik = port.dajKorisnika(korime, lozinka, korime);

      if (korisnik != null) {
        model.put("korisnik", korisnik);
        model.put("uspjesnoPrijavljen", true);
      } else {
        model.put("uspjesnoPrijavljen", false);
      }

      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);

    } catch (Exception e) {
      model.put("greska", "Došlo je do pogreške prilikom prijave: " + e.getMessage());
    }
  }

  @POST
  @Path("pregled")
  @View("pogled_5_2_3.jsp")
  public void pregled(@FormParam("korime") String korime, @FormParam("lozinka") String lozinka,
      @FormParam("traziImeKorisnika") String traziImeKorisnika,
      @FormParam("traziPrezimeKorisnika") String traziPrezimeKorisnika) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = konfiguracija.dajPostavku("autor.ime");
    String prezime = konfiguracija.dajPostavku("autor.prezime");
    String predmet = konfiguracija.dajPostavku("autor.predmet");
    String godina = konfiguracija.dajPostavku("aplikacija.godina");
    String verzija = konfiguracija.dajPostavku("aplikacija.verzija");

    var port = service.getWsKorisniciPort();

    try {
      List<Korisnik> listaKorisnika =
          port.dajKorisnike(korime, lozinka, traziImeKorisnika, traziPrezimeKorisnika);

      model.put("listaKorisnika", listaKorisnika);
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);

    } catch (Exception e) {
      model.put("greska", "Došlo je do pogreške prilikom dohvaćanja korisnika: " + e.getMessage());
    }
  }

}
