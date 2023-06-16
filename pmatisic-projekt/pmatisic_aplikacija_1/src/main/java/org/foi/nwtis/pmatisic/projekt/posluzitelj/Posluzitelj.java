package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.dretva.Dretva;

public class Posluzitelj {

  private StanjePosluzitelja stanje;
  public boolean kraj = false;
  private int brojCekaca = 0;
  private int brojRadnika = 0;
  private int mreznaVrata = 0;
  protected Konfiguracija konf;

  public Posluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
    this.stanje = new StanjePosluzitelja();
  }

  public void pokreniPosluzitelja() {
    if (jestSlobodan()) {
      this.pripremiPosluzitelja();
    }
  }

  public boolean jestSlobodan() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata)) {
      return true;
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u spajanju na mrežna vrata! " + e.getMessage());
      return false;
    }
  }

  public void pripremiPosluzitelja() {
    ExecutorService executor = Executors.newFixedThreadPool(this.brojRadnika);
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        Dretva d = new Dretva(veza, konf, this, stanje);
        executor.execute(d);
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška u stvaranju veze! " + e.getMessage());
    } finally {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException ex) {
        executor.shutdownNow();
      }
    }
  }

  public void ugasi() {
    for (Thread t : Thread.getAllStackTraces().keySet()) {
      if (t instanceof Dretva && t.isAlive()) {
        t.interrupt();
      }
    }
    this.kraj = true;
  }

}
