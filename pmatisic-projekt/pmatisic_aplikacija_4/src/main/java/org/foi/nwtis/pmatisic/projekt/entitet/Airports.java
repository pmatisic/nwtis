package org.foi.nwtis.pmatisic.projekt.entitet;

import java.io.Serializable;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the AIRPORTS database table.
 * 
 */
@Entity
@Table(name = "AIRPORTS")
@NamedQuery(name = "Airports.findAll", query = "SELECT a FROM Airports a")
public class Airports implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ICAO", unique = true, nullable = false, length = 10)
  private String icao;

  @Column(name = "CONTINENT", length = 30)
  private String continent;

  @Column(name = "COORDINATES", nullable = false, length = 30)
  private String coordinates;

  @Column(name = "ELEVATION_FT", length = 10)
  private String elevationFt;

  @Column(name = "GPS_CODE", nullable = false, length = 10)
  private String gpsCode;

  @Column(name = "IATA_CODE", nullable = false, length = 10)
  private String iataCode;

  @Column(name = "ISO_COUNTRY", length = 30)
  private String isoCountry;

  @Column(name = "ISO_REGION", length = 10)
  private String isoRegion;

  @Column(name = "LOCAL_CODE", nullable = false, length = 10)
  private String localCode;

  @Column(name = "MUNICIPALITY", length = 30)
  private String municipality;

  @Column(name = "NAME", nullable = false, length = 255)
  private String name;

  @Column(name = "TYPE", nullable = false, length = 30)
  private String type;

  // bi-directional many-to-one association to AirportsDistanceMatrix
  @OneToMany(mappedBy = "airport1")
  private List<AirportsDistanceMatrix> airportsDistanceMatrixs1;

  // bi-directional many-to-one association to AirportsDistanceMatrix
  @OneToMany(mappedBy = "airport2")
  private List<AirportsDistanceMatrix> airportsDistanceMatrixs2;

  // bi-directional many-to-one association to LetoviPolasci
  @OneToMany(mappedBy = "airport")
  private List<LetoviPolasci> letoviPolascis;

  public Airports() {}

  public String getIcao() {
    return this.icao;
  }

  public void setIcao(String icao) {
    this.icao = icao;
  }

  public String getContinent() {
    return this.continent;
  }

  public void setContinent(String continent) {
    this.continent = continent;
  }

  public String getCoordinates() {
    return this.coordinates;
  }

  public void setCoordinates(String coordinates) {
    this.coordinates = coordinates;
  }

  public String getElevationFt() {
    return this.elevationFt;
  }

  public void setElevationFt(String elevationFt) {
    this.elevationFt = elevationFt;
  }

  public String getGpsCode() {
    return this.gpsCode;
  }

  public void setGpsCode(String gpsCode) {
    this.gpsCode = gpsCode;
  }

  public String getIataCode() {
    return this.iataCode;
  }

  public void setIataCode(String iataCode) {
    this.iataCode = iataCode;
  }

  public String getIsoCountry() {
    return this.isoCountry;
  }

  public void setIsoCountry(String isoCountry) {
    this.isoCountry = isoCountry;
  }

  public String getIsoRegion() {
    return this.isoRegion;
  }

  public void setIsoRegion(String isoRegion) {
    this.isoRegion = isoRegion;
  }

  public String getLocalCode() {
    return this.localCode;
  }

  public void setLocalCode(String localCode) {
    this.localCode = localCode;
  }

  public String getMunicipality() {
    return this.municipality;
  }

  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<AirportsDistanceMatrix> getAirportsDistanceMatrixs1() {
    return this.airportsDistanceMatrixs1;
  }

  public void setAirportsDistanceMatrixs1(List<AirportsDistanceMatrix> airportsDistanceMatrixs1) {
    this.airportsDistanceMatrixs1 = airportsDistanceMatrixs1;
  }

  public AirportsDistanceMatrix addAirportsDistanceMatrixs1(
      AirportsDistanceMatrix airportsDistanceMatrixs1) {
    getAirportsDistanceMatrixs1().add(airportsDistanceMatrixs1);
    airportsDistanceMatrixs1.setAirport1(this);

    return airportsDistanceMatrixs1;
  }

  public AirportsDistanceMatrix removeAirportsDistanceMatrixs1(
      AirportsDistanceMatrix airportsDistanceMatrixs1) {
    getAirportsDistanceMatrixs1().remove(airportsDistanceMatrixs1);
    airportsDistanceMatrixs1.setAirport1(null);

    return airportsDistanceMatrixs1;
  }

  public List<AirportsDistanceMatrix> getAirportsDistanceMatrixs2() {
    return this.airportsDistanceMatrixs2;
  }

  public void setAirportsDistanceMatrixs2(List<AirportsDistanceMatrix> airportsDistanceMatrixs2) {
    this.airportsDistanceMatrixs2 = airportsDistanceMatrixs2;
  }

  public AirportsDistanceMatrix addAirportsDistanceMatrixs2(
      AirportsDistanceMatrix airportsDistanceMatrixs2) {
    getAirportsDistanceMatrixs2().add(airportsDistanceMatrixs2);
    airportsDistanceMatrixs2.setAirport2(this);

    return airportsDistanceMatrixs2;
  }

  public AirportsDistanceMatrix removeAirportsDistanceMatrixs2(
      AirportsDistanceMatrix airportsDistanceMatrixs2) {
    getAirportsDistanceMatrixs2().remove(airportsDistanceMatrixs2);
    airportsDistanceMatrixs2.setAirport2(null);

    return airportsDistanceMatrixs2;
  }

  public List<LetoviPolasci> getLetoviPolascis() {
    return this.letoviPolascis;
  }

  public void setLetoviPolascis(List<LetoviPolasci> letoviPolascis) {
    this.letoviPolascis = letoviPolascis;
  }

  public LetoviPolasci addLetoviPolasci(LetoviPolasci letoviPolasci) {
    getLetoviPolascis().add(letoviPolasci);
    letoviPolasci.setAirport(this);

    return letoviPolasci;
  }

  public LetoviPolasci removeLetoviPolasci(LetoviPolasci letoviPolasci) {
    getLetoviPolascis().remove(letoviPolasci);
    letoviPolasci.setAirport(null);

    return letoviPolasci;
  }

}
