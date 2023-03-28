package org.foi.nwtis.pmatisic.zadaca_1;

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

    // String[] adresaPortKomanda = regexProvjera(s);

    var gk = new GlavniKlijent();
    Matcher unos = gk.provjeriArgumente(s);

    String komanda;
    if (unos == null) {
      Logger.getGlobal().log(Level.SEVERE, "Nisu unešeni argumenti!");
      return;
    } else {
      komanda = obradiKomandu(unos);
    }
    gk.spojiSeNaPosluzitelj(unos.group("ADRESA"), Integer.parseInt(unos.group("MREZNAVRATA")),
        komanda);
  }

  private Matcher provjeriArgumente(String s) {
    String sintaksa = "nesto";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(sintaksa);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  private String obradiKomandu(String group) {
    // TODO Auto-generated method stub
    return null;
  }

  private static String napraviKomandu(Matcher podudarac) {
    String kljucevi[] = new String[] {"METEO", "ALARM", "UDALJENOST", "UDALJENOSTSPREMI", "MAKSTEMP", "MAKSVLAGA", "MAKSTLAK", "KRAJ"};
    
    StringBuilder sb = new StringBuilder();
    sb.append("KORISNIK ").append(matcher)
    
  }

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

        // Logger.getGlobal().log(Level.INFO, red);
        poruka.append(red);
      }
      Logger.getGlobal().log(Level.INFO, poruka.toString());
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
