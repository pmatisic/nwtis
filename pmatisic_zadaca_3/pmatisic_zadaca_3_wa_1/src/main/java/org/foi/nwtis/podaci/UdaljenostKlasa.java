package org.foi.nwtis.podaci;

/**
 * @author Petar Matišić (pmatisic@foi.hr)
 */
public class UdaljenostKlasa {

  private String drzava;
  private float km;

  public UdaljenostKlasa(String drzava, float km) {
    super();
    this.drzava = drzava;
    this.km = km;
  }

  public String getDrzava() {
    return drzava;
  }

  public void setDrzava(String drzava) {
    this.drzava = drzava;
  }

  public float getKm() {
    return km;
  }

  public void setKm(float km) {
    this.km = km;
  }

}
