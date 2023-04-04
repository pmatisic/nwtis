package org.foi.nwtis.pmatisic.vjezba_04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.pmatisic.vjezba_04.podaci.SystemInfo;
import com.google.gson.Gson;

public class Vjezba_04_1 {

  private SystemInfo systeminfo;

  public static void main(String[] args) {
    var vjezba = new Vjezba_04_1();
    if (!vjezba.provjeriArgumente(args)) {
      Logger.getGlobal().log(Level.SEVERE, "Krivi argumenti!");
      return;
    }

    vjezba.systeminfo = new SystemInfo();
    vjezba.ispisiSystemInfo();
    try {
      vjezba.spremiSystemInfo(args[0]);
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška kod učitavaja!" + e.getMessage());
    }
  }

  private boolean provjeriArgumente(String[] args) {
    return args.length == 1 ? true : false;
  }

  private void ispisiSystemInfo() {
    Logger.getGlobal().log(Level.INFO, "OS: " + this.systeminfo.getNazivOS());
    Logger.getGlobal().log(Level.INFO, "Proizvođač: " + this.systeminfo.getProizvodacVM());
    Logger.getGlobal().log(Level.INFO, "Verzija: " + this.systeminfo.getVerzijaVM());
    Logger.getGlobal().log(Level.INFO, "VM dir: " + this.systeminfo.getDirektorijVM());
    Logger.getGlobal().log(Level.INFO, "Temp dir: " + this.systeminfo.getDirektorijTemp());
    Logger.getGlobal().log(Level.INFO, "User dir: " + this.systeminfo.getDirektorijKorisnik());
  }

  private void spremiSystemInfo(String nazivDatoteke) throws IOException {
    var datoteka = Path.of(nazivDatoteke);
    var pisac = Files.newBufferedWriter(datoteka);
    var gson = new Gson();
    var json = gson.toJson(this.systeminfo);
    Logger.getGlobal().log(Level.INFO, json);
    pisac.write(json);
    pisac.close();
  }
}
