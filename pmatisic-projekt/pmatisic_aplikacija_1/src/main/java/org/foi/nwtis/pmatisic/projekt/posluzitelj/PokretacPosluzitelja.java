package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Klasa PokretacPosluzitelja koja pokreće glavni poslužitelj.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class PokretacPosluzitelja {

  /**
   * Glavna metoda koja pokreće glavni poslužitelj s konfiguracijom iz datoteke.
   *
   * @param args argumenti
   */
  public static void main(String[] args) {
    var pokretac = new PokretacPosluzitelja();
    if (!pokretac.provjeriArgumente(args)) {
      return;
    }
    try {
      var konf = pokretac.ucitajPostavke(args[0]);
      var glavniPosluzitelj = new Posluzitelj(konf);
      glavniPosluzitelj.pokreniPosluzitelja();
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Greška kod učitavanja postavki iz datoteke! " + e.getMessage());
    }
  }

  /**
   * Provjerava ispravnost unesenih argumenata.
   *
   * @param args argumenti
   * @return istina, ako je unos ispravan, false inače
   */
  private boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      var argument = args[0];
      String provjeraUnosa = "^[a-zA-Z0-9._-]+(\\.txt|\\.xml|\\.bin|\\.json|\\.yaml)$";
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

  /**
   * Učitava konfiguracijske postavke iz datoteke.
   *
   * @param nazivDatoteke naziv datoteke
   * @return konfiguracija konfiguracija
   * @throws NeispravnaKonfiguracija neispravna konfiguracija
   */
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

}
