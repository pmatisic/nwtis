package org.foi.nwtis.pmatisic.vjezba_02;

import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class Vjezba_02_3 {

  public Vjezba_02_3() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) throws NeispravnaKonfiguracija {
    Konfiguracija konf = null;
    switch (args.length) {
      case 1:
        System.out.println("Ispis svih postavki");
        konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
        Properties postavke = konf.dajSvePostavke();
        for (Object o : postavke.keySet()) {
          String k = (String) o;
          String v = konf.dajPostavku(k);
          System.out.println(k + "=> " + v);
        }
        break;
      case 2:
        System.out.println("Ispis jedne postavke");
        konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
        String v = konf.dajPostavku(args[1]);
        System.out.println(args[0] + "=> " + v);
        break;
      case 3:
        System.out.println("Spremi postavku");
        konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
        String k = args[1];
        String v2 = args[2];
        konf.spremiPostavku(k, v2);
        konf.spremiKonfiguraciju();
        break;
      default:
        System.out.println("Neispravni unos!");

        break;
    }

  }

}