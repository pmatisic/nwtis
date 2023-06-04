package org.foi.nwtis.pmatisic.projekt.dretva;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.pomocnik.StanjePosluzitelja;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.GlavniPosluzitelj;

public class MrezniRadnik extends Thread {

  private Map<String, Lokacija> poznateLokacije = new HashMap<>();
  private GlavniPosluzitelj gp;
  private Matcher m;
  private StanjePosluzitelja stanjePosluzitelja;
  private int ispis = 0;
  protected Socket mreznaUticnica;
  protected Konfiguracija konf;

  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf, GlavniPosluzitelj gp) {
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.gp = gp;
  }

  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konf, GlavniPosluzitelj gp,
      StanjePosluzitelja stanjePosluzitelja) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konf = konf;
    this.stanjePosluzitelja = stanjePosluzitelja;
    this.gp = gp;
    this.poznateLokacije = gp.lokacije;
  }

  @Override
  public synchronized void start() {
    super.start();
  }

  @Override
  public synchronized void run() {
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
      m = provjeriKomanduZaGlavniKlijent(neobradenaPoruka);
      if (m != null) {
        obradenaPoruka = obradiKomanduZaGlavniKlijent(m);
      } else {
        Logger.getGlobal().log(Level.SEVERE, "Greška u komandi!");
        return;
      }
      String odgovor = this.obradiZahtjev(obradenaPoruka);
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownInput();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u radu dretve! " + e.getMessage());
    }
  }

  @Override
  public synchronized void interrupt() {
    Logger.getLogger(MrezniRadnik.class.getName()).info("Mrežni radnik se gasi!");
    super.interrupt();
  }

  private String spojiSeNaPosluziteljUdaljenosti(String adresa, int mreznaVrata, String komanda) {
    String primitak = null;
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

  private Matcher provjeriKomanduZaGlavniKlijent(String s) {
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

  private String obradiZahtjev(Map<String, String> mapa) {
    String komanda = izvuciKomandu(mapa);
    switch (komanda) {
      case "STATUS":
        return "OK " + stanjePosluzitelja.dajStatus().ordinal();
      case "KRAJ":
        gp.kraj = true;
        return "OK";
      case "INIT":
        stanjePosluzitelja.promijeniStatus(Status.AKTIVAN);
        return "OK";
      case "PAUZA":
        stanjePosluzitelja.promijeniStatus(Status.PAUZA);
        return "OK " + stanjePosluzitelja.dajBrojacZahtjeva();
      case "INFO":
        if (mapa.get("INFO").equals("DA")) {
          stanjePosluzitelja.postaviIspisKomandi(true);
        } else if (mapa.get("INFO").equals("NE")) {
          stanjePosluzitelja.postaviIspisKomandi(false);
        }
        return "OK";
      default:
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
    }
  }

  private String izvuciKomandu(Map<String, String> mapa) {
    for (String kljuc : mapa.keySet()) {
      if (kljuc.equals("KRAJ") || kljuc.equals("STATUS") || kljuc.equals("INIT")
          || kljuc.equals("PAUZA") || kljuc.equals("INFO")) {
        return kljuc;
      }
    }
    return "";
  }

  private String obradiUdaljenost(String idLokacija1, String idLokacija2) {
    if (lokacijaPostoji(idLokacija1) && lokacijaPostoji(idLokacija2)) {
      String adresa = konf.dajPostavku("posluziteljUdaljenostiAdresa");
      short mreznaVrata = Short.parseShort(konf.dajPostavku("posluziteljUdaljenostiVrata"));
      Lokacija lok1 = poznateLokacije.get(idLokacija1);
      Lokacija lok2 = poznateLokacije.get(idLokacija2);
      String komanda = String.format("UDALJENOST %s %s %s %s", lok1.gpsSirina(), lok1.gpsDuzina(),
          lok2.gpsSirina(), lok2.gpsDuzina());
      String odgovor = spojiSeNaPosluziteljUdaljenosti(adresa, mreznaVrata, komanda);
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
    String odgovor = spojiSeNaPosluziteljUdaljenosti(adresa, mreznaVrata, komanda);
    if (odgovor.startsWith("OK")) {
      return "OK";
    } else {
      return "ERROR 29 Serijalizacija nije uspjela.";
    }
  }

}
