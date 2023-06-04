package org.foi.nwtis.pmatisic.projekt.dretva;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.Posluzitelj;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.StanjePosluzitelja;

public class Dretva extends Thread {

  private Posluzitelj gp;
  private Matcher m;
  private StanjePosluzitelja stanjePosluzitelja;
  private int ispis = 0;
  protected Socket mreznaUticnica;
  protected Konfiguracija konf;

  public Dretva(Socket mreznaUticnica, Konfiguracija konf, Posluzitelj gp,
      StanjePosluzitelja stanjePosluzitelja) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.gp = gp;
    this.stanjePosluzitelja = stanjePosluzitelja;
    this.ispis = Integer.parseInt(this.konf.dajPostavku("ispis"));
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
        if (this.ispis == 1)
          Logger.getGlobal().log(Level.INFO, red);
        poruka.append(red);
      }
      Map<String, String> obradenaPoruka = new HashMap<String, String>();
      String neobradenaPoruka = poruka.toString();
      m = provjeriKomandu(neobradenaPoruka);
      if (m != null) {
        obradenaPoruka = obradiKomandu(m);
      } else {
        Logger.getGlobal().log(Level.SEVERE, "Greška u komandi!");
        return;
      }
      String odgovor = this.obradiZahtjev(obradenaPoruka);
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownInput();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u radu dretve! " + e.getMessage());
    }
  }

  @Override
  public synchronized void interrupt() {
    Logger.getLogger(Dretva.class.getName()).info("Dretva se gasi!");
    super.interrupt();
  }

  private Matcher provjeriKomandu(String s) {
    String sintaksa =
        "(KORISNIK) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (LOZINKA) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) ((((METEO) (?<meteo>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS TEMP) (?<makstemp>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS VLAGA) (?<maksvlaga>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS TLAK) (?<makstlak>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((ALARM) (?<alarm>[0-9a-zA-Z' ]+))|((UDALJENOST) (?<udaljenostnavodnici>'[0-9a-zA-Z ]+' '[0-9a-zA-Z ]+'))|((UDALJENOST) (?<udaljenostspremi>SPREMI))|(?<kraj>KRAJ)))";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  private static Map<String, String> obradiKomandu(Matcher m) {
    Map<String, String> grupe = new HashMap<>();
    grupe.put("METEO", m.group("meteo"));
    grupe.put("MAKS TEMP", m.group("makstemp"));
    grupe.put("MAKS VLAGA", m.group("maksvlaga"));
    grupe.put("MAKS TLAK", m.group("makstlak"));
    grupe.put("ALARM", m.group("alarm"));
    grupe.put("UDALJENOST", m.group("udaljenostnavodnici"));
    grupe.put("UDALJENOST SPREMI", m.group("udaljenostspremi"));
    grupe.put("KRAJ", m.group("kraj"));
    Map<String, String> pomocnaGrupa = new HashMap<>();
    for (String key : grupe.keySet()) {
      if (grupe.get(key) != null) {
        if (key == "UDALJENOST" || key == "UDALJENOST SPREMI") {
          pomocnaGrupa.put("UDALJENOST", grupe.get(key));
        } else {
          pomocnaGrupa.put(key, grupe.get(key));
        }
      }
    }
    return pomocnaGrupa;
  }

  private String obradiZahtjev(Map<String, String> mapa) {
    String komanda = izvuciKomandu(mapa);
    switch (komanda) {
      case "STATUS":
        return "OK " + stanjePosluzitelja.dajStatus().ordinal();
      case "KRAJ":
        gp.kraj = true;
        for (Thread t : Thread.getAllStackTraces().keySet()) {
          if (t instanceof Dretva && t.isAlive()) {
            t.interrupt();
          }
        }
        return "OK";
      case "INIT":
        stanjePosluzitelja.promijeniStatus(Status.AKTIVAN);
        return "OK";
      case "PAUZA":
        if (stanjePosluzitelja.dajStatus() != Status.PAUZA) {
          stanjePosluzitelja.promijeniStatus(Status.PAUZA);
          return "OK " + stanjePosluzitelja.dajBrojacZahtjeva();
        } else {
          return "ERROR 01 Posluzitelj je vec na pauzi.";
        }
      case "INFO":
        if (mapa.get("INFO").equals("DA")) {
          stanjePosluzitelja.postaviIspisKomandi(true);
          if (this.ispis == 0) {
            this.ispis = 1;
            return "OK";
          } else {
            return "ERROR 03 Ispis je vec omogucen.";
          }
        } else if (mapa.get("INFO").equals("NE")) {
          stanjePosluzitelja.postaviIspisKomandi(false);
          if (this.ispis == 1) {
            this.ispis = 0;
            return "OK";
          } else {
            return "ERROR 04 Ispis je vec onemogucen.";
          }
        }
        return "ERROR 05 Nepoznata INFO komanda.";
      case "UDALJENOST":
        if (stanjePosluzitelja.dajStatus() == Status.AKTIVAN) {
          double gpsSirina1 = Double.parseDouble(mapa.get("gpsSirina1"));
          double gpsDuzina1 = Double.parseDouble(mapa.get("gpsDuzina1"));
          double gpsSirina2 = Double.parseDouble(mapa.get("gpsSirina2"));
          double gpsDuzina2 = Double.parseDouble(mapa.get("gpsDuzina2"));
          double udaljenost = izracunajUdaljenost(gpsSirina1, gpsDuzina1, gpsSirina2, gpsDuzina2);
          stanjePosluzitelja.inkrementirajBrojacZahtjeva();
          return "OK " + udaljenost;
        } else {
          return "ERROR 02 Posluzitelj je na pauzi.";
        }
      default:
        return "ERROR 05 Nepoznata komanda.";
    }
  }

  private String izvuciKomandu(Map<String, String> mapa) {
    if (mapa.containsKey("STATUS"))
      return "STATUS";
    if (mapa.containsKey("KRAJ"))
      return "KRAJ";
    if (mapa.containsKey("INIT"))
      return "INIT";
    if (mapa.containsKey("PAUZA"))
      return "PAUZA";
    if (mapa.containsKey("INFO"))
      return "INFO";
    if (mapa.containsKey("UDALJENOST"))
      return "UDALJENOST";
    return "";
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
