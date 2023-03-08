package org.foi.nwtis.pmatisic.vjezba_02;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class Vjezba_02_2 {

  public Vjezba_02_2() {
    // TODO Auto-generated constructor stub
  }

  public static void main(String[] args) {
    if (args.length != 0) {
      try {
        KonfiguracijaApstraktna.kreirajKonfiguraciju(args[0]);
      } catch (NeispravnaKonfiguracija e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Nema argumenata!");
    }
  }

}
