package org.foi.nwtis.pmatisic.zadaca_3.dretve;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.AirportFacade;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.JmsPosiljatelj;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.LetoviPolasciFacade;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

/**
 * Klasa koja se koristi za sakupljanje letova aviona. Ova klasa se izvršava kao dretva.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class SakupljacLetovaAviona extends Thread {

  private boolean radi = true;
  private LocalDate trenutniDan;
  private LocalDate krajnjiDan;
  @Inject
  private ServletContext konfig;
  JmsPosiljatelj jmsPosiljatelj;
  LetoviPolasciFacade lpFacade;
  AirportFacade airportFacade;

  /**
   * Konstruktor klase SakupljacLetovaAviona.
   * 
   * @param context Kontekst servleta.
   * @param lpFacade Fasada za letove polazaka.
   * @param airportFacade Fasada za aerodrome.
   * @param jmsPosiljatelj Posiljatelj JMS poruka.
   */
  public SakupljacLetovaAviona(ServletContext context, LetoviPolasciFacade lpFacade,
      AirportFacade airportFacade, JmsPosiljatelj jmsPosiljatelj) {
    this.konfig = context;
    this.lpFacade = lpFacade;
    this.airportFacade = airportFacade;
    this.jmsPosiljatelj = jmsPosiljatelj;
  }

  /**
   * Metoda koja zaustavlja izvršavanje ove dretve.
   */
  @Override
  public void interrupt() {
    radi = false;
    super.interrupt();
  }

  /**
   * Metoda koja se izvršava kada se pokrene dretva. U ovoj metodi se obavlja sakupljanje letova
   * aviona.
   */
  @Override
  public void run() {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String korisnik = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik").toString();
    String lozinka = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka").toString();
    String aerodromiString = konfiguracija.dajPostavku("aerodromi.sakupljanje").toString();
    String[] aerodromi = aerodromiString.split(" ");
    String pocetniDanString = konfiguracija.dajPostavku("preuzimanje.od").toString();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    this.trenutniDan = LocalDate.parse(pocetniDanString, dtf);
    LocalDate zadnjiDan = lpFacade.zadnjiDatumPolaska(konfig);
    String krajnjiDanString = konfiguracija.dajPostavku("preuzimanje.do").toString();
    this.krajnjiDan = LocalDate.parse(krajnjiDanString, dtf);

    if (zadnjiDan.isEqual(trenutniDan) || zadnjiDan.isAfter(trenutniDan)) {
      trenutniDan = zadnjiDan.plusDays(1);
    }

    if (trenutniDan.isEqual(krajnjiDan) || trenutniDan.isAfter(krajnjiDan)) {
      radi = false;
      System.out.println("Dretva je prekinuta!");
    }

    while (radi) {
      int odVremena = (int) trenutniDan.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int doVremena = (int) trenutniDan.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      OSKlijent osKlijent = new OSKlijent(korisnik, lozinka);
      int brojLetova = 0;
      try {
        for (String icao : aerodromi) {
          List<LetAviona> avioniPolasci = osKlijent.getDepartures(icao, odVremena, doVremena);
          for (LetAviona let : avioniPolasci) {
            Airports aerodrom = airportFacade.find(let.getEstDepartureAirport());
            lpFacade.dodajLet(let, aerodrom);
            brojLetova++;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      String poruka = "Na dan: " + trenutniDan.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
          + " preuzeto ukupno " + brojLetova + " letova aviona.";
      jmsPosiljatelj.saljiPoruku(poruka);
      System.out.println("Poruka je poslana sa sadržajem:\n" + poruka);

      try {
        int ciklusTrajanje =
            Integer.parseInt(konfiguracija.dajPostavku("ciklus.trajanje").toString());
        Thread.sleep(ciklusTrajanje * 1000);
      } catch (InterruptedException ex) {
        break;
      }

      trenutniDan = trenutniDan.plusDays(1);
      System.out.println(trenutniDan);
      if (trenutniDan.isEqual(krajnjiDan) || trenutniDan.isAfter(krajnjiDan)) {
        radi = false;
        System.out.println("Dretva je prekinuta!");
      }
    }

  }

}
