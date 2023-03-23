package org.foi.nwtis.pmatisic.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * The Class KonfiguracijaBin.
 */
public class KonfiguracijaBin extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "bin";

  /**
   * Instantiates a new konfiguracija bin.
   * 
   *
   * @param nazivDatoteke the naziv datoteke
   */
  public KonfiguracijaBin(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  /**
   * Spremi konfiguraciju.
   *
   * @param datoteka the datoteka
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   */
  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {

  }

  /**
   * Ucitaj konfiguraciju.
   *
   * @throws NeispravnaKonfiguracija the neispravna konfiguracija
   */
  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {

  }

}
