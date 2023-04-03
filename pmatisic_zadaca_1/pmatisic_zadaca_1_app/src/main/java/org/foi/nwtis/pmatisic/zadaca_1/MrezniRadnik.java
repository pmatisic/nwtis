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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Uredaj;

public class MrezniRadnik extends Thread {

  protected Socket mreznaUticnica;
  protected Konfiguracija konf;
  private int ispis = 0;
  private GlavniPosluzitelj gp;
  private Matcher m1;
  private Matcher m2;
  private Map<String, Integer> brojPodatakaPoUredjaju = new HashMap<>();
  private Map<String, List<Map<String, Double>>> ocitanja = new HashMap<>();
  private List<String> alarmInfoLog = new ArrayList<>();
  private Map<String, Lokacija> poznateLokacije = new HashMap<>();
  private Map<String, Uredaj> poznatiUredjaji = new HashMap<>();

  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.ispis = Integer.parseInt(this.konf.dajPostavku("ispis"));
  }

  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf, GlavniPosluzitelj gp) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.ispis = Integer.parseInt(this.konf.dajPostavku("ispis"));
    this.gp = gp;
    this.poznatiUredjaji = gp.uredaji;
    this.poznateLokacije = gp.lokacije;
  }

  @Override
  public synchronized void start() {
    super.start();
  }

  @Override
  public void run() {
    try {
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
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u radu dretve! " + e.getMessage());
    }
  }

  @Override
  public void interrupt() {
    Logger.getLogger(MrezniRadnik.class.getName()).info("Mrežni radnik se gasi");
    super.interrupt();
  }

  private String spojiSeNaPU(String adresa, int mreznaVrata, String komanda) {
    String primitak = "";
    try {
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      mreznaUticnica.setSoTimeout(Short.parseShort(konf.dajPostavku("maksCekanje")));
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
    return primitak;
  }

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

  private String obradiZahtjev(Map<String, String> mapa) {
    String korisnik = mapa.get("KORISNIK");
    String lozinka = mapa.get("LOZINKA");
    String korisnickaUloga = autenticirajKorisnika(korisnik, lozinka);
    if (korisnickaUloga.equals("0")) {
      return "ERROR 21 Neispravni korisnički podaci.";
    }
    String komanda = izvuciKomandu(mapa);
    String idUredjaj = mapa.get("METEO");
    String vrijeme = mapa.get("vrijeme");
    String temp = mapa.get("temp");
    String vlaga = mapa.get("vlaga");
    String tlak = mapa.get("tlak");
    switch (komanda) {
      case "KRAJ":
        if (korisnickaUloga.equals("1")) {
          gp.kraj = true;
          return "OK";
        } else {
          return "ERROR 22 Korisnik nije administrator.";
        }
      case "SENZOR":
        return obradiSenzor(korisnickaUloga, idUredjaj, vrijeme, temp, vlaga, tlak);
      case "METEO":
        return obradiMeteo(idUredjaj);
      case "UDALJENOST":
        String lokacijeString = mapa.get("UDALJENOST");
        String[] lokacije = lokacijeString.split("'\\s+'");
        if (lokacije.length == 1) {
          return obradiUdaljenostSpremi();
        }
        if (lokacije.length != 2) {
          return "ERROR 25 Neispravan broj lokacija.";
        }
        String idLokacija1 = lokacije[0].substring(1, lokacije[0].length());
        String idLokacija2 = lokacije[1].substring(0, lokacije[1].length() - 1);
        return obradiUdaljenost(idLokacija1, idLokacija2);
      default:
        return "ERROR 20 Nepoznata komanda ili format komande nije ispravan.";
    }
  }

  private String obradiSenzor(String korisnickaUloga, String idUredjaj, String vrijeme, String temp,
      String vlaga, String tlak) {
    if (!korisnickaUloga.equals("1")) {
      return "ERROR 22 Korisnik nije administrator.";
    }
    if (!poznatiUredjaji.containsKey(idUredjaj)) {
      return "ERROR 23 Uređaj ne postoji.";
    }
    int brojPrimljenihMeteoPodataka = 1;
    if (vlaga != null)
      brojPrimljenihMeteoPodataka++;
    if (tlak != null)
      brojPrimljenihMeteoPodataka++;
    Integer brojOcekivanihMeteoPodataka = brojPodatakaPoUredjaju.get(idUredjaj);
    if (brojOcekivanihMeteoPodataka == null
        || brojPrimljenihMeteoPodataka != brojOcekivanihMeteoPodataka) {
      return "ERROR 29 Pogrešan broj primljenih meteo podataka.";
    }
    Map<String, Double> novoOcitanje = kreirajNovoOcitanje(temp, vlaga, tlak);
    List<Map<String, Double>> očitanjaUređaja = ocitanja.get(idUredjaj);
    if (očitanjaUređaja == null) {
      očitanjaUređaja = new ArrayList<>();
      ocitanja.put(idUredjaj, očitanjaUređaja);
    }
    očitanjaUređaja.add(novoOcitanje);
    List<Map<String, Double>> prethodnaOcitanja = ocitanja.get(idUredjaj);
    String alarmInfo = "";
    if (prethodnaOcitanja != null && !prethodnaOcitanja.isEmpty()) {
      alarmInfo =
          obradiAlarm(novoOcitanje, prethodnaOcitanja.subList(0, prethodnaOcitanja.size() - 1));
    }
    if (!alarmInfo.isEmpty()) {
      StringBuilder logEntry = new StringBuilder();
      logEntry.append("Vrijeme: ").append(vrijeme).append(", ID uređaja: ").append(idUredjaj)
          .append(", Meteo podaci: ").append(novoOcitanje.toString()).append(", Odstupanje: ")
          .append(alarmInfo);
      alarmInfoLog.add(logEntry.toString());

      očitanjaUređaja.clear();
    }
    String odgovor = generirajOdgovor(alarmInfo);
    return odgovor;
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
          "Datoteka korisnika nije pronađena!", e);
    } catch (IOException e) {
      Logger.getLogger(PosluziteljUdaljenosti.class.getName()).log(Level.SEVERE,
          "Problem s čitanjem datoteke korisnika!", e);
    }
    return "0";
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
          break;
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
      odgovor += " ALARM " + alarmInfo;
    }
    return odgovor;
  }

  private String obradiMeteo(String idUredjaj) {
    if (senzorPostoji(idUredjaj)) {
      Map<String, Double> zadnjeOcitanje = dohvatiZadnjeOcitanje(idUredjaj);
      String vrijeme = dohvatiVrijemeOcitanja(idUredjaj);
      String temp = String.valueOf(zadnjeOcitanje.get("temp"));
      String vlaga = String.valueOf(zadnjeOcitanje.get("vlaga"));
      String tlak = String.valueOf(zadnjeOcitanje.get("tlak"));
      return String.format("OK %s %s (%s (%s)+)+", vrijeme, temp, vlaga, tlak);
    } else {
      return "ERROR 23 Nepostojeći senzor.";
    }
  }

  private String dohvatiVrijemeOcitanja(String idUredjaj) {
    for (int i = alarmInfoLog.size() - 1; i >= 0; i--) {
      String log = alarmInfoLog.get(i);
      if (log.contains(idUredjaj)) {
        String[] dijelovi = log.split(" ");
        return dijelovi[0];
      }
    }
    return "";
  }

  private boolean senzorPostoji(String idUredjaj) {
    return poznatiUredjaji.containsKey(idUredjaj);
  }

  private Map<String, Double> dohvatiZadnjeOcitanje(String idUredjaj) {
    List<Map<String, Double>> listaOcitanja = ocitanja.get(idUredjaj);
    if (listaOcitanja != null && !listaOcitanja.isEmpty()) {
      return listaOcitanja.get(listaOcitanja.size() - 1);
    } else {
      return new HashMap<>();
    }
  }

  private String obradiUdaljenost(String idLokacija1, String idLokacija2) {
    if (lokacijaPostoji(idLokacija1) && lokacijaPostoji(idLokacija2)) {
      String adresa = konf.dajPostavku("posluziteljUdaljenostiAdresa");
      short mreznaVrata = Short.parseShort(konf.dajPostavku("posluziteljUdaljenostiVrata"));
      Lokacija lok1 = poznateLokacije.get(idLokacija1);
      Lokacija lok2 = poznateLokacije.get(idLokacija2);
      String komanda = String.format("UDALJENOST %s %s %s %s", lok1.gpsSirina(), lok1.gpsDuzina(),
          lok2.gpsSirina(), lok2.gpsDuzina());
      String odgovor = spojiSeNaPU(adresa, mreznaVrata, komanda);
      return odgovor;
    } else {
      return "ERROR 24 Nepostojeća lokacija.";
    }
  }

  private boolean lokacijaPostoji(String idLokacija) {
    return poznateLokacije.containsKey(idLokacija);
  }

  private String obradiUdaljenostSpremi() {
    String adresa = konf.dajPostavku("posluziteljUdaljenostiAdresa");
    short mreznaVrata = Short.parseShort(konf.dajPostavku("posluziteljUdaljenostiVrata"));
    String komanda = "UDALJENOST SPREMI";
    String odgovor = spojiSeNaPU(adresa, mreznaVrata, komanda);
    if (odgovor.startsWith("OK")) {
      return "OK";
    } else {
      return "ERROR 29 Serijalizacija nije uspjela.";
    }
  }

}
