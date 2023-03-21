package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeKorisnika;

/**
 * Klasa GlavniPosluzitelj koja je zadužena za otvaranje veze na određenim mrežnim vratima/portu.
 * 
 * @author Petar M.
 *
 */
public class GlavniPosluzitelj {

  protected Konfiguracija konf;

  protected Map<String, Korisnik> korisnici;
  protected Map<String, Lokacija> lokacije;
  protected Map<String, Uredaj> uredaji;
  private int ispis = 0;
  private int mreznaVrata = 8000;
  private int brojCekaca = 10;

  private boolean kraj = false;

  public GlavniPosluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  public void pokreniPosluzitelja() {
    try {
      ucitajKorisnika();
      otvoriMreznaVrata();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }

  }


  /**
   * Učitava sve korisnike iz CSV datoteke koja je definirana u postavci datotekaKorisnika.
   * 
   * @throws IOException - baca iznimku ako je problem s učitavanjem
   */
  public void ucitajKorisnika() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaKorisnika");
    var citacKorisnika = new CitanjeKorisnika();
    this.korisnici = citacKorisnika.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String korime : this.korisnici.keySet()) {
        var korisnik = this.korisnici.get(korime);
        Logger.getGlobal().log(Level.INFO,
            "Korisnik: " + korisnik.prezime() + " " + korisnik.ime());
      }
    }
  }

  public void otvoriMreznaVrata() {
    try (var posluzitelj = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        var uticnica = posluzitelj.accept();
        var dretva = new MrezniRadnik(uticnica, konf);
        dretva.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}