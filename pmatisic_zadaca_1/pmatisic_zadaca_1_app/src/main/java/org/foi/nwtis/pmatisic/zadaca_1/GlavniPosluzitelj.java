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
 * Klasa GlavniPosluzitelj.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class GlavniPosluzitelj {

  /** konf. */
  protected Konfiguracija konf;

  /** broj radnika. */
  protected int brojRadnika;

  /** maks vrijeme neaktivnosti. */
  protected int maksVrijemeNeaktivnosti;

  /** korisnici. */
  protected Map<String, Korisnik> korisnici;

  /** lokacije. */
  protected Map<String, Lokacija> lokacije;

  /** uređaji. */
  protected Map<String, Uredaj> uredaji;

  /** dretva. */
  private int dretva = 0;

  /** ispis. */
  private int ispis = 0;

  /** mrežna vrata. */
  private int mreznaVrata = 8000;

  /** broj čekača. */
  private int brojCekaca = 10;

  /** kraj. */
  protected boolean kraj = false;

  /**
   * Instancira glavni poslužitelj.
   *
   * @param konf konf
   */
  public GlavniPosluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  /**
   * Pokreće poslužitelja.
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
   * Provjera je li mrežna vrata/port slobodan.
   *
   * @return istina, ako je uspješno
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
   * Stvaranje komunikacije i pripremanje poslužitelja.
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
   * Učitava korisnike.
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
   * Učitava lokacije.
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
   * Učitava uređaje.
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
