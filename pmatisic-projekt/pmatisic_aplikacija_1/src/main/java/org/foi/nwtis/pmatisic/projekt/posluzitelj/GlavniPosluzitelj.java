package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.dretva.MrezniRadnik;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.pomocnik.CitanjeLokacija;

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

  /** Mapa koja sadrži lokacije s njihovim identifikatorima kao ključevima. */
  public Map<String, Lokacija> lokacije;

  /** Brojač za dretve koje se koriste za obradu zahtjeva. */
  private int dretva = 0;

  /** Oznaka za ispis dodatnih informacija. */
  private int ispis = 0;

  /** Mrežna vrata na kojima će poslužitelj slušati dolazne veze. */
  private int mreznaVrata = 8000;

  /** Broj čekača koji će se koristiti za spajanje na poslužitelj. */
  private int brojCekaca = 10;

  /** Zastavica za zaustavljanje poslužitelja. */
  public boolean kraj = false;

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
    if (jestSlobodan()) {
      try {
        this.ucitajLokacije();
      } catch (IOException e) {
        e.printStackTrace();
      }
      this.pripremiPosluzitelja();
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

}
