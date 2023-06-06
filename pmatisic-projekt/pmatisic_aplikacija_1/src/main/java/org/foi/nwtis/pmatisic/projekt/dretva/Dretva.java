package org.foi.nwtis.pmatisic.projekt.dretva;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.Posluzitelj;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.StanjePosluzitelja;

public class Dretva extends Thread {

  private Posluzitelj p;
  private Matcher m;
  private StanjePosluzitelja stanje;
  private boolean ispis;
  protected Socket mreznaUticnica;
  protected Konfiguracija konf;

  public Dretva(Socket mreznaUticnica, Konfiguracija konf, Posluzitelj p,
      StanjePosluzitelja stanje) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.p = p;
    this.stanje = stanje;
    this.ispis = stanje.dajIspisKomandi();
  }

  @Override
  public synchronized void start() {
    super.start();
  }

  @Override
  public synchronized void run() {
    try {
      var citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;
        if (this.ispis == true)
          System.out.println(red);
        poruka.append(red);
      }
      String obradenaPoruka = null;
      String neobradenaPoruka = poruka.toString();
      m = provjeriKomandu(neobradenaPoruka);
      if (m != null) {
        obradenaPoruka = obradiKomandu(m);
      } else {
        Logger.getGlobal().log(Level.SEVERE, "Greška u komandi!");
        return;
      }
      this.mreznaUticnica.shutdownInput();
      String odgovor = this.obradiZahtjev(obradenaPoruka);
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u radu dretve! " + e.getMessage());
    }
  }

  @Override
  public synchronized void interrupt() {
    super.interrupt();
  }

  private Matcher provjeriKomandu(String s) {
    String sintaksa =
        "^(?<status>STATUS)|(?<kraj>KRAJ)|(?<init>INIT)|(?<pauza>PAUZA)|(?<infoda>(INFO DA))|(?<infone>(INFO NE))|(?<udaljenost>(UDALJENOST -?\\d+\\.\\d+ -?\\d+\\.\\d+ -?\\d+\\.\\d+ -?\\d+\\.\\d+))$";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  private static String obradiKomandu(Matcher m) {
    if (m.group("status") != null) {
      return m.group("status");
    }
    if (m.group("kraj") != null) {
      return m.group("kraj");
    }
    if (m.group("init") != null) {
      return m.group("init");
    }
    if (m.group("pauza") != null) {
      return m.group("pauza");
    }
    if (m.group("infoda") != null) {
      return m.group("infoda");
    }
    if (m.group("infone") != null) {
      return m.group("infone");
    }
    if (m.group("udaljenost") != null) {
      return m.group("udaljenost");
    }
    Logger.getGlobal().log(Level.INFO, "Nijedna komanda nije pronađena u matcheru!");
    return null;
  }

  private String obradiZahtjev(String s) {
    String komanda = s;
    if (!komanda.equals("STATUS") && !komanda.equals("KRAJ") && !komanda.equals("INIT")
        && stanje.dajStatus() == Status.PAUZA) {
      return "ERROR 01 Posluzitelj je na pauzi i odbija komande osim STATUS, KRAJ i INIT.";
    }
    switch (komanda) {
      case "STATUS":
        return "OK " + stanje.dajStatus().ordinal();
      case "KRAJ":
        p.ugasi();
        return "OK";
      case "INIT":
        if (stanje.dajStatus() == Status.AKTIVAN) {
          return "ERROR 02 Posluzitelj je vec aktivan.";
        } else {
          stanje.promijeniStatus(Status.AKTIVAN);
          return "OK";
        }
      case "PAUZA":
        if (stanje.dajStatus() != Status.PAUZA) {
          stanje.promijeniStatus(Status.PAUZA);
          return "OK " + stanje.dajBrojacZahtjeva();
        } else {
          return "ERROR 01 Posluzitelj je vec na pauzi.";
        }
      case "INFO DA":
        if (this.ispis == false) {
          stanje.postaviIspisKomandi(true);
          return "OK";
        } else {
          return "ERROR 03 Ispis je vec omogucen.";
        }
      case "INFO NE":
        if (this.ispis == true) {
          stanje.postaviIspisKomandi(false);
          return "OK";
        } else {
          return "ERROR 04 Ispis je vec onemogucen.";
        }
      default:
        if (komanda.startsWith("UDALJENOST")) {
          String[] dijelovi = komanda.trim().split("\\s+");
          double gpsSirina1 = Double.parseDouble(dijelovi[1]);
          double gpsDuzina1 = Double.parseDouble(dijelovi[2]);
          double gpsSirina2 = Double.parseDouble(dijelovi[3]);
          double gpsDuzina2 = Double.parseDouble(dijelovi[4]);
          double udaljenost = izracunajUdaljenost(gpsSirina1, gpsDuzina1, gpsSirina2, gpsDuzina2);
          stanje.inkrementirajBrojacZahtjeva();
          return "OK " + String.format("%.2f", udaljenost);
        } else {
          return "ERROR 05 Nepoznata komanda.";
        }
    }
  }

  /**
   * Funkcija <i>izracunajUdaljenost</i> je temeljena na Haversineovoj formuli, koja se često
   * koristi za izračunavanje udaljenosti između dvije točke na Zemljinoj površini pomoću njihovih
   * geografskih koordinata (širina i dužina). Haversineova formula je posebno korisna za male
   * udaljenosti, gdje se uzima u obzir zakrivljenost Zemlje.
   *
   * @param lat1 Geografska širina prve točke (u stupnjevima). Geografska širina je kutna udaljenost
   *        neke točke sjeverno ili južno od ekvatora. Vrijednost varira od -90° (južni pol) do 90°
   *        (sjeverni pol).
   * @param lon1 Geografska dužina prve točke (u stupnjevima). Geografska dužina je kutna udaljenost
   *        neke točke istočno ili zapadno od početnog meridijana (Greenwich meridijan). Vrijednost
   *        varira od -180° (zapadno) do 180° (istočno).
   * @param lat2 Geografska širina druge točke (u stupnjevima).
   * @param lon2 Geografska dužina druge točke (u stupnjevima).
   * @return Funkcija vraća udaljenost između dvije točke u kilometrima kao rezultat tipa double.
   * @see - Wikipedia članak o Haversineovoj formuli:
   *      <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
   * @see - StackOverflow odgovor s implementacijom u Javi: <a href=
   *      "https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">Calculate
   *      distance between two latitude-longitude points? (Haversine formula)</a>
   */
  public static double izracunajUdaljenost(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371;
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return (R * c);
  }

}
