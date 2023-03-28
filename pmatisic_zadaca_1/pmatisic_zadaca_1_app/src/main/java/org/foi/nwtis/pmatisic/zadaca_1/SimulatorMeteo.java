package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.MeteoSimulacija;

public class SimulatorMeteo {

  public static void main(String[] args) {
    var sm = new SimulatorMeteo();
    if (!sm.provjeriArgumente(args)) {
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
          "Nije upisan naziv datoteke!");
      return;
    }

    try {
      var konf = sm.ucitajPostavke(args[0]);
      sm.pokreniSimulator(konf);
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
          "Pogreška kod učitavanja postavki iz datoteke!" + e.getMessage());
    } catch (IOException e) {
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
          "Pogreška kod učitavanja meteo podatka iz datoteke!" + e.getMessage());
    }
  }

  private boolean provjeriArgumente(String[] args) {
    return args.length == 1 ? true : false;
  }

  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  private void pokreniSimulator(Konfiguracija konf) throws IOException {
    var nazivDatoteke = konf.dajPostavku("datotekaMeteo");
    var putanja = Path.of(nazivDatoteke);
    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka!");
    }
    var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

    int brojac = 0;
    MeteoSimulacija prethodniMeteo = null;
    while (true) {
      var red = citac.readLine();
      if (red == null)
        break;

      brojac++;
      if (jestZaglavlje(brojac))
        continue;

      var kolone = red.split(";");
      if (!redImaPetKolona(kolone)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var vazeciMeteo = new MeteoSimulacija(kolone[0], kolone[1], Float.parseFloat(kolone[2]),
            Float.parseFloat(kolone[3]), Float.parseFloat(kolone[4]));

        this.posaljiMeteoPodatak(vazeciMeteo);
        if (!jestPrviMeteoPodatak(brojac)) {
          this.izracunajObradiSpavanje(prethodniMeteo, vazeciMeteo);
        }
        prethodniMeteo = vazeciMeteo;
      }
    }
  }

  private boolean jestPrviMeteoPodatak(int brojac) {
    return brojac == 2;
  }

  private boolean redImaPetKolona(String[] kolone) {
    return kolone.length == 5;
  }

  private void posaljiMeteoPodatak(MeteoSimulacija vazeciMeteo) {
    // TODO ovdje isto kao i kod glavnogKlijenta šalješ podatke na
    // GlavniPoslužitelj
  }

  private boolean jestZaglavlje(int brojac) {
    return brojac == 1;
  }

  private void izracunajObradiSpavanje(MeteoSimulacija prethodniMeteo,
      MeteoSimulacija vazeciMeteo) {
    String prvi = prethodniMeteo.vrijeme();
    String drugi = vazeciMeteo.vrijeme();
    // TODO pretvori string u milisekunde
    int pocetak = 10;
    int kraj = 20;
    int spavaj = kraj - pocetak;
    // TODO korigiraj spavanje temeljem podatka trajanjeSekunde
    try {
      Thread.sleep(spavaj);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
