package org.foi.nwtis.pmatisic.projekt.podatak;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Aerodrom {

  @Getter
  @Setter
  private String icao;
  @Getter
  @Setter
  private String naziv;
  @Getter
  @Setter
  private String drzava;
  @Getter
  @Setter
  private Lokacija lokacija;

  public Aerodrom() {}
}
