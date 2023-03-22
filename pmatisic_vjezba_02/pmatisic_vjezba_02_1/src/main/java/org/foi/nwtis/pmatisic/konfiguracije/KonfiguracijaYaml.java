package org.foi.nwtis.pmatisic.konfiguracije;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

/**
 * Podklasa KonfiguracijaApstraktna i koristi standardno spremanje i čitanje podataka iz datoteke uz
 * pomoć String uz pretvaranje Java objekata u YAML i obratno
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 *
 */
public class KonfiguracijaYaml extends KonfiguracijaApstraktna {

  public static final String TIP = "yaml";

  public KonfiguracijaYaml(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  /**
   * Metoda za spremanje konfiguracije. Ako je neispravan naziv datoteke izbacuje se iznimka
   * NeispravnaKonfiguracija, ako se javi problem kod spremanja izbacuje se iznimka
   * NeispravnaKonfiguracija.
   */
  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    var putanja = Path.of(datoteka);

    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);
    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije tip " + TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isWritable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće pisati.");
    }

    try {
      FileWriter fw = new FileWriter(datoteka);
      BufferedWriter bw = new BufferedWriter(fw);

      DumpSettings settings = DumpSettings.builder().build();
      Dump dump = new Dump(settings);

      String yamlTip = dump.dumpToString(this.postavke);
      bw.write(yamlTip);
      bw.close();
      fw.close();
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće upisivati." + e.getMessage());
    }

  }

  /**
   * Metoda za učitavanje konfiguracije. Ako je neispravan naziv datoteke ili ne postoji datoteka
   * izbacuje se iznimka NeispravnaKonfiguracija, ako se javi problem kod čitanja izbacuje se
   * iznimka NeispravnaKonfiguracija.
   */
  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    var datoteka = this.nazivDatoteke;
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije tip " + TIP);
    } else if (Files.exists(putanja)
        && (Files.isDirectory(putanja) || !Files.isReadable(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće čitati.");
    }
    try {
      FileReader fr = new FileReader(datoteka);
      BufferedReader br = new BufferedReader(fr);

      LoadSettings settings = LoadSettings.builder().build();
      Load load = new Load(settings);

      Properties map = (Properties) load.loadFromReader(br);
      this.postavke = map;

      br.close();
      fr.close();
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati. " + e.getMessage());
    }

  }

}
