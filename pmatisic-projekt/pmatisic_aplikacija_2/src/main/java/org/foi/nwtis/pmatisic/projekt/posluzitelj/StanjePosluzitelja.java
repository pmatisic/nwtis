package org.foi.nwtis.pmatisic.projekt.posluzitelj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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

    try (Socket socket = new Socket(adresaPosluzitelja, mreznaVrataPosluzitelja);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

      String response = in.readLine();

      if ("OK 1".equals(response)) {
        return Status.AKTIVAN;
      } else if ("OK 0".equals(response)) {
        return Status.PAUZA;
      } else {
        throw new IOException("Neispravan odgovor od poslužitelja: " + response);
      }

    } catch (IOException e) {
      throw new RuntimeException("Pogreška pri provjeri statusa poslužitelja", e);
    }

  }

}
