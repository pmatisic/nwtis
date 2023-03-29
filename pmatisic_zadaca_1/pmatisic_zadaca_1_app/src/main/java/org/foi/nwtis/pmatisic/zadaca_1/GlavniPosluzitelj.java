package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeKorisnika;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeLokacija;
import org.foi.nwtis.pmatisic.zadaca_1.pomocnici.CitanjeUredaja;

public class GlavniPosluzitelj {

  protected Konfiguracija konf;
  protected int brojRadnika;
  protected int maksVrijemeNeaktivnosti;
  protected Map<String, Korisnik> korisnici;
  protected Map<String, Lokacija> lokacije;
  protected Map<String, Uredaj> uredaji;
  private int ispis = 0;
  private int mreznaVrata = 8000;
  private int brojCekaca = 10;
  private boolean kraj = false;

  public GlavniPosluzitelj(Konfiguracija konf) {
    this.konf = konf;
    this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
    // this.maksVrijemeNeaktivnosti = Integer.parseInt(konf.dajPostavku("maksVrijemeNeaktivnosti"));
    this.ispis = Integer.parseInt(konf.dajPostavku("ispis"));
    this.mreznaVrata = Integer.parseInt(konf.dajPostavku("mreznaVrata"));
    this.brojCekaca = Integer.parseInt(konf.dajPostavku("brojCekaca"));
  }

  public void pokreniPosluzitelja() {
    try {
      this.ucitajKorisnike();
      this.ucitajLokacije();
      this.ucitajUredaje();
      this.pripremiPosluzitelja();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
    }
  }

  public void ucitajKorisnike() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaKorisnika");
    var citac = new CitanjeKorisnika();
    this.korisnici = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String korime : this.korisnici.keySet()) {
        var k = this.korisnici.get(korime);
        Logger.getGlobal().log(Level.INFO, "Korisnik: " + k.prezime() + " " + k.ime());
      }
    }
  }

  public void ucitajLokacije() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaLokacija");
    var citac = new CitanjeLokacija();
    this.lokacije = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String id : this.lokacije.keySet()) {
        var l = this.lokacije.get(id);
        Logger.getGlobal().log(Level.INFO, "Lokacija: " + l.naziv() + " " + l.id());
      }
    }
  }

  public void ucitajUredaje() throws IOException {
    var nazivDatoteke = this.konf.dajPostavku("datotekaUredaja");
    var citac = new CitanjeUredaja();
    this.uredaji = citac.ucitajDatoteku(nazivDatoteke);
    if (this.ispis == 1) {
      for (String id : this.uredaji.keySet()) {
        var u = this.uredaji.get(id);
        Logger.getGlobal().log(Level.INFO, "Uredaj: " + u.naziv() + " " + u.id());
      }
    }
  }

  // TODO doraditi za dretve
  public void pripremiPosluzitelja() {
    try (ServerSocket ss = new ServerSocket(this.mreznaVrata, this.brojCekaca)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        MrezniRadnik mr = new MrezniRadnik(veza, konf);
        mr.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
