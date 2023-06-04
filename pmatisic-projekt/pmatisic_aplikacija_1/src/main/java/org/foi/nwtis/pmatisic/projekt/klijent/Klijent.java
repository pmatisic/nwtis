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
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Klasa klijenta.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class Klijent {

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
    var gk = new Klijent();
    Matcher unos = gk.provjeriKomandu(s);
    String komanda = "";
    if (unos == null) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u argumentima, provjerite unos!");
      return;
    } else {
      komanda = obradiKomandu(unos);
    }
    try {
      String datoteka = "postavke.txt";
      Konfiguracija konf;
      konf = KonfiguracijaApstraktna.dajKonfiguraciju(datoteka);
      String adresa = konf.dajPostavku("posluziteljGlavniAdresa").toString();
      int port = Integer.parseInt(konf.dajPostavku("posluziteljGlavniVrata"));
      gk.spojiSeNaGlavniPosluzitelj(adresa, port, komanda);
    } catch (NeispravnaKonfiguracija e) {
      e.printStackTrace();
    }
  }

  /**
   * Provjerava korisnički unos tj. argumente.
   * 
   * @param s ulazni string s argumentima
   * @return Matcher objekt ako su argumenti ispravni, inače null
   */
  private Matcher provjeriKomandu(String s) {
    String sintaksa =
        "^(?<status>STATUS)|(?<kraj>KRAJ)|(?<init>INIT)|(?<pauza>PAUZA)|(?<info>INFO)|(?<udaljenost>(UDALJENOST \\d\\d.\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d))$";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  /**
   * Obrađuje komandu u razumljiv oblik za dretvu.
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
    grupe.put("udaljenost", m.group("udaljenost"));
    String komanda = pretvoriUKomandu(grupe);
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
    String naredba = null;
    Optional<String> komanda = mapa.keySet().stream().findFirst();
    if (komanda.isPresent()) {
      switch (komanda.get()) {
        case "status":
          naredba = "STATUS";
          break;
        case "kraj":
          naredba = "KRAJ";
          break;
        case "init":
          naredba = "INIT";
          break;
        case "pauza":
          naredba = "PAUZA";
          break;
        case "info":
          naredba = "INFO";
          break;
        case "udaljenost":
          naredba = mapa.get("udaljenost");
          break;
        default:
          Logger.getGlobal().log(Level.SEVERE, "Greška u pretvaranju komande!");
      }
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
      pisac.write(komanda + "\n");
      pisac.flush();
      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;
        if (red.startsWith("OK")) {
          Logger.getGlobal().log(Level.INFO, "Odgovor od poslužitelja: " + red);
        }
        poruka.append(red);
      }
      mreznaUticnica.shutdownOutput();
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj! " + e.getMessage());
    }
  }

}
