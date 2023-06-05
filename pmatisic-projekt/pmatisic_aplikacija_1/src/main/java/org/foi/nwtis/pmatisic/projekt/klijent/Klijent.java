package org.foi.nwtis.pmatisic.projekt.klijent;

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
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class Klijent {

  public static void main(String[] args) {

    if (args.length < 1) {
      Logger.getGlobal().log(Level.SEVERE,
          "Morate navesti putanju do datoteke s postavkama kao prvi argument!");
      return;
    }

    String datoteka = args[0];
    String komanda = null;
    Klijent k = new Klijent();
    StringBuilder sb = new StringBuilder();

    for (int i = 1; i < args.length; i++) {
      if (args[i].contains(" ")) {
        String trenutni = args[i];
        args[i] = "'" + trenutni + "'";
      }
      sb.append(args[i]).append(" ");
    }

    String s = sb.toString().trim();
    Matcher unos = k.provjeriKomandu(s);

    if (unos == null) {
      Logger.getGlobal().log(Level.SEVERE,
          "Greška u argumentima ili fali argument, provjerite unos!");
      return;
    } else {
      komanda = obradiKomandu(unos);
    }

    try {
      var konf = k.ucitajPostavke(datoteka);
      var adresa = (konf.dajPostavku("posluziteljAdresa")).toString();
      var port = Integer.parseInt(konf.dajPostavku("posluziteljVrata"));
      var cekanje = Integer.parseInt(konf.dajPostavku("maksCekanje"));
      k.spojiSeNaPosluzitelj(adresa, port, cekanje, komanda);
    } catch (NeispravnaKonfiguracija e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u dohvaćanju postavki!");
    }

  }

  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  private Matcher provjeriKomandu(String s) {
    String sintaksa =
        "^(?<status>STATUS)|(?<kraj>KRAJ)|(?<init>INIT)|(?<pauza>PAUZA)|(?<infoda>(INFO DA))|(?<infone>(INFO NE))|(?<udaljenost>(UDALJENOST \\d\\d.\\d\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d\\d \\d\\d.\\d\\d\\d\\d\\d))$";
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
    Logger.getGlobal().log(Level.SEVERE, "Nijedna komanda nije pronađena u matcheru!");
    return null;
  }

  private void spojiSeNaPosluzitelj(String adresa, int mreznaVrata, int cekanje, String komanda) {
    try {
      var mreznaUticnica = new Socket(adresa, mreznaVrata);
      mreznaUticnica.setSoTimeout(cekanje);
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      var poruka = new StringBuilder();
      pisac.write(komanda);
      pisac.flush();
      mreznaUticnica.shutdownOutput();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;
        Logger.getGlobal().log(Level.INFO, "Odgovor od poslužitelja: " + red);
        poruka.append(red);
      }
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj! " + e.getMessage());
    }
  }

}
