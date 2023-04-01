package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
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

  private int ispis = 0;

  // main
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

  // provjerava dobivene argumente
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

  // ucitaj postavke
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

      // TODO ispis, varijabla na pocetku klase
      if (this.ispis == 1) {
        Logger.getGlobal().log(Level.INFO, red);
      }

      brojac++;
      if (jestZaglavlje(brojac))
        continue;

      var kolone = red.split(";");
      if (!redImaPetKolona(kolone)) {
        Logger.getGlobal().log(Level.WARNING, red);
      } else {
        var vazeciMeteo = new MeteoSimulacija(kolone[0], kolone[1], Float.parseFloat(kolone[2]),
            Float.parseFloat(kolone[3]), Float.parseFloat(kolone[4]));

        String foo = this.posaljiMeteoPodatak(vazeciMeteo, konf); // primljeni odgovor od mr
        System.out.println(foo);

        // TODO odgovor od mr
        if (foo.contains("ERROR")) {
          provjeriPostupak(vazeciMeteo, konf);
        }
        if (foo.contains("OK ")) {
          provjeriPostupak(vazeciMeteo, konf);
        }
        if (foo.equals("OK")) {
          provjeriPostupak(vazeciMeteo, konf);
        }
        if (!jestPrviMeteoPodatak(brojac)) {
          this.obradiSpavanje(prethodniMeteo, vazeciMeteo,
              Integer.parseInt(konf.dajPostavku("trajanjeSekunde")));
        }

        prethodniMeteo = vazeciMeteo;
      }
    }
  }

  // provjerava je li zaglavlje
  private boolean jestZaglavlje(int brojac) {
    return brojac == 1;
  }

  // provjerava je li red u datoteci ima 5 kolona
  private boolean redImaPetKolona(String[] kolone) {
    return kolone.length == 5;
  }

  // provjerava je li meteo podatak drugi red u datoteci
  private boolean jestPrviMeteoPodatak(int brojac) {
    return brojac == 2;
  }

  // obradiva spavanje i pretvaranje vremena
  private void obradiSpavanje(MeteoSimulacija prethodniMeteo, MeteoSimulacija vazeciMeteo,
      int trajanjeSekunde) {
    String prvi = prethodniMeteo.vrijeme();
    String drugi = vazeciMeteo.vrijeme();
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    try {
      Date vrijeme1 = format.parse(prvi);
      Date vrijeme2 = format.parse(drugi);
      long razlika = vrijeme2.getTime() - vrijeme1.getTime(); // u ms
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

  // primanje odgovora i slanje podataka na mr
  private String posaljiMeteoPodatak(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {
    String primitak = "";

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

      primitak = poruka.toString(); // primljeni odgovor od mr
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na poslužitelj! " + e.getMessage());
    }

    return primitak;
  }

  // ovdje obradivam komandu
  private String obradiZahtjev(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {
    HashMap<String, String> komanda = new HashMap<>();

    komanda.put("korisnik", konf.dajPostavku("korisnickoIme"));
    komanda.put("lozinka", konf.dajPostavku("korisnickaLozinka"));
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

    String komandaString = sb.toString();
    return komandaString;
  }

  // provjeravam postupak slanja preko uvjeta iz pdfa
  public void provjeriPostupak(MeteoSimulacija vazeciMeteo, Konfiguracija konf) {
    int brojPokusaja = Integer.parseInt(konf.dajPostavku("brojPokusaja"));
    String datotekaProblema = konf.dajPostavku("datotekaProblema");
    boolean uspjesno = false;
    int brojPokusajaOstalo = brojPokusaja;

    while (!uspjesno && brojPokusajaOstalo > 0) {
      String odgovor = posaljiMeteoPodatak(vazeciMeteo, konf);
      if (odgovor.contains("ERROR")) {
        brojPokusajaOstalo--;
      } else {
        uspjesno = true;
      }
    }

    if (!uspjesno) {
      try {
        FileWriter writer = new FileWriter(datotekaProblema, true);
        writer.write(vazeciMeteo.id() + ";" + vazeciMeteo.vrijeme() + ";"
            + vazeciMeteo.temperatura() + ";" + vazeciMeteo.vlaga() + ";" + vazeciMeteo.tlak() + ";"
            + "Greška u slanju podataka" + "\n");
        writer.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

}
