package org.foi.nwtis.pmatisic.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class KonfiguracijaBin extends KonfiguracijaApstraktna {

  public static final String TIP = "bin";

  public KonfiguracijaBin(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    // TODO Auto-generated method stub

  }

  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    // TODO Auto-generated method stub

  }

}
