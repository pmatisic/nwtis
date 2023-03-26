package org.foi.nwtis.pmatisic.zadaca_1.pomocnici;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Korisnik;

// TODO napravit dokumentaciju

public class CitanjeKorisnika {

  public Map<String, Korisnik> ucitajDatoteku(String nazivDatoteke) throws IOException {
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka!");
    }

    var korisnici = new HashMap<String, Korisnik>();
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      var kolone = red.split(";");
      if (!redImaPetKolona(kolone)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var admin = jestAdministrator(kolone[4]);
        var k = new Korisnik(kolone[0], kolone[1], kolone[2], kolone[3], admin);
        korisnici.put(kolone[2], k);
      }
    }

    return korisnici;
  }

  private boolean jestAdministrator(String kolona) {
    return kolona.compareTo("1") == 0 ? true : false;
  }

  private boolean redImaPetKolona(String[] kolone) {
    return kolone.length == 5;
  }

}
