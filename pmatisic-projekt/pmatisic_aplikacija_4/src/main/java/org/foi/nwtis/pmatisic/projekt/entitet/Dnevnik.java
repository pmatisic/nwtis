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

@Entity
@Table(name = "DNEVNIK")
@NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d")
public class Dnevnik implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "vrsta", length = 255)
  private String vrsta;

  @Column(name = "vrijeme_pristupa")
  private Timestamp vrijemePristupa;

  @Column(name = "putanja", length = 1024)
  private String putanja;

  @Column(name = "ip_adresa", length = 255)
  private String ipAdresa;

  @ManyToOne
  @JoinColumn(name = "korisnik", referencedColumnName = "id")
  private Korisnici korisnik;

  public Dnevnik() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getVrsta() {
    return vrsta;
  }

  public void setVrsta(String vrsta) {
    this.vrsta = vrsta;
  }

  public Timestamp getVrijemePristupa() {
    return vrijemePristupa;
  }

  public void setVrijemePristupa(Timestamp vrijemePristupa) {
    this.vrijemePristupa = vrijemePristupa;
  }

  public String getPutanja() {
    return putanja;
  }

  public void setPutanja(String putanja) {
    this.putanja = putanja;
  }

  public Korisnici getKorisnik() {
    return korisnik;
  }

  public void setKorisnik(Korisnici korisnik) {
    this.korisnik = korisnik;
  }

  public String getIpAdresa() {
    return ipAdresa;
  }

  public void setIpAdresa(String ipAdresa) {
    this.ipAdresa = ipAdresa;
  }

}
