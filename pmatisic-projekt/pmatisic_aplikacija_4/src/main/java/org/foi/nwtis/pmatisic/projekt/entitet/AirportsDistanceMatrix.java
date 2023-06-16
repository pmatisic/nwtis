package org.foi.nwtis.pmatisic.projekt.entitet;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * The persistent class for the AIRPORTS_DISTANCE_MATRIX database table.
 * 
 */
@Entity
@Table(name = "AIRPORTS_DISTANCE_MATRIX")
@NamedQuery(name = "AirportsDistanceMatrix.findAll",
    query = "SELECT a FROM AirportsDistanceMatrix a")
public class AirportsDistanceMatrix implements Serializable {
  private static final long serialVersionUID = 1L;

  @EmbeddedId
  private AirportsDistanceMatrixPK id;

  @Column(name = "DIST_CTRY")
  private float distCtry;

  @Column(name = "DIST_TOT")
  private float distTot;

  @Column(name = "NOTE", length = 30)
  private String note;

  // bi-directional many-to-one association to Airport
  @ManyToOne
  @JoinColumn(name = "ICAO_FROM", nullable = false, insertable = false, updatable = true)
  private Airports airport1;

  // bi-directional many-to-one association to Airport
  @ManyToOne
  @JoinColumn(name = "ICAO_TO", nullable = false, insertable = false, updatable = true)
  private Airports airport2;

  public AirportsDistanceMatrix() {}

  public AirportsDistanceMatrixPK getId() {
    return this.id;
  }

  public void setId(AirportsDistanceMatrixPK id) {
    this.id = id;
  }

  public float getDistCtry() {
    return this.distCtry;
  }

  public void setDistCtry(float distCtry) {
    this.distCtry = distCtry;
  }

  public float getDistTot() {
    return this.distTot;
  }

  public void setDistTot(float distTot) {
    this.distTot = distTot;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Airports getAirport1() {
    return this.airport1;
  }

  public void setAirport1(Airports airport1) {
    this.airport1 = airport1;
  }

  public Airports getAirport2() {
    return this.airport2;
  }

  public void setAirport2(Airports airport2) {
    this.airport2 = airport2;
  }

}
