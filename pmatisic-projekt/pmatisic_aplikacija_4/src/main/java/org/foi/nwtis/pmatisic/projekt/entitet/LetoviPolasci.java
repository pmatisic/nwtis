package org.foi.nwtis.pmatisic.projekt.entitet;

import java.io.Serializable;
import java.sql.Timestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * The persistent class for the LETOVI_POLASCI database table.
 * 
 */
@Entity
@Table(name = "LETOVI_POLASCI")
@NamedQuery(name = "LetoviPolasci.findAll", query = "SELECT l FROM LetoviPolasci l")
public class LetoviPolasci implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private int arrivalAirportCandidatesCount;

  @Column(length = 20)
  private String callsign;

  @Column(nullable = false)
  private int departureAirportCandidatesCount;

  @Column(nullable = false, length = 10)
  private String estArrivalAirport;

  @Column(nullable = false)
  private int estArrivalAirportHorizDistance;

  @Column(nullable = false)
  private int estArrivalAirportVertDistance;

  @Column(nullable = false)
  private int estDepartureAirportHorizDistance;

  @Column(nullable = false)
  private int estDepartureAirportVertDistance;

  @Column(nullable = false)
  private int firstSeen;

  @Column(nullable = false, length = 30)
  private String icao24;

  @Column(nullable = false)
  private int lastSeen;

  @Column(nullable = false)
  private Timestamp stored;

  // bi-directional many-to-one association to Airport
  @ManyToOne
  @JoinColumn(name = "estDepartureAirport", nullable = false)
  private Airports airport;

  public LetoviPolasci() {}

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getArrivalAirportCandidatesCount() {
    return this.arrivalAirportCandidatesCount;
  }

  public void setArrivalAirportCandidatesCount(int arrivalAirportCandidatesCount) {
    this.arrivalAirportCandidatesCount = arrivalAirportCandidatesCount;
  }

  public String getCallsign() {
    return this.callsign;
  }

  public void setCallsign(String callsign) {
    this.callsign = callsign;
  }

  public int getDepartureAirportCandidatesCount() {
    return this.departureAirportCandidatesCount;
  }

  public void setDepartureAirportCandidatesCount(int departureAirportCandidatesCount) {
    this.departureAirportCandidatesCount = departureAirportCandidatesCount;
  }

  public String getEstArrivalAirport() {
    return this.estArrivalAirport;
  }

  public void setEstArrivalAirport(String estArrivalAirport) {
    this.estArrivalAirport = estArrivalAirport;
  }

  public int getEstArrivalAirportHorizDistance() {
    return this.estArrivalAirportHorizDistance;
  }

  public void setEstArrivalAirportHorizDistance(int estArrivalAirportHorizDistance) {
    this.estArrivalAirportHorizDistance = estArrivalAirportHorizDistance;
  }

  public int getEstArrivalAirportVertDistance() {
    return this.estArrivalAirportVertDistance;
  }

  public void setEstArrivalAirportVertDistance(int estArrivalAirportVertDistance) {
    this.estArrivalAirportVertDistance = estArrivalAirportVertDistance;
  }

  public int getEstDepartureAirportHorizDistance() {
    return this.estDepartureAirportHorizDistance;
  }

  public void setEstDepartureAirportHorizDistance(int estDepartureAirportHorizDistance) {
    this.estDepartureAirportHorizDistance = estDepartureAirportHorizDistance;
  }

  public int getEstDepartureAirportVertDistance() {
    return this.estDepartureAirportVertDistance;
  }

  public void setEstDepartureAirportVertDistance(int estDepartureAirportVertDistance) {
    this.estDepartureAirportVertDistance = estDepartureAirportVertDistance;
  }

  public int getFirstSeen() {
    return this.firstSeen;
  }

  public void setFirstSeen(int firstSeen) {
    this.firstSeen = firstSeen;
  }

  public String getIcao24() {
    return this.icao24;
  }

  public void setIcao24(String icao24) {
    this.icao24 = icao24;
  }

  public int getLastSeen() {
    return this.lastSeen;
  }

  public void setLastSeen(int lastSeen) {
    this.lastSeen = lastSeen;
  }

  public Timestamp getStored() {
    return this.stored;
  }

  public void setStored(Timestamp stored) {
    this.stored = stored;
  }

  public Airports getAirport() {
    return this.airport;
  }

  public void setAirport(Airports airport) {
    this.airport = airport;
  }

}
