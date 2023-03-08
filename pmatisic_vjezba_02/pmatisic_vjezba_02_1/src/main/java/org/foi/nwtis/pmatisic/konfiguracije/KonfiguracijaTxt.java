package org.foi.nwtis.pmatisic.konfiguracije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class KonfiguracijaTxt extends KonfiguracijaApstraktna {

  public static final String TIP = "txt";

  public KonfiguracijaTxt(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: ");
    } else if (Files.exists(putanja)
        && (!Files.isWritable(putanja) || Files.isDirectory(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće spremati!");
    }

    try {
      this.postavke.store(Files.newOutputStream(putanja), "NWTiS matnovak 2023.");
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće pisati." + e.getMessage());
    }
  }

  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    var datoteka = this.nazivDatoteke;
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: ");
    } else if (Files.exists(putanja)
        && (!Files.isReadable(putanja) || Files.isDirectory(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće čitati!");
    }

    try {
      this.postavke.load(Files.newInputStream(putanja));
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati." + e.getMessage());
    }
  }

}
