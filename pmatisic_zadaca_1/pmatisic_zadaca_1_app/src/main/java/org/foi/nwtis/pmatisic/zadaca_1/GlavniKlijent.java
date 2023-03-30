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

public class GlavniKlijent {

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

    System.out.println(s);

    var gk = new GlavniKlijent();
    Matcher unos = gk.provjeriArgumente(s);

    String komanda;
    if (unos == null) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u argumentima, provjerite unos!");
      return;
    } else {
      komanda = obradiKomandu(unos);
    }
    gk.spojiSeNaPosluzitelj(unos.group("adresa"), Integer.parseInt(unos.group("port")), komanda);
  }

  // provjera kor. unosa za argument
  private Matcher provjeriArgumente(String s) {
    String sintaksa =
        "(-k) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (-l) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) (-a) (?<adresa>[0-9a-z.]+) (-v) (?<port>[0-9]{4}) (-t) (?<vrijeme>[0-9]+) ((((--meteo) (?<meteo>[0-9a-zA-Z-]+))|((--makstemp) (?<makstemp>[0-9a-zA-Z-]+))|((--maksvlaga) (?<maksvlaga>[0-9a-zA-Z-]+))|((--makstlak) (?<makstlak>[0-9a-zA-Z-]+))|((--alarm) (?<alarm>[0-9a-zA-Z' ]+))|((--udaljenost) (?<udaljenostnavodnici>'[0-9a-zA-Z ]+' '[0-9a-zA-Z ]+'))|((--udaljenost) (?<udaljenostspremi>spremi))|(?<kraj>--kraj)))";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  // obrada dobivene komande u razumljiv oblik
  private static String obradiKomandu(Matcher m) {
    Map<String, String> grupe = new HashMap<>();
    grupe.put("korisnik", "-k");
    grupe.put("lozinka", "-l");
    grupe.put("adresa", "-a");
    grupe.put("port", "-v");
    grupe.put("vrijeme", "-t ");
    grupe.put("meteo", "--meteo");
    grupe.put("makstemp", "--makstemp");
    grupe.put("maksvlaga", "--maksvlaga");
    grupe.put("makstlak", "--makstlak");
    grupe.put("alarm", "--alarm");
    grupe.put("udaljenostnavodnici", "--udaljenost");
    grupe.put("udaljenostspremi", "--udaljenost");
    grupe.put("kraj", "--kraj");

    Map<String, String> pomocnaGrupa = new HashMap<>();
    for (String key : grupe.keySet()) {
      if (m.group(key) != null) {
        if (key == "udaljenostnavodnici" || key == "udaljenostspremi") {
          pomocnaGrupa.put("UDALJENOST", m.group(key));
        } else {
          pomocnaGrupa.put(key.toUpperCase(), m.group(key));
        }
      }
    }

    System.out.println(pomocnaGrupa.toString());

    String komanda = pretvoriUKomandu(pomocnaGrupa);

    return komanda;
  }

  // prosirenje prethodne metode na nacin da se dobije cjelovita naredba
  private static String pretvoriUKomandu(Map<String, String> mapa) {

    String naredba = "KORISNIK " + mapa.get("KORISNIK") + " LOZINKA " + mapa.get("LOZINKA");

    if (mapa.containsKey("UDALJENOST")) {
      naredba += " UDALJENOST " + mapa.get("UDALJENOST");
    } else if (mapa.containsKey("METEO")) {
      naredba += " METEO " + mapa.get("METEO");
    } else if (mapa.containsKey("MAKSTEMP")) {
      naredba += " MAKSTEMP " + mapa.get("MAKSTEMP");
    } else if (mapa.containsKey("MAKSVLAGA")) {
      naredba += " MAKSVLAGA " + mapa.get("MAKSVLAGA");
    } else if (mapa.containsKey("MAKSTLAK")) {
      naredba += " MAKSTLAK " + mapa.get("MAKSTLAK");
    } else if (mapa.containsKey("ALARM")) {
      naredba += " ALARM " + mapa.get("ALARM");
    } else if (mapa.containsKey("UDALJENOST")) {
      naredba += " UDALJENOST " + mapa.get("UDALJENOST");
    } else if (mapa.containsKey("KRAJ")) {
      naredba += " KRAJ";
    }

    System.out.println(naredba);

    return naredba;
  }

  // metoda za spajanje na posluzitelj
  private void spojiSeNaPosluzitelj(String adresa, int mreznaVrata, String komanda) {
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

        poruka.append(red);
      }
      Logger.getGlobal().log(Level.INFO, poruka.toString());
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj!");
    }
  }

}
