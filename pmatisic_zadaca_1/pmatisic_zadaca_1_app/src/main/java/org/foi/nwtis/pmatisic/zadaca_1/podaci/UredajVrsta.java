package org.foi.nwtis.pmatisic.zadaca_1.podaci;

/**
 * Enumeracija UredajVrsta.
 */
public enum UredajVrsta {

  /** Senzor temperatura. */
  SenzorTemperatura(1),
  /** Senzor vlaga. */
  SenzorVlaga(2),
  /** Senzor tlak. */
  SenzorTlak(3),
  /** Senzor svjetlo. */
  SenzorSvjetlo(4),
  /** Senzor kontakt. */
  SenzorKontakt(5),
  /** Senzor temperatura vlaga. */
  SenzorTemperaturaVlaga(50),
  /** Senzor temperatura tlak. */
  SenzorTemperaturaTlak(51),
  /** Senzor temperatura vlaga tlak. */
  SenzorTemperaturaVlagaTlak(52),
  /** Aktuator ventilator. */
  AktuatorVentilator(100),
  /** Aktuator grijanje. */
  AktuatorGrijanje(101),
  /** Aktuator rasvjeta. */
  AktuatorRasvjeta(102),
  /** Aktuator vrata. */
  AktuatorVrata(103);

  /** broj. */
  private int broj;

  /**
   * Instancira novu uredaj vrstu.
   *
   * @param broj broj
   */
  private UredajVrsta(int broj) {
    this.broj = broj;
  }

  /**
   * Dohvaća vrstu preko broja
   *
   * @param broj broj
   * @return uredaj vrsta
   */
  public static UredajVrsta odBroja(int broj) {
    for (UredajVrsta uv : UredajVrsta.values()) {
      if (uv.broj == broj) {
        return uv;
      }
    }
    throw new IllegalArgumentException("Ne postoji vrsta za vrijednost: " + broj + ".");
  }
}
