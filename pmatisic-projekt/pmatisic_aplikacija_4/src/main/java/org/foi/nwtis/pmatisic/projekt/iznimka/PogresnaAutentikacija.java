package org.foi.nwtis.pmatisic.projekt.iznimka;

public class PogresnaAutentikacija extends Exception {

  private static final long serialVersionUID = 1L;

  public PogresnaAutentikacija(String poruka) {
    super(poruka);
  }
}
