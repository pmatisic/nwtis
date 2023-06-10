package org.foi.nwtis.pmatisic.projekt.entitet;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "AERODROMI_LETOVI")
@NamedQuery(name = "AerodromiLetovi.findAll", query = "SELECT a FROM AerodromiLetovi a")
public class AerodromiLetovi implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ICAO", unique = true, nullable = false, length = 255)
  private String icao;

  @Column(name = "AKTIVAN")
  private boolean aktivan;

  @OneToOne
  @JoinColumn(name = "ICAO", referencedColumnName = "ICAO", insertable = false, updatable = false)
  private Airports airports;

  public AerodromiLetovi() {}

  public String getIcao() {
    return this.icao;
  }

  public void setIcao(String icao) {
    this.icao = icao;
  }

  public boolean isAktivan() {
    return this.aktivan;
  }

  public void setAktivan(boolean aktivan) {
    this.aktivan = aktivan;
  }

  public Airports getAirports() {
    return this.airports;
  }

  public void setAirports(Airports airports) {
    this.airports = airports;
  }
}
