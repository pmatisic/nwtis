package org.foi.nwtis.pmatisic.projekt.entitet;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "KORISNICI")
@NamedQuery(name = "Korisnik.findAll", query = "SELECT k FROM Korisnik k")
public class Korisnik implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "korime", nullable = false, length = 255)
  private String korime;

  @Column(name = "lozinka", nullable = false, length = 255)
  private String lozinka;

  @Column(name = "ime", nullable = false, length = 255)
  private String ime;

  @Column(name = "prezime", nullable = false, length = 255)
  private String prezime;

  public Korisnik() {}

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getKorime() {
    return korime;
  }

  public void setKorime(String korime) {
    this.korime = korime;
  }

  public String getLozinka() {
    return lozinka;
  }

  public void setLozinka(String lozinka) {
    this.lozinka = lozinka;
  }

  public String getIme() {
    return ime;
  }

  public void setIme(String ime) {
    this.ime = ime;
  }

  public String getPrezime() {
    return prezime;
  }

  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

}
