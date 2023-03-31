package org.foi.nwtis.pmatisic.zadaca_1;

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

public class MrezniRadnik extends Thread {

  protected Socket mreznaUticnica;
  protected Konfiguracija konfig;
  private int ispis = 0;
  private GlavniPosluzitelj gp;
  private Matcher m1;
  private Matcher m2;

  // konstruktor mreznog radnika
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
    this.ispis = Integer.parseInt(this.konfig.dajPostavku("ispis"));
  }

  // overloadani konstruktor mreznog radnika
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig, GlavniPosluzitelj gp) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
    this.ispis = Integer.parseInt(this.konfig.dajPostavku("ispis"));
    this.gp = gp;
  }

  // pokretanje dretve
  @Override
  public synchronized void start() {
    super.start();
  }

  // sredisnji dio
  @Override
  public void run() {
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

        if (this.ispis == 1) {
          Logger.getGlobal().log(Level.INFO, red);
        }

        poruka.append(red);
      }

      Map<String, String> obradenaPoruka = new HashMap<String, String>();
      String neobradenaPoruka = poruka.toString();
      m1 = provjeraZaGlavniKlijent(neobradenaPoruka);
      m2 = provjeraZaSimulatorMeteo(neobradenaPoruka);
      if (m1 != null) {
        obradenaPoruka = obradiKomanduZaGlavniKlijent(m1);
      } else if (m2 != null) {
        obradenaPoruka = obradiKomanduZaSimulatorMeteo(m2);
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

  // provjera kor. unosa za komande
  private Matcher provjeraZaGlavniKlijent(String s) {
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

  // provjera kor. unosa za komande
  private Matcher provjeraZaSimulatorMeteo(String s) {
    String sintaksa =
        "((KORISNIK) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (LOZINKA) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) (SENZOR) (?<senzor>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+) (?<vrijeme>(?:[1-9]|1\\d|2[0-3]):(?:[1-5]?\\d|0):(?:[1-5]?\\d|0)|0:(?:[1-5]?\\d):(?:[1-5]?\\d)) (?<temp>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?))( (?<vlaga>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?)( (?<tlak>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?))?)?";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  // obrada dobivene komande da bi se dobili podaci iz njih
  private static Map<String, String> obradiKomanduZaGlavniKlijent(Matcher m) {
    Map<String, String> grupe = new HashMap<>();
    grupe.put("KORISNIK", m.group("korisnik"));
    grupe.put("LOZINKA", m.group("lozinka"));
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

  // TODO treba testirati kad dobijem simulator meteo
  // obrada dobivene komande da bi se dobili podaci iz njih
  private static Map<String, String> obradiKomanduZaSimulatorMeteo(Matcher m) {
    Map<String, String> grupe = new HashMap<>();
    grupe.put("KORISNIK", m.group("korisnik"));
    grupe.put("LOZINKA", m.group("lozinka"));
    grupe.put("SENZOR", m.group("senzor"));
    grupe.put("vrijeme", m.group("vrijeme"));
    grupe.put("temp", m.group("temp"));
    grupe.put("vlaga", m.group("vlaga"));
    grupe.put("tlak", m.group("tlak"));

    Map<String, String> pomocnaGrupa = new HashMap<>();
    for (String key : grupe.keySet()) {
      if (grupe.get(key) != null) {
        pomocnaGrupa.put(key, grupe.get(key));
      }
    }

    return pomocnaGrupa;
  }

  // ovdje obradivam zahtjev koji dobijem kroz komandu if elif elif elif...
  private String obradiZahtjev(Map<String, String> mapa) {
    return "OK";
  }

  // prekidanje dretve
  @Override
  public void interrupt() {
    super.interrupt();
  }

}
