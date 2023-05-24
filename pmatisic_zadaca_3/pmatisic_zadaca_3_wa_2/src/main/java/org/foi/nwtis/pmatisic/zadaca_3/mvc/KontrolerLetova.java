package org.foi.nwtis.pmatisic.zadaca_3.mvc;

import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.web.SakupljacJmsPoruka;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

/**
 * Klasa kontroler za upravljanje letovima u aplikaciji.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
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

  /**
   * Dohvaća poruke za prikaz u sučelju aplikacije. Koristi sakupljač JMS poruka za dohvaćanje
   * podataka.
   */
  @GET
  @Path("poruke")
  @View("poruke.jsp")
  public void getPoruke() {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    List<String> poruke = sakupljacJmsPoruka.dohvatiPoruke();

    try {
      model.put("poruke", poruke);
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
