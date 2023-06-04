package org.foi.nwtis.pmatisic.projekt.klijent;

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

/**
 * Klasa za obradu komandi glavnog klijenta.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class GlavniKlijent {

  /**
   * Main metoda koja pokreće klijenta.
   * 
   * @param args argumenti potrebni za pokretanje klijenta
   */
  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < args.length; i++) {
      if (args[i].contains(" ")) {
        String trenutni = args[i];
        args[i] = "'" + trenutni + "'";
      }
      sb.append(args[i]).append(" ");
    }

    String s = sb.toString().trim();
    var gk = new GlavniKlijent();
    Matcher unos = gk.provjeriArgumente(s);
    String komanda = "";

    if (unos == null) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u argumentima, provjerite unos!");
      return;
    } else {
      komanda = obradiKomandu(unos);
    }

    gk.spojiSeNaGlavniPosluzitelj(unos.group("adresa"), Integer.parseInt(unos.group("port")),
        komanda);
  }

  /**
   * Provjerava korisnički unos tj. argumente.
   * 
   * @param s ulazni string s argumentima
   * @return Matcher objekt ako su argumenti ispravni, inače null
   */
  private Matcher provjeriArgumente(String s) {
    String sintaksa =
        "(-k) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (-l) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) (-a) (?<adresa>[0-9a-z.]+) (-v) (?<port>[0-9]{4}) (-t) (?<vrijeme>[0-9]+) ((((--meteo) (?<meteo>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((--makstemp) (?<makstemp>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((--maksvlaga) (?<maksvlaga>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((--makstlak) (?<makstlak>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((--alarm) (?<alarm>[0-9a-zA-Z' ]+))|((--udaljenost) (?<udaljenostnavodnici>'[0-9a-zA-Z ]+' '[0-9a-zA-Z ]+'))|((--udaljenost) (?<udaljenostspremi>spremi))|(?<kraj>--kraj)))";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  /**
   * Obrađuje komandu u razumljiv oblik za mrežnog radnika.
   * 
   * @param m Matcher objekt s grupama iz ulaznih argumenata
   * @return komanda u string formatu
   */
  private static String obradiKomandu(Matcher m) {
    Map<String, String> grupe = new HashMap<>();
    grupe.put("status", "STATUS");
    grupe.put("kraj", "KRAJ");
    grupe.put("init", "INIT");
    grupe.put("pauza", "PAUZA");
    grupe.put("info", "INFO");
    grupe.put("udaljenostnavodnici", "--udaljenost");
    grupe.put("udaljenostspremi", "--udaljenost");
    grupe.put("kraj", "--kraj");

    Map<String, String> pomocnaGrupa = new HashMap<>();
    for (String key : grupe.keySet()) {
      if (m.group(key) != null) {
        if (key == "udaljenostnavodnici" || key == "udaljenostspremi") {
          pomocnaGrupa.put("UDALJENOST", m.group(key).toUpperCase());
        } else {
          pomocnaGrupa.put(key.toUpperCase(), m.group(key));
        }
      }
    }

    String komanda = pretvoriUKomandu(pomocnaGrupa);
    return komanda;
  }

  /**
   * Proširenje metode <i>obradiKomandu</i> na način da se dobije cjelovita naredba.
   * 
   * @param mapa Mapa s ključem koji predstavlja tip komande i vrijednosti koje su povezane s tim
   *        ključem
   * @return komanda u string formatu
   */
  private static String pretvoriUKomandu(Map<String, String> mapa) {
    String naredba = "KORISNIK " + mapa.get("KORISNIK") + " LOZINKA " + mapa.get("LOZINKA");

    if (mapa.containsKey("STATUS")) {
      naredba += " STATUS";
    } else if (mapa.containsKey("KRAJ")) {
      naredba += " KRAJ";
    } else if (mapa.containsKey("INIT")) {
      naredba += " INIT";
    } else if (mapa.containsKey("PAUZA")) {
      naredba += " PAUZA";
    } else if (mapa.containsKey("INFO")) {
      naredba += " INFO " + mapa.get("INFO");
    } else if (mapa.containsKey("UDALJENOST")) {
      naredba += " UDALJENOST " + mapa.get("UDALJENOST");
    } else if (mapa.containsKey("KRAJ")) {
      naredba += " KRAJ";
    }

    return naredba;
  }

  /**
   * Spaja se na poslužitelj i šalje komandu.
   * 
   * @param adresa IP adresa ili domensko ime poslužitelja
   * @param mreznaVrata broj mrežnih vrata na kojima poslužitelj sluša
   * @param komanda komanda koja se šalje poslužitelju
   */
  private void spojiSeNaGlavniPosluzitelj(String adresa, int mreznaVrata, String komanda) {
    try {
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

      pisac.write(komanda);
      pisac.flush();
      mreznaUticnica.shutdownOutput();

      var poruka = new StringBuilder();

      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;

        if (red.startsWith("OK")) {
          Logger.getGlobal().log(Level.INFO, "Odgovor od poslužitelja: " + red);
        } else {
          Logger.getGlobal().log(Level.SEVERE, "Greška u odgovoru poslužitelja: " + red);
        }

        poruka.append(red);
      }

      Logger.getGlobal().log(Level.INFO, poruka.toString());
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj! " + e.getMessage());
    }
  }

}
