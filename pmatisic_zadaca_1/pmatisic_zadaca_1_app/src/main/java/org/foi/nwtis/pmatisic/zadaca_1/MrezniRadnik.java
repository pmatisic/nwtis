package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;

public class MrezniRadnik extends Thread {

  protected Socket mreznaUticnica;
  protected Konfiguracija konfig;
  private int ispis = 0;

  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
    this.ispis = Integer.parseInt(this.konfig.dajPostavku("ispis"));
  }

  @Override
  public synchronized void start() {
    // OVDJE RADI SVOJE
    super.start();
  }

  @Override
  public void run() {
    try {
      var citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;

        if (this.ispis == 1) {
          Logger.getGlobal().log(Level.INFO, red);
        }

        poruka.append(red);
      }
      this.mreznaUticnica.shutdownInput();
      String odgovor = this.obradiZahtjev(poruka.toString());

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private String obradiZahtjev(String string) {
    return "OK";
  }

  @Override
  public void interrupt() {
    // OVDJE RADI SVOJE
    super.interrupt();
  }

}
