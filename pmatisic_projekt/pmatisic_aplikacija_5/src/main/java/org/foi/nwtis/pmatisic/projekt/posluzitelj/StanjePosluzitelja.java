package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;

public class StanjePosluzitelja {

  private Konfiguracija konfiguracija;

  public StanjePosluzitelja(Konfiguracija konfiguracija) {
    this.konfiguracija = konfiguracija;
  }

  public Status provjeriStatusPosluzitelja() {
    String adresaPosluzitelja = (konfiguracija.dajPostavku("adresa.posluzitelja")).toString();
    Integer mreznaVrataPosluzitelja =
        Integer.parseInt(konfiguracija.dajPostavku("mreznaVrata.posluzitelja"));

    try (var socket = new Socket(adresaPosluzitelja, mreznaVrataPosluzitelja);
        var citac = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        var pisac = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));) {

      String komanda = "STATUS";
      pisac.write(komanda);
      pisac.flush();
      socket.shutdownOutput();
      String response = citac.readLine();
      socket.shutdownInput();

      switch (response) {
        case "OK 1": {
          return Status.AKTIVAN;
        }
        case "OK 0": {
          return Status.PAUZA;
        }
        default:
          throw new IOException("Neispravan odgovor od poslužitelja: " + response);
      }

    } catch (IOException e) {
      throw new RuntimeException("Pogreška pri provjeri statusa poslužitelja", e);
    }

  }

}
