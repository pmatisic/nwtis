package org.foi.nwtis.pmatisic.konfiguracije;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Podklasa KonfiguracijaApstraktna i koristi serijalizaciju za spremanje i čitanje podataka iz
 * datoteke.
 *
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class KonfiguracijaBin extends KonfiguracijaApstraktna {

  /** Konstanta TIP. */
  public static final String TIP = "bin";

  /**
   *
   * @param nazivDatoteke naziv datoteke
   */
  public KonfiguracijaBin(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  /**
   * Metoda za spremanje konfiguracije. Ako je neispravan naziv datoteke izbacuje se iznimka
   * NeispravnaKonfiguracija, ako se javi problem kod spremanja izbacuje se iznimka
   * NeispravnaKonfiguracija.
   *
   * @param datoteka datoteka
   * @throws NeispravnaKonfiguracija neispravna konfiguracija
   * @see https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/ObjectOutputStream.html
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
      FileOutputStream fis = new FileOutputStream(datoteka);
      ObjectOutputStream ois = new ObjectOutputStream(fis);
      ois.writeObject(this.postavke);
      ois.close();
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
   * @see https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/io/ObjectInputStream.html
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
      FileInputStream fis = new FileInputStream(datoteka);
      ObjectInputStream ois = new ObjectInputStream(fis);
      this.postavke = (Properties) ois.readObject();
      ois.close();
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati. " + e.getMessage());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

  }

}
