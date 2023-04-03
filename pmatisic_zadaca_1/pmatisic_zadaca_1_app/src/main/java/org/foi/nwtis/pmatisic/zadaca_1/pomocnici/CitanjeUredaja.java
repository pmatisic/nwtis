package org.foi.nwtis.pmatisic.zadaca_1.pomocnici;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.UredajVrsta;

/**
 * Klasa služi za čitanje podataka o uređaju iz csv datoteke.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class CitanjeUredaja {

  /**
   * Učitava datoteku.
   *
   * @param nazivDatoteke naziv datoteke
   * @return mapa kao skup podataka
   */
  public Map<String, Uredaj> ucitajDatoteku(String nazivDatoteke) throws IOException {
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka!");
    }

    var uredaji = new HashMap<String, Uredaj>();
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      var kolone = red.split(";");
      if (!redImaCetiriKolone(kolone)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var vrsta = UredajVrsta.odBroja(Integer.parseInt(kolone[3]));
        var u = new Uredaj(kolone[0], kolone[1], kolone[2], vrsta);
        uredaji.put(kolone[1], u);
      }
    }

    return uredaji;
  }

  /**
   * Provjerava je li red ima četiri kolone.
   *
   * @param kolone kolone u csv datoteci
   * @return istina, ako je uspješno
   */
  private boolean redImaCetiriKolone(String[] kolone) {
    return kolone.length == 4;
  }

}
