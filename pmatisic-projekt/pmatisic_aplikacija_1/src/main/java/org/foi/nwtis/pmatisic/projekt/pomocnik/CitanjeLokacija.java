package org.foi.nwtis.pmatisic.projekt.pomocnik;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;

/**
 * Klasa služi za čitanje podataka o lokacijama iz csv datoteke.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class CitanjeLokacija {

  /**
   * Učitava datoteku.
   *
   * @param nazivDatoteke naziv datoteke
   * @return mapa kao skup podataka
   */
  public Map<String, Lokacija> ucitajDatoteku(String nazivDatoteke) throws IOException {
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka!");
    }

    var lokacije = new HashMap<String, Lokacija>();
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      var kolone = red.split(";");
      if (!redImaCetiriKolone(kolone)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var l = new Lokacija(kolone[0], kolone[1], kolone[2], kolone[3]);
        lokacije.put(kolone[1], l);
      }
    }

    return lokacije;
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
