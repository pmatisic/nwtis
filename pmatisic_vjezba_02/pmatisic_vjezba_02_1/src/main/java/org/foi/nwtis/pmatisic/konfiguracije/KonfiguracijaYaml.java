package org.foi.nwtis.pmatisic.konfiguracije;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

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
   * 
   * Reference:
   * 
   * @see https://www.geeksforgeeks.org/linkedhashmap-class-in-java/
   * @see https://stackoverflow.com/questions/12310914/how-to-iterate-through-linkedhashmap-with-lists-as-values
   * @see https://javadoc.io/doc/org.snakeyaml/snakeyaml-engine/latest/index.html
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
      Map<String, String> podaci = new LinkedHashMap<>();

      for (String key : this.postavke.stringPropertyNames()) {
        String value = this.postavke.getProperty(key);
        podaci.put(key, value);
      }

      DumpSettings settings = DumpSettings.builder().setDefaultFlowStyle(FlowStyle.BLOCK)
          .setExplicitStart(true).build();
      Dump dump = new Dump(settings);

      FileWriter fw = new FileWriter(datoteka);
      BufferedWriter bw = new BufferedWriter(fw);

      String yamlTip = dump.dumpToString((Object) this.postavke);
      bw.write(yamlTip);

      bw.close();
      fw.close();
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
   * Reference:
   * 
   * @see https://www.geeksforgeeks.org/linkedhashmap-class-in-java/
   * @see https://stackoverflow.com/questions/12310914/how-to-iterate-through-linkedhashmap-with-lists-as-values
   * @see https://javadoc.io/doc/org.snakeyaml/snakeyaml-engine/latest/index.html
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
      LoadSettings settings = LoadSettings.builder().build();
      Load load = new Load(settings);

      Map<String, String> podaci =
          (Map<String, String>) load.loadFromInputStream(Files.newInputStream(putanja));

      this.postavke.putAll(podaci);

    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati. " + e.getMessage());
    }

  }

}
