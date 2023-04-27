package org.foi.nwtis.pmatisic.konfiguracije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Podklasa KonfiguracijaApstraktna i koristi XML format.
 *
 * @author NWTiS_4
 */
public class KonfiguracijaXml extends KonfiguracijaApstraktna {

  /** konstanta TIP. */
  public static final String TIP = "xml";

  /**
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaXml(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  /**
   * Metoda za spremanje konfiguracije. Ako je neispravan naziv datoteke izbacuje se iznimka
   * NeispravnaKonfiguracija, ako se javi problem kod spremanja izbacuje se iznimka
   * NeispravnaKonfiguracija.
   *
   * @param datoteka datoteka
   * @throws NeispravnaKonfiguracija neispravna konfiguracija
   */
  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: " + TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isWritable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće spremati.");
    }

    try {
      this.postavke.storeToXML(Files.newOutputStream(putanja), "NWTiS pmatisic 2023.");
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće pisati. " + e.getMessage());
    }

  }

  /**
   * Metoda za učitavanje konfiguracije. Ako je neispravan naziv datoteke ili ne postoji datoteka
   * izbacuje se iznimka NeispravnaKonfiguracija, ako se javi problem kod čitanja izbacuje se
   * iznimka NeispravnaKonfiguracija.
   *
   * @throws NeispravnaKonfiguracija neispravna konfiguracija
   */
  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    var datoteka = this.nazivDatoteke;
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: " + TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isReadable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće čitati.");
    }

    try {
      this.postavke.loadFromXML(Files.newInputStream(putanja));
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati. " + e.getMessage());
    }

  }

}
