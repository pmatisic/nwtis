package org.foi.nwtis.pmatisic.vjezba_02;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class Vjezba_02_3 {

  public Vjezba_02_3() {}

  public static void main(String[] args) {
    try {
      Konfiguracija konf = null;
      switch (args.length) {
        case 1:
          Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.INFO, "Ispis svih postavki.");

          konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);

          Properties postavke = konf.dajSvePostavke();
          for (Object o : postavke.keySet()) {
            String k = (String) o;
            String v = konf.dajPostavku(k);
            Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.INFO, k + "=> " + v);
          }
          break;
        case 2:
          Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.INFO, "Ispis jedne postavke.");
          konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
          String v = konf.dajPostavku(args[1]);
          Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.INFO, args[0] + "=> " + v);
          break;
        case 3:
          Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.INFO, "Dodavanje nove postavke.");
          konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
          String k = args[1];
          String v2 = args[2];
          konf.spremiPostavku(k, v2);
          konf.spremiKonfiguraciju();
          break;
        default:
          Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.SEVERE, "Neispravni unos!");
          break;
      }
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(Vjezba_02_3.class.getName()).log(Level.SEVERE, e.getMessage());
      // e.printStackTrace();
    }
  }

}
