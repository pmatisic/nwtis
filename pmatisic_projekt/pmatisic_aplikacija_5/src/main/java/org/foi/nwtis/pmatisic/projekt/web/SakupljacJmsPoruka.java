package org.foi.nwtis.pmatisic.projekt.web;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Singleton;

@Singleton
public class SakupljacJmsPoruka {
  private List<String> poruke;

  public SakupljacJmsPoruka() {
    poruke = new ArrayList<>();
  }

  public List<String> dohvatiPoruke(int odBroja, int broj) {
    int pocetak = (odBroja - 1) * broj;
    int kraj = Math.min(pocetak + broj, poruke.size());

    if (pocetak < poruke.size()) {
      return this.poruke.subList(pocetak, kraj);
    } else {
      return new ArrayList<>();
    }
  }

  public void spremiPoruku(String poruka) {
    this.poruke.add(poruka);
  }
}
