package org.foi.nwtis.pmatisic.zadaca_3.web;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Singleton;

@Singleton
public class SakupljacJmsPoruka {
  private List<String> poruke;

  public SakupljacJmsPoruka() {
    poruke = new ArrayList<>();
  }

  public List<String> dohvatiPoruke() {
    return this.poruke;
  }

  public void spremiPoruku(String poruka) {
    this.poruke.add(poruka);
  }
}
