package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class PosluziteljUdaljenosti {

  protected Konfiguracija konf;
  protected int brojRadnika;
  protected int maksVrijemeNeaktivnosti;
  private int ispis = 0; // TODO napravit ispis prema tablici
  private int mreznaVrata = 8000;
  private int brojCekaca = 10;
  private boolean kraj = false;
  LinkedHashMap<String, String> zadnjiZahtjevi = new LinkedHashMap<String, String>() {
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
      return size() > Integer.parseInt(konf.dajPostavku("brojZadnjihSpremljenih"));
    }
  };

  // main
  public static void main(String[] args) {
    var pu = new PosluziteljUdaljenosti();

    if (!pu.provjeriArgumente(args)) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Nije upisan naziv datoteke!");
      return;
    }

    try {
      var konf = pu.ucitajPostavke(args[0]);
      pu.pokreniPosluzitelja(konf);
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Greška kod učitavanja postavki iz datoteke! " + e.getMessage());
    } catch (IOException e) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Greška kod učitavanja podataka o udaljenosti iz datoteke! " + e.getMessage());
    }
  }

  // provjerava dobivene argumente
  private boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      var argument = args[0];
      String provjeraUnosa = "NWTiS_[a-zA-Z0-9.]{1,255}_2.(txt|xml|bin|json|yaml)";
      Pattern uzorak = Pattern.compile(provjeraUnosa);
      Matcher m = uzorak.matcher(argument);
      boolean status = m.matches();
      if (status == false) {
        Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
            "Greška pri unosu argumenta!");
      }
      return status;
    } else {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Nije unešen argument!");
      return false;
    }
  }

  // ucitaj postavke
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    return KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
  }

  // pokretanje posluzitelja
  public void pokreniPosluzitelja(Konfiguracija konf) throws IOException {
    this.konf = konf;
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));

    try {
      if (jestSlobodan()) {
        this.pripremiPosluzitelja();
      }
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u pokretanju poslužitelja! " + e.getMessage());
    }

    var nazivDatoteke = konf.dajPostavku("datotekaSerijalizacija");
    var putanja = Path.of(nazivDatoteke);

    if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
      throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka!");
    } else {
      deserijalizirajPodatke(nazivDatoteke);
    }
  }

  // serijaliacija podataka za komandu
  private boolean serijalizirajPodatke() {
    String nazivDatoteke = konf.dajPostavku("datotekaSerijalizacija");
    File datoteka = new File(nazivDatoteke);

    try {
      FileOutputStream fos = new FileOutputStream(datoteka);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(zadnjiZahtjevi);
      oos.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  // deserijaliacija podataka na pokretanju posluzitelja
  private Properties deserijalizirajPodatke(String s) {
    var nazivDatoteke = konf.dajPostavku("datotekaSerijalizacija");
    Properties objekt = new Properties();
    File datoteka = new File(nazivDatoteke);

    try {
      FileInputStream fis = new FileInputStream(datoteka);
      ObjectInputStream ois = new ObjectInputStream(fis);
      objekt = (Properties) ois.readObject();
      ois.close();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return objekt;
  }

  // provjera je li port slobodan
  public boolean jestSlobodan() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata)) {
      return true;
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na mrežna vrata! " + e.getMessage());
      return false;
    }
  }

  // stvaranje komunikacije
  public void pripremiPosluzitelja() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        BufferedReader ulaz = new BufferedReader(new InputStreamReader(veza.getInputStream()));
        String zahtjev = ulaz.readLine();
        String odgovor = obradiZahtjev(zahtjev);
        PrintWriter izlaz = new PrintWriter(new OutputStreamWriter(veza.getOutputStream()));

        izlaz.println(odgovor);
        izlaz.flush();
        veza.close();
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u stvaranju veze! " + e.getMessage());
    }
  }

  /**
   * Funkcija <i>izracunajUdaljenost</i> je temeljena na Haversineovoj formuli, koja se često
   * koristi za izračunavanje udaljenosti između dvije točke na Zemljinoj površini pomoću njihovih
   * geografskih koordinata (širina i dužina). Haversineova formula je posebno korisna za male
   * udaljenosti, gdje se uzima u obzir zakrivljenost Zemlje.
   * 
   * @see - Wikipedia članak o Haversineovoj formuli:
   *      <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine formula</a>
   * @see - StackOverflow odgovor s implementacijom u Javi: <a href=
   *      "https://stackoverflow.com/questions/27928/calculate-distance-between-two-latitude-longitude-points-haversine-formula">Calculate
   *      distance between two latitude-longitude points? (Haversine formula)</a>
   * 
   * @param lat1 Geografska širina prve točke (u stupnjevima). Geografska širina je kutna udaljenost
   *        neke točke sjeverno ili južno od ekvatora. Vrijednost varira od -90° (južni pol) do 90°
   *        (sjeverni pol).
   * @param lon1 Geografska dužina prve točke (u stupnjevima). Geografska dužina je kutna udaljenost
   *        neke točke istočno ili zapadno od početnog meridijana (Greenwich meridijan). Vrijednost
   *        varira od -180° (zapadno) do 180° (istočno).
   * @param lat2 Geografska širina druge točke (u stupnjevima).
   * @param lon2 Geografska dužina druge točke (u stupnjevima).
   * @return Funkcija vraća udaljenost između dvije točke u kilometrima kao rezultat tipa double.
   */
  public static double izracunajUdaljenost(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Radijus Zemlje u kilometrima

    // Pretvorba stupnjeva u radijane
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }

  public String obradiZahtjev(String zahtjev) {
    String[] dijelovi = zahtjev.trim().split("\\s+");

    if (dijelovi.length < 1 || !dijelovi[0].equalsIgnoreCase("UDALJENOST")) {
      return "ERROR 10: Neispravan format komande.";
    }

    if (dijelovi.length == 2 && dijelovi[1].equalsIgnoreCase("SPREMI")) {
      // obrada naredbe "UDALJENOST SPREMI"
      if (serijalizirajPodatke()) {
        return "OK";
      } else {
        return "ERROR 19: Neuspješno spremanje podataka.";
      }
    } else if (dijelovi.length == 5) {
      // obrada naredbe "UDALJENOST 46.30771 16.33808 46.02419 15.90968"
      try {
        double lat1 = Double.parseDouble(dijelovi[1]);
        double lon1 = Double.parseDouble(dijelovi[2]);
        double lat2 = Double.parseDouble(dijelovi[3]);
        double lon2 = Double.parseDouble(dijelovi[4]);

        String kljuc = lat1 + "," + lon1 + "," + lat2 + "," + lon2;

        // provjeri da li zahtjev postoji u kolekciji zadnjih zahtjeva
        if (zadnjiZahtjevi.containsKey(kljuc)) {
          return "OK " + zadnjiZahtjevi.get(kljuc);
        }

        // ako ne postoji, izračunaj udaljenost
        double udaljenost = izracunajUdaljenost(lat1, lon1, lat2, lon2);

        // ažuriraj kolekciju zadnjih zahtjeva
        if (zadnjiZahtjevi.size() >= Integer.parseInt(konf.dajPostavku("brojZadnjihSpremljenih"))) {
          String najstarijiKljuc = zadnjiZahtjevi.keySet().iterator().next();
          zadnjiZahtjevi.remove(najstarijiKljuc);
        }
        zadnjiZahtjevi.put(kljuc, String.format("%.2f", udaljenost));

        return "OK " + String.format("%.2f", udaljenost);

      } catch (NumberFormatException e) {
        return "ERROR 19: Neispravne koordinate.";
      }
    } else {
      return "ERROR 10: Neispravan format komande.";
    }
  }

}
