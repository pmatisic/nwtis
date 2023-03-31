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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
      Logger.getLogger(SimulatorMeteo.class.getName()).log(Level.SEVERE, "Nije unešen argument!");
      return false;
    }
  }

  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  // pokretanje simulatora
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
          this.obradiSpavanje(prethodniMeteo, vazeciMeteo,
              Integer.parseInt(konf.dajPostavku("trajanjeSekunde")));
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

  // primanje odgovora i slanje podataka na mr
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

  /*
   * KORISNIK pero LOZINKA 123456 SENZOR FOISK-SHTC3 0:0:18 19 16 za napraviti tu komandu
   * "KORISNIK " konf.dohvatiPostavku("korisnickoIme") + " LOZINKA " +
   * konf.dohvatiPostavku("korisnickaLozinka") + " SENZOR " + vazeciMeteo.id() + " " +
   * vazeciMeteo.vrijeme() + " " + vazeciMeteo.temperatura()...
   */
  // ovdje obradivam komandu
  private String obradiZahtjev(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {
    HashMap<String, String> komanda = new HashMap<>();

    // postavljanje korisničkog imena i lozinke
    komanda.put("korisnik", konf.dajPostavku("korisnickoIme"));
    komanda.put("lozinka", konf.dajPostavku("korisnickaLozinka"));

    // postavljanje podataka o senzoru
    komanda.put("senzor", vazeciMeteo.id());
    komanda.put("vrijeme", vazeciMeteo.vrijeme());

    if (vazeciMeteo.temperatura() != -999) {
      komanda.put("temperatura", String.valueOf(vazeciMeteo.temperatura()));
    }

    if (vazeciMeteo.vlaga() != -999) {
      komanda.put("vlaga", String.valueOf(vazeciMeteo.vlaga()));
    }

    if (vazeciMeteo.tlak() != -999) {
      komanda.put("tlak", String.valueOf(vazeciMeteo.tlak()));
    }


    // spajanje dijelova komande u konačni oblik
    StringBuilder sb = new StringBuilder();
    sb.append("KORISNIK ").append(komanda.get("korisnik")).append(" ");
    sb.append("LOZINKA ").append(komanda.get("lozinka")).append(" ");
    sb.append("SENZOR ").append(komanda.get("senzor")).append(" ");
    if (komanda.get("vrijeme") != null) {
      sb.append(komanda.get("vrijeme"));
    }
    if (komanda.get("temperatura") != null) {
      sb.append(" ").append(komanda.get("temperatura"));
    }
    if (komanda.get("vlaga") != null) {
      sb.append(" ").append(komanda.get("vlaga"));
    }
    if (komanda.get("tlak") != null) {
      sb.append(" ").append(komanda.get("tlak"));
    }
    // sb.append(komanda.get("vrijeme")).append(" ");
    // sb.append(komanda.get("temperatura")).append(" ");
    // sb.append(komanda.get("vlaga")).append(" ");
    // sb.append(komanda.get("tlak"));

    String komandaString = sb.toString();

    System.out.println(komandaString);

    // int brojPokusaja = Integer.parseInt(konf.dajPostavku("brojPokusaja"));
    // String datotekaProblema = konf.dajPostavku("datotekaProblema");

    return komandaString;
  }

  private boolean jestZaglavlje(int brojac) {
    return brojac == 1;
  }

  // obradiva spavanje i parsanje
  private void obradiSpavanje(MeteoSimulacija prethodniMeteo, MeteoSimulacija vazeciMeteo,
      int trajanjeSekunde) {
    String prvi = prethodniMeteo.vrijeme();
    String drugi = vazeciMeteo.vrijeme();
    SimpleDateFormat format = new SimpleDateFormat("H:mm:ss"); // TODO morat cemo sredit ovaj format
    try {
      Date datum1 = format.parse(prvi);
      Date datum2 = format.parse(drugi);
      long razlika = datum2.getTime() - datum1.getTime(); // u ms
      razlika = razlika / 1000; // u sekundama
      razlika = razlika - trajanjeSekunde; // korigiranje razlike
      if (razlika > 0) {
        long spavaj = razlika * 1000; // pretvaranje u ms
        Thread.sleep(spavaj);
      }
    } catch (ParseException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u pretvorbi vremena! " + e.getMessage());
    } catch (InterruptedException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u obradi! " + e.getMessage());
    }
  }

}
