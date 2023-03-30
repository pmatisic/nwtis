package org.foi.nwtis.pmatisic.zadaca_1;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class PokretacPosluzitelja {

  public static void main(String[] args) {
    var pokretac = new PokretacPosluzitelja();
    if (!pokretac.provjeriArgumente(args)) {
      return;
    }

    try {
      var konf = pokretac.ucitajPostavke(args[0]);
      var glavniPosluzitelj = new GlavniPosluzitelj(konf);
      glavniPosluzitelj.pokreniPosluzitelja();
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Greška kod učitavanja postavki iz datoteke! " + e.getMessage());
    }
  }

  // provjera argumenata za GP
  private boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      var argument = args[0];
      String provjeraUnosa = "NWTiS_[a-zA-Z0-9.]{1,255}_3.(txt|xml|bin|json|yaml)";
      Pattern uzorak = Pattern.compile(provjeraUnosa);
      Matcher m = uzorak.matcher(argument);
      boolean status = m.matches();
      if (status == false) {
        Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
            "Greška pri unosu argumenta!");
      }
      return status;
    } else {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Nije unešen argument!");
      return false;
    }
  }

  // ucitavanje postavki iz datoteke konfiguracije
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

}
