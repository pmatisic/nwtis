package org.foi.nwtis.pmatisic.projekt.podatak;

public class AerodromSaStatusom extends Aerodrom {

  private boolean preuzimanjeAktivno;

  public AerodromSaStatusom(String icao, String naziv, String drzava, Lokacija lokacija,
      boolean preuzimanjeAktivno) {
    super(icao, naziv, drzava, lokacija);
    this.preuzimanjeAktivno = preuzimanjeAktivno;
  }

  public boolean isPreuzimanjeAktivno() {
    return preuzimanjeAktivno;
  }

  public void setPreuzimanjeAktivno(boolean preuzimanjeAktivno) {
    this.preuzimanjeAktivno = preuzimanjeAktivno;
  }

}
