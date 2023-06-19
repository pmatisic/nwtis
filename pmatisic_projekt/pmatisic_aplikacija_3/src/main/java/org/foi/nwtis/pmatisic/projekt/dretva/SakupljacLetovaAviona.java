package org.foi.nwtis.pmatisic.projekt.dretva;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.entitet.AerodromiLetovi;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.zrno.AerodromiLetoviFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.JmsPosiljatelj;
import org.foi.nwtis.pmatisic.projekt.zrno.LetoviPolasciFacade;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

public class SakupljacLetovaAviona extends Thread {

  private boolean radi = true;
  private LocalDate trenutniDan;
  private LocalDate krajnjiDan;
  @Inject
  private ServletContext konfig;
  JmsPosiljatelj jmsPosiljatelj;
  LetoviPolasciFacade lpFacade;
  AirportFacade airportFacade;
  AerodromiLetoviFacade alFacade;

  public SakupljacLetovaAviona(ServletContext context, LetoviPolasciFacade lpFacade,
      AerodromiLetoviFacade alFacade, AirportFacade airportFacade, JmsPosiljatelj jmsPosiljatelj) {
    this.konfig = context;
    this.lpFacade = lpFacade;
    this.alFacade = alFacade;
    this.airportFacade = airportFacade;
    this.jmsPosiljatelj = jmsPosiljatelj;
  }

  @Override
  public void interrupt() {
    radi = false;
    super.interrupt();
  }

  @Override
  public void run() {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String korisnik = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik").toString();
    // String ldap = konfiguracija.dajPostavku("aai.ldap").toString();
    String lozinka = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka").toString();
    String pocetniDanString = konfiguracija.dajPostavku("preuzimanje.od").toString();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    this.trenutniDan = LocalDate.parse(pocetniDanString, dtf);
    LocalDate zadnjiDan = lpFacade.zadnjiDatumPolaska(konfig);
    String krajnjiDanString = konfiguracija.dajPostavku("preuzimanje.do").toString();
    this.krajnjiDan = LocalDate.parse(krajnjiDanString, dtf);
    int ciklusTrajanje = Integer.parseInt(konfiguracija.dajPostavku("ciklus.trajanje")) * 1000;

    if (zadnjiDan.isEqual(trenutniDan) || zadnjiDan.isAfter(trenutniDan)) {
      trenutniDan = zadnjiDan.plusDays(1);
    }

    if (trenutniDan.isEqual(krajnjiDan) || trenutniDan.isAfter(krajnjiDan)) {
      radi = false;
      System.out.println("Dretva je prekinuta!");
    }

    while (radi) {
      long pocetakCiklusa = System.currentTimeMillis();
      // ZoneId zoneId = ZoneId.of("CET");
      // ZoneId zoneId = ZoneId.systemDefault();
      // int odVremena = (int) trenutniDan.atStartOfDay(zoneId).toEpochSecond();
      int odVremena = (int) trenutniDan.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int doVremena = (int) trenutniDan.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      // int doVremena = (int) trenutniDan.plusDays(1).atStartOfDay().minusSeconds(1).toEpochSecond(ZoneOffset.UTC);
      List<AerodromiLetovi> aktivniAerodromi = alFacade.dohvatiAktivneAerodrome();
      OSKlijent osKlijent = new OSKlijent(korisnik, lozinka);
      // OSKlijentBP osKlijent = new OSKlijentBP(ldap, korisnik);
      int brojLetova = 0;
      try {
        for (AerodromiLetovi aerodromLet : aktivniAerodromi) {
          String icao = aerodromLet.getIcao();
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

      long krajRadnogDijela = System.currentTimeMillis();
      long trajanjeRadnogDijela = krajRadnogDijela - pocetakCiklusa;
      long vrijemeSpavanja = ciklusTrajanje - trajanjeRadnogDijela;

      if (vrijemeSpavanja > 0) {
        try {
          Thread.sleep(vrijemeSpavanja);
        } catch (InterruptedException ex) {
          break;
        }
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
