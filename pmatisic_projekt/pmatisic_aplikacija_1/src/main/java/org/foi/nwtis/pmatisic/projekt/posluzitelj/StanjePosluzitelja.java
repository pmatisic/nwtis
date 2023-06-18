package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import org.foi.nwtis.pmatisic.projekt.podatak.Status;

public class StanjePosluzitelja {
  private Status status;
  private int brojacZahtjeva;
  private boolean ispisKomandi;

  public StanjePosluzitelja() {
    this.status = Status.PAUZA;
    this.brojacZahtjeva = 0;
    this.ispisKomandi = false;
  }

  public synchronized void promijeniStatus(Status status) {
    this.status = status;
    if (status == Status.AKTIVAN) {
      this.brojacZahtjeva = 0;
    }
  }

  public synchronized void inkrementirajBrojacZahtjeva() {
    this.brojacZahtjeva++;
  }

  public synchronized void postaviIspisKomandi(boolean ispis) {
    this.ispisKomandi = ispis;
  }

  public synchronized Status dajStatus() {
    return this.status;
  }

  public synchronized int dajBrojacZahtjeva() {
    return this.brojacZahtjeva;
  }

  public synchronized boolean dajIspisKomandi() {
    return this.ispisKomandi;
  }
}

