package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.dretva.Dretva;

/**
 * Klasa Posluzitelj služi za upravljanje i pokretanje glavnog poslužitelja.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class Posluzitelj {

  public boolean kraj = false;
  private int brojCekaca = 10;
  private int mreznaVrata = 8000;
  protected Konfiguracija konf;
  protected int brojRadnika;
  protected int maksVrijemeNeaktivnosti;

  /**
   * Instancira glavni poslužitelj s konfiguracijskim objektom.
   *
   * @param konf Konfiguracijski objekt koji sadrži postavke poslužitelja
   */
  public Posluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  /**
   * Pokreće poslužitelja, te priprema poslužitelj za obradu zahtjeva.
   */
  public void pokreniPosluzitelja() {
    if (jestSlobodan()) {
      this.pripremiPosluzitelja();
    }
  }

  /**
   * Provjerava je li mrežna vrata (port) slobodna za korištenje.
   *
   * @return true ukoliko uspostavi vezu, false inače
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
   * Priprema poslužitelj za obradu zahtjeva stvaranjem komunikacije i pokretanjem dretve.
   */
  public void pripremiPosluzitelja() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        Dretva d = new Dretva(veza, konf, this);
        d.start();
        Logger.getGlobal().log(Level.INFO, "Dretva je pokrenuta!");
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u stvaranju veze! " + e.getMessage());
    }
  }

}
