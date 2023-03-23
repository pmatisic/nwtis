package org.foi.nwtis.pmatisic.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * The Class KonfiguracijaYaml.
 */
public class KonfiguracijaYaml extends KonfiguracijaApstraktna {

  /** The Constant TIP. */
  public static final String TIP = "yaml";

  /**
   * Instantiates a new konfiguracija yaml.
   *
   * @param nazivDatoteke the naziv datoteke
   */
  public KonfiguracijaYaml(String nazivDatoteke) {
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
