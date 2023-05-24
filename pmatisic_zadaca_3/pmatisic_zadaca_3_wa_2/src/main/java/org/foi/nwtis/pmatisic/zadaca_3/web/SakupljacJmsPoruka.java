package org.foi.nwtis.pmatisic.zadaca_3.web;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Singleton;

/**
 * Singleton klasa koja sakuplja JMS poruke.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@Singleton
public class SakupljacJmsPoruka {
  private List<String> poruke;

  /**
   * Konstruktor klase. Inicijalizira praznu listu poruka.
   */
  public SakupljacJmsPoruka() {
    poruke = new ArrayList<>();
  }

  /**
   * Dohvaća listu svih sakupljenih JMS poruka.
   *
   * @return Lista sakupljenih JMS poruka
   */
  public List<String> dohvatiPoruke() {
    return this.poruke;
  }

  /**
   * Dodaje novu JMS poruku u listu.
   *
   * @param poruka Nova JMS poruka
   */
  public void spremiPoruku(String poruka) {
    this.poruke.add(poruka);
  }
}
