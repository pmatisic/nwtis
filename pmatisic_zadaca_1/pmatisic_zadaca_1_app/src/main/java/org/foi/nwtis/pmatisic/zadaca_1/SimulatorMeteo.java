package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
          "Greška kod učitavanja postavki iz datoteke! " + e.getMessage());
    } catch (IOException e) {
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
          "Greška kod učitavanja meteo podatka iz datoteke! " + e.getMessage());
    }
  }

  // provjerava argumente dobivene
  private boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      var argument = args[0];
      String provjeraUnosa = "NWTiS_[a-zA-Z0-9.]{1,255}_1.(txt|xml|bin|json|yaml)";
      Pattern uzorak = Pattern.compile(provjeraUnosa);
      Matcher m = uzorak.matcher(argument);
      boolean status = m.matches();
      if (status == false) {
        Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
            "Greška pri unosu argumenta!");
      }
      return status;
    } else {
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE,
          "Greška, nije unešen argument!");
      return false;
    }
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

        this.posaljiMeteoPodatak(vazeciMeteo, konf);
        if (!jestPrviMeteoPodatak(brojac)) {
          this.obradiSpavanje(prethodniMeteo, vazeciMeteo);
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

  private void posaljiMeteoPodatak(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {
    try {
      String adresa = konf.dajPostavku("posluziteljGlavniAdresa");
      short mreznaVrata = Short.parseShort(konf.dajPostavku("posluziteljGlavniVrata"));
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

      String komanda = obradiZahtjev(vazeciMeteo, konf);
      pisac.write(komanda);
      pisac.flush();
      mreznaUticnica.shutdownOutput();

      var poruka = new StringBuilder();

      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;

        poruka.append(red);
      }
      String primitak = poruka.toString();
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj! " + e.getMessage());
    }
  }

  // ovdje obradivam komandu
  private String obradiZahtjev(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {

    if (vazeciMeteo.temperatura() != -999) {

    }
    return null;
  }

  private boolean jestZaglavlje(int brojac) {
    return brojac == 1;
  }

  private void obradiSpavanje(MeteoSimulacija prethodniMeteo, MeteoSimulacija vazeciMeteo) {
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
      Logger.getGlobal().log(Level.SEVERE, "Greška u obradi! " + e.getMessage());
    }
  }

}
