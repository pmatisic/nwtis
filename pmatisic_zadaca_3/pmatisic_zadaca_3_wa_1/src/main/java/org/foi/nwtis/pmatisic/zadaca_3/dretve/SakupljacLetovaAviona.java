package org.foi.nwtis.pmatisic.zadaca_3.dretve;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.JmsPosiljatelj;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.LetoviPolasciFacade;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

public class SakupljacLetovaAviona extends Thread {

  JmsPosiljatelj jmsPosiljatelj;
  private boolean radi = true;
  private LocalDate trenutniDan;

  @Inject
  LetoviPolasciFacade lpFacade;

  @Inject
  private ServletContext konfig;

  public SakupljacLetovaAviona(Konfiguracija konfig) {
    this.konfig = konfig;
    String pocetniDanString = konfig.dajPostavku("preuzimanje.od");
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    this.trenutniDan = LocalDate.parse(pocetniDanString, dtf);
    LocalDate zadnjiDan = lpFacade.zadnjiZapis().getFirstSeen(); // Ažurirajte ovo prema vašoj bazi
                                                                 // podataka
    if (zadnjiDan.isAfter(trenutniDan)) {
      trenutniDan = zadnjiDan.plusDays(1);
    }
  }

  @Override
  public void interrupt() {
    radi = false;
    super.interrupt();
  }

  @Override
  public void run() {
    while (radi) {
      String korisnik = konfig.dajPostavku("OpenSkyNetwork.korisnik");
      String lozinka = konfig.dajPostavku("OpenSkyNetwork.lozinka");
      String icao = konfig.dajPostavku("aerodromi.sakupljanje");

      int odVremena = (int) trenutniDan.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int doVremena = (int) trenutniDan.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC);

      OSKlijent osKlijent = new OSKlijent(korisnik, lozinka);
      try {
        List<LetAviona> avioniPolasci = osKlijent.getDepartures(icao, odVremena, doVremena);
        for (LetAviona let : avioniPolasci) {
          lpFacade.dodajLet(let);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        int ciklusTrajanje = Integer.parseInt(konfig.dajPostavku("ciklus.trajanje"));
        Thread.sleep(ciklusTrajanje * 1000);
      } catch (InterruptedException ex) {
        break;
      }

      trenutniDan = trenutniDan.plusDays(1);
    }
  }
}
