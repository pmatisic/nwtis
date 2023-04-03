package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeKorisnika;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeLokacija;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeUredaja;

/**
 * Klasa GlavniPosluzitelj služi za upravljanje i pokretanje glavnog poslužitelja.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class GlavniPosluzitelj {

  /** Konfiguracijski objekt koji sadrži postavke poslužitelja. */
  protected Konfiguracija konf;

  /** Broj radnika koji će se koristiti za obradu zahtjeva. */
  protected int brojRadnika;

  /** Maksimalno vrijeme neaktivnosti poslužitelja. */
  protected int maksVrijemeNeaktivnosti;

  /** Mapa koja sadrži korisnike s njihovim korisničkim imenima kao ključevima. */
  protected Map<String, Korisnik> korisnici;

  /** Mapa koja sadrži lokacije s njihovim identifikatorima kao ključevima. */
  protected Map<String, Lokacija> lokacije;

  /** Mapa koja sadrži uređaje s njihovim identifikatorima kao ključevima. */
  protected Map<String, Uredaj> uredaji;

  /** Brojač za dretve koje se koriste za obradu zahtjeva. */
  private int dretva = 0;

  /** Oznaka za ispis dodatnih informacija. */
  private int ispis = 0;

  /** Mrežna vrata na kojima će poslužitelj slušati dolazne veze. */
  private int mreznaVrata = 8000;

  /** Broj čekača koji će se koristiti za spajanje na poslužitelj. */
  private int brojCekaca = 10;

  /** Zastavica za zaustavljanje poslužitelja. */
  protected boolean kraj = false;

  /**
   * Instancira glavni poslužitelj s konfiguracijskim objektom.
   *
   * @param konf Konfiguracijski objekt koji sadrži postavke poslužitelja.
   */
  public GlavniPosluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  /**
   * Pokreće poslužitelja, učitava korisnike, lokacije i uređaje te priprema poslužitelj za obradu
   * zahtjeva.
   */
  public void pokreniPosluzitelja() {
    try {
      if (jestSlobodan()) {
        this.ucitajKorisnike();
        this.ucitajLokacije();
        this.ucitajUredaje();
        this.pripremiPosluzitelja();
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u pokretanju poslužitelja! " + e.getMessage());
    }
  }

  /**
   * Provjerava je li mrežna vrata (port) slobodna za korištenje.
   *
   * @return true ako su mrežna vrata slobodna, false inače.
   */
  public boolean jestSlobodan() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata)) {
      return true;
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na mrežna vrata! " + e.getMessage());
      return false;
    }
  }

  /**
   * Priprema poslužitelj za obradu zahtjeva stvaranjem komunikacije i pokretanjem mrežnih radnika.
   */
  public void pripremiPosluzitelja() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        MrezniRadnik mr = new MrezniRadnik(veza, konf, this);
        mr.setName("pmatisic_" + Integer.toString(dretva));
        dretva++;
        mr.start();
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u stvaranju veze! " + e.getMessage());
    }
  }

  /**
   * Učitava korisnike iz datoteke korisnika i sprema ih u mapu korisnici. Ispisuje informacije o
   * korisnicima ako je ispis postavljen na 1.
   *
   * @throws IOException ako se dogodi greška pri čitanju datoteke.
   */
  public void ucitajKorisnike() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaKorisnika");
    var citac = new CitanjeKorisnika();
    this.korisnici = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String korime : this.korisnici.keySet()) {
        var k = this.korisnici.get(korime);
        Logger.getGlobal().log(Level.INFO, "Korisnik: " + k.prezime() + " " + k.ime());
      }
    }
  }

  /**
   * Učitava lokacije iz datoteke lokacija i sprema ih u mapu lokacije. Ispisuje informacije o
   * lokacijama ako je ispis postavljen na 1.
   *
   * @throws IOException ako se dogodi greška pri čitanju datoteke.
   */
  public void ucitajLokacije() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaLokacija");
    var citac = new CitanjeLokacija();
    this.lokacije = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String id : this.lokacije.keySet()) {
        var l = this.lokacije.get(id);
        Logger.getGlobal().log(Level.INFO, "Lokacija: " + l.naziv() + " " + l.id());
      }
    }
  }

  /**
   * Učitava uređaje iz datoteke uređaja i sprema ih u mapu uređaja. Ispisuje informacije o
   * uređajima ako je ispis postavljen na 1.
   *
   * @throws IOException ako se dogodi greška pri čitanju datoteke.
   */
  public void ucitajUredaje() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaUredaja");
    var citac = new CitanjeUredaja();
    this.uredaji = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String id : this.uredaji.keySet()) {
        var u = this.uredaji.get(id);
        Logger.getGlobal().log(Level.INFO, "Uredaj: " + u.naziv() + " " + u.id());
      }
    }
  }

}
