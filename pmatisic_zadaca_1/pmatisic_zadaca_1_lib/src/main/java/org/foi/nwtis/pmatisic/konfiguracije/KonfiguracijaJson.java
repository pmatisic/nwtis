package org.foi.nwtis.pmatisic.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * The Class KonfiguracijaJson.
 */
public class KonfiguracijaJson extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "json";

  /**
   * Instantiates a new konfiguracija json.
   *
   * @param nazivDatoteke the naziv datoteke
   */
  public KonfiguracijaJson(String nazivDatoteke) {
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
