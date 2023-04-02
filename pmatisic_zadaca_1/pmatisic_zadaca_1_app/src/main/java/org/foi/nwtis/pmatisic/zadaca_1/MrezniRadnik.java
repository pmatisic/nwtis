package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;

public class MrezniRadnik extends Thread {

  protected Socket mreznaUticnica;
  protected Konfiguracija konf;
  private int ispis = 0;
  private GlavniPosluzitelj gp;
  private PosluziteljUdaljenosti pu;
  private Matcher m1;
  private Matcher m2;
  private static AtomicInteger brojZahtjeva = new AtomicInteger(0);
  private static AtomicInteger brojOdgovora = new AtomicInteger(0);
  private static Object lockOdgovora = new Object();
  private List<String> poznatiUredjaji = new ArrayList<>();
  private Map<String, Integer> brojPodatakaPoUredjaju = new HashMap<>();
  private Map<String, List<Map<String, Double>>> ocitanja = new HashMap<>();
  private List<String> alarmInfoLog = new ArrayList<>();

  // konstruktor
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.ispis = Integer.parseInt(this.konf.dajPostavku("ispis"));
  }

  // overloadani konstruktor mreznog radnika
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf, GlavniPosluzitelj gp) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.ispis = Integer.parseInt(this.konf.dajPostavku("ispis"));
    this.gp = gp;
    ucitajPoznateUredjaje(konf.dajPostavku("datotekaMeteo"));
  }

  // ra algoritam
  private void posaljiZahtjev() {
    synchronized (lockOdgovora) {
      brojZahtjeva.incrementAndGet();
    }
  }

  // ra algoritam
  private void obradiOdgovor() {
    synchronized (lockOdgovora) {
      brojOdgovora.incrementAndGet();
      lockOdgovora.notifyAll();
    }
  }

  // ra algoritam
  private void cekajOdgovore(int ukupnoDretvi) {
    synchronized (lockOdgovora) {
      while (brojOdgovora.get() < ukupnoDretvi - 1) {
        try {
          lockOdgovora.wait();
        } catch (InterruptedException e) {
          Logger.getGlobal().log(Level.SEVERE, "Greška u čekanju odgovora! " + e.getMessage());
        }
      }
    }
  }

  // pokretanje dretve
  @Override
  public synchronized void start() {
    super.start();
  }

  // sredisnji dio
  @Override
  public void run() {
    try {
      // Ovdje dodajemo Ricart-Agrawala algoritam
      posaljiZahtjev();

      // Čekamo odgovore od ostalih dretvi
      // Pretpostavimo da ukupnoDretvi varijabla predstavlja ukupan broj dretvi koje koriste resurs
      int ukupnoDretvi = Integer.parseInt(konf.dajPostavku("brojRadnika"));
      cekajOdgovore(ukupnoDretvi);

      var citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      var poruka = new StringBuilder();

      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;

        if (this.ispis == 1) {
          Logger.getGlobal().log(Level.INFO, red);
        }
        poruka.append(red);
      }

      Map<String, String> obradenaPoruka = new HashMap<String, String>();
      String neobradenaPoruka = poruka.toString();
      m1 = provjeriZaGlavniKlijent(neobradenaPoruka);
      m2 = provjeriZaSimulatorMeteo(neobradenaPoruka);

      if (m1 != null) {
        obradenaPoruka = obradiKomanduZaGlavniKlijent(m1);
      } else if (m2 != null) {
        obradenaPoruka = obradiKomanduZaSimulatorMeteo(m2);
      } else {
        Logger.getGlobal().log(Level.SEVERE, "Greška u komandi!");
        return;
      }

      this.mreznaUticnica.shutdownInput();
      String odgovor = this.obradiZahtjev(obradenaPoruka);
      pisac.write(odgovor);
      pisac.flush();

      // Nakon završetka kritičnog dijela, šaljemo odgovor ostalim dretvama
      obradiOdgovor();

      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u radu dretve! " + e.getMessage());
    }
  }

  // prekidanje dretve
  @Override
  public void interrupt() {
    Logger.getLogger(MrezniRadnik.class.getName()).info("Mrezni radnik se gasi");
    super.interrupt();
  }

  // TODO metoda za spajanje na posluzitelj udaljenosti
  private void spojiSeNaPU(String adresa, int mreznaVrata, String komanda) {
    String primitak = ""; // jel ova metoda spojise ista vraca?
    try {
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      // TODO doraditi maksCekanje
      // mreznaUticnica.setSoTimeout(Short.parseShort(konf.dajPostavku("maksCekanje")));
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

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
      primitak = poruka.toString();
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE,
          "Greška u spajanju na PosluziteljUdaljenosti! " + e.getMessage());
    }
  }

  // provjera kor. unosa za komande
  private Matcher provjeriZaGlavniKlijent(String s) {
    String sintaksa =
        "(KORISNIK) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (LOZINKA) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) ((((METEO) (?<meteo>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS TEMP) (?<makstemp>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS VLAGA) (?<maksvlaga>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((MAKS TLAK) (?<makstlak>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+))|((ALARM) (?<alarm>[0-9a-zA-Z' ]+))|((UDALJENOST) (?<udaljenostnavodnici>'[0-9a-zA-Z ]+' '[0-9a-zA-Z ]+'))|((UDALJENOST) (?<udaljenostspremi>SPREMI))|(?<kraj>KRAJ)))";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  // provjera kor. unosa za komande
  private Matcher provjeriZaSimulatorMeteo(String s) {
    String sintaksa =
        "((KORISNIK) (?<korisnik>[0-9a-zA-Z_-]{3,10}) (LOZINKA) (?<lozinka>[0-9a-zA-Z!#_-]{3,10}) (SENZOR) (?<senzor>[0-9a-zA-ZćĆčČžŽšŠđĐ-]+) (?<vrijeme>(?:[1-9]|1\\d|2[0-3]):(?:[1-5]?\\d|0):(?:[1-5]?\\d|0)|0:(?:[1-5]?\\d):(?:[1-5]?\\d)) (?<temp>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?))( (?<vlaga>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?)( (?<tlak>(?:(?<=^|[^\\d.])[1-9]\\d{0,3}|0)(?:\\.\\d)?))?)?";
    Pattern p = Pattern.compile(sintaksa);
    Matcher m = p.matcher(s);
    if (!m.matches()) {
      return null;
    } else {
      return m;
    }
  }

  // obrada dobivene komande da bi se dobili podaci iz njih
  private static Map<String, String> obradiKomanduZaGlavniKlijent(Matcher m) {
    Map<String, String> grupe = new HashMap<>();

    grupe.put("KORISNIK", m.group("korisnik"));
    grupe.put("LOZINKA", m.group("lozinka"));
    grupe.put("METEO", m.group("meteo"));
    grupe.put("MAKS TEMP", m.group("makstemp"));
    grupe.put("MAKS VLAGA", m.group("maksvlaga"));
    grupe.put("MAKS TLAK", m.group("makstlak"));
    grupe.put("ALARM", m.group("alarm"));
    grupe.put("UDALJENOST", m.group("udaljenostnavodnici"));
    grupe.put("UDALJENOST SPREMI", m.group("udaljenostspremi"));
    grupe.put("KRAJ", m.group("kraj"));

    Map<String, String> pomocnaGrupa = new HashMap<>();

    for (String key : grupe.keySet()) {
      if (grupe.get(key) != null) {
        if (key == "UDALJENOST" || key == "UDALJENOST SPREMI") {
          pomocnaGrupa.put("UDALJENOST", grupe.get(key));
        } else {
          pomocnaGrupa.put(key, grupe.get(key));
        }
      }
    }

    return pomocnaGrupa;
  }

  // obrada dobivene komande da bi se dobili podaci iz njih
  private static Map<String, String> obradiKomanduZaSimulatorMeteo(Matcher m) {
    Map<String, String> grupe = new HashMap<>();

    grupe.put("KORISNIK", m.group("korisnik"));
    grupe.put("LOZINKA", m.group("lozinka"));
    grupe.put("SENZOR", m.group("senzor"));
    grupe.put("vrijeme", m.group("vrijeme"));
    grupe.put("temp", m.group("temp"));
    grupe.put("vlaga", m.group("vlaga"));
    grupe.put("tlak", m.group("tlak"));

    Map<String, String> pomocnaGrupa = new HashMap<>();

    for (String key : grupe.keySet()) {
      if (grupe.get(key) != null) {
        pomocnaGrupa.put(key, grupe.get(key));
      }
    }

    return pomocnaGrupa;
  }

  // ovdje obradivam zahtjev koji dobijem kroz komandu
  private String obradiZahtjev(Map<String, String> mapa) {
    // autentikacijski dio
    String korisnik = mapa.get("KORISNIK");
    String lozinka = mapa.get("LOZINKA");
    String korisnickaUloga = autenticirajKorisnika(korisnik, lozinka);

    if (korisnickaUloga.equals("0")) {
      return "Greška: Neispravni korisnički podaci.";
    }

    // predmetni dio
    String komanda = izvuciKomandu(mapa);
    String idUredjaj = mapa.get("senzor");
    String vrijeme = mapa.get("vrijeme");
    String temp = mapa.get("temp");
    String vlaga = mapa.get("vlaga");
    String tlak = mapa.get("tlak");

    // Obradi predmetni dio na temelju komande
    switch (komanda) {
      case "KRAJ":
        if (korisnickaUloga.equals("1")) {
          // Prekini čekanje, zatvori mrežna vrata i pričekaj da sve aktivne dretve završe svoj rad
          return "Poslužitelj je završio s radom.";
        } else {
          return "Greška: Korisnik nije administrator.";
        }
      case "SENZOR":
        return obradiSenzor(korisnickaUloga, idUredjaj, vrijeme, temp, vlaga, tlak);
      case "METEO":
        // return obradiMeteo(idUredjaj);
      case "UDALJENOST":
        // return obradiUdaljenost(mapa.get("UDALJENOST"));
      case "UDALJENOST SPREMI":
        // Obradi komandu UDALJENOST SPREMI
      case "MAKS TEMP":
        // Obradi komandu MAKS TEMP
      case "MAKS VLAGA":
        // Obradi komandu MAKS VLAGA
      case "MAKS TLAK":
        // Obradi komandu MAKS TLAK
      case "ALARM":
        // Obradi komandu ALARM
      default:
        return "Greška: Nepoznata komanda.";
    }
  }

  private String autenticirajKorisnika(String korisnik, String lozinka) {
    String datotekaKorisnika = konf.dajPostavku("datotekaKorisnika");

    try (BufferedReader br = new BufferedReader(new FileReader(datotekaKorisnika))) {
      String linija;

      while ((linija = br.readLine()) != null) {
        String[] podaci = linija.split(";");

        if (podaci.length >= 5) {
          String korisnickoIme = podaci[2];
          String korisnickaLozinka = podaci[3];
          String uloga = podaci[4];

          if (korisnik.equals(korisnickoIme) && lozinka.equals(korisnickaLozinka)) {
            return uloga;
          }
        }
      }
    } catch (FileNotFoundException e) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Greška: datoteka korisnika nije pronađena!", e);
    } catch (IOException e) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Greška: problem s čitanjem datoteke korisnika!", e);
    }

    return "0"; // Vraća 0 ako korisnik nije pronađen ili lozinka nije ispravna
  }

  private String izvuciKomandu(Map<String, String> mapa) {
    for (String kljuc : mapa.keySet()) {
      if (kljuc.equals("KRAJ") || kljuc.equals("SENZOR") || kljuc.equals("METEO")
          || kljuc.equals("MAKS TEMP") || kljuc.equals("MAKS VLAGA") || kljuc.equals("MAKS TLAK")
          || kljuc.equals("ALARM") || kljuc.equals("UDALJENOST")
          || kljuc.equals("UDALJENOST SPREMI")) {
        return kljuc;
      }
    }
    return "";
  }

  private String obradiSenzor(String korisnickaUloga, String idUredjaj, String vrijeme, String temp,
      String vlaga, String tlak) {
    // Provjeri je li korisnik administrator
    if (!korisnickaUloga.equals("1")) {
      return "Greška: Korisnik nije administrator.";
    }
    // Provjeri je li ID uređaja poznat
    if (!poznatiUredjaji.contains(idUredjaj)) {
      return "Unknown device ID";
    }

    int brojPrimljenihMeteoPodataka = 1; // temperatura je uvijek prisutna
    if (vlaga != null)
      brojPrimljenihMeteoPodataka++;
    if (tlak != null)
      brojPrimljenihMeteoPodataka++;

    Integer brojOcekivanihMeteoPodataka = brojPodatakaPoUredjaju.get(idUredjaj);
    if (brojOcekivanihMeteoPodataka == null
        || brojPrimljenihMeteoPodataka != brojOcekivanihMeteoPodataka) {
      return "Invalid number of received meteo data";
    }

    // Ažurirajte način na koji dodajete novo očitanje u ocitanja
    Map<String, Double> novoOcitanje = kreirajNovoOcitanje(temp, vlaga, tlak);
    List<Map<String, Double>> očitanjaUređaja = ocitanja.get(idUredjaj);
    if (očitanjaUređaja == null) {
      očitanjaUređaja = new ArrayList<>();
      ocitanja.put(idUredjaj, očitanjaUređaja);
    }
    očitanjaUređaja.add(novoOcitanje);

    // Ažurirajte poziv metode obradiAlarm kako biste proslijedili cijeli popis prethodnih očitanja
    List<Map<String, Double>> prethodnaOcitanja = ocitanja.get(idUredjaj);
    String alarmInfo = "";
    if (prethodnaOcitanja != null && !prethodnaOcitanja.isEmpty()) {
      alarmInfo =
          obradiAlarm(novoOcitanje, prethodnaOcitanja.subList(0, prethodnaOcitanja.size() - 1)); // Ne
                                                                                                 // uključuj
                                                                                                 // trenutno
                                                                                                 // očitanje
    }

    if (!alarmInfo.isEmpty()) {
      StringBuilder logEntry = new StringBuilder();
      logEntry.append("Vrijeme: ").append(vrijeme).append(", ID uređaja: ").append(idUredjaj)
          .append(", Meteo podaci: ").append(novoOcitanje.toString()).append(", Odstupanje: ")
          .append(alarmInfo);
      alarmInfoLog.add(logEntry.toString());
    }

    // Generiraj odgovor
    String odgovor = generirajOdgovor(alarmInfo);
    return odgovor;
  }


  private void ucitajPoznateUredjaje(String csvDatoteka) {
    List<String[]> podaci = procitajCsvDatoteku(csvDatoteka);

    for (String[] red : podaci) {
      String idUredjaja = red[0]; // Pretpostavljamo da je ID uređaja prvi stupac u CSV datoteci
      poznatiUredjaji.add(idUredjaja);

      int brojMeteoPodataka = 0;
      for (int i = 2; i < red.length; i++) {
        if (!red[i].equals("-999")) {
          brojMeteoPodataka++;
        }
      }
      brojPodatakaPoUredjaju.put(idUredjaja, brojMeteoPodataka);
    }
  }

  private List<String[]> procitajCsvDatoteku(String csvDatoteka) {
    List<String[]> podaci = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(csvDatoteka))) {
      String linija;
      boolean prviRed = true;
      while ((linija = br.readLine()) != null) {
        if (prviRed) {
          prviRed = false;
          continue; // preskače prvi red (zaglavlje)
        }
        String[] red = linija.split(";"); // mijenjamo razdjelnik na točku sa zarezom
        podaci.add(red);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return podaci;
  }

  private boolean provjeriOdstupanje(double trenutnaVrijednost, double prethodnaVrijednost,
      double dozvoljenoOdstupanje) {
    return Math.abs(trenutnaVrijednost - prethodnaVrijednost) > dozvoljenoOdstupanje;
  }

  private String obradiAlarm(Map<String, Double> zadnjeOcitanje,
      List<Map<String, Double>> prethodnaOcitanja) {
    StringBuilder alarm = new StringBuilder();

    for (Map<String, Double> prethodnoOcitanje : prethodnaOcitanja) {
      for (String tip : zadnjeOcitanje.keySet()) {
        double trenutnaVrijednost = zadnjeOcitanje.get(tip);
        double prethodnaVrijednost = prethodnoOcitanje.get(tip);
        double dozvoljenoOdstupanje;

        switch (tip) {
          case "temperatura":
            dozvoljenoOdstupanje = Double.parseDouble(konf.dajPostavku("odstupanjeTemp"));
            break;
          case "vlaga":
            dozvoljenoOdstupanje = Double.parseDouble(konf.dajPostavku("odstupanjeVlaga"));
            break;
          case "tlak":
            dozvoljenoOdstupanje = Double.parseDouble(konf.dajPostavku("odstupanjeTlak"));
            break;
          default:
            throw new IllegalStateException("Nepoznat tip senzora: " + tip);
        }

        if (provjeriOdstupanje(trenutnaVrijednost, prethodnaVrijednost, dozvoljenoOdstupanje)) {
          alarm.append(tip.toUpperCase()).append(" ");
          break; // Ako je odstupanje pronađeno, nema potrebe provjeravati ostala očitanja za isti
                 // tip senzora
        }
      }
    }

    return alarm.toString().trim();
  }


  private Map<String, Double> kreirajNovoOcitanje(String temp, String vlaga, String tlak) {
    Map<String, Double> novoOcitanje = new HashMap<>();
    novoOcitanje.put("temp", Double.parseDouble(temp));
    if (vlaga != null) {
      novoOcitanje.put("vlaga", Double.parseDouble(vlaga));
    }
    if (tlak != null) {
      novoOcitanje.put("tlak", Double.parseDouble(tlak));
    }
    return novoOcitanje;
  }

  private String generirajOdgovor(String alarmInfo) {
    String odgovor = "OK";
    if (!alarmInfo.isEmpty()) {
      odgovor += " " + alarmInfo;
    }
    return odgovor;
  }

}
