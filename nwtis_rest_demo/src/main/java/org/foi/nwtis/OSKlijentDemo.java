package org.foi.nwtis;

import java.util.List;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;

/**
 *
 * @author dkermek
 */
public class OSKlijentDemo {

  /**
   * @param args argumenti komandne linije
   */
  public static void main(String[] args) {
    if (args.length != 5) {
      System.out.println("Broj argumenata ne ogdovara (5).");
      return;
    }
    String korisnik = args[0];
    String lozinka = args[1];
    String icao = args[2];
    int odVremena = Integer.parseInt(args[3]);
    int doVremena = Integer.parseInt(args[4]);
    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);
    System.out.println("Polasci s aerodroma: " + icao);
    List<LetAviona> avioniPolasci;
    try {
      avioniPolasci = oSKlijent.getDepartures(icao, odVremena, doVremena);
      if (avioniPolasci != null) {
        System.out.println("Broj letova: " + avioniPolasci.size());
        for (LetAviona a : avioniPolasci) {
          System.out.println("Avion: " + a.getIcao24() + " Odredište: " + a.getEstArrivalAirport());
        }
      }
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }
    System.out.println("Dolasci na aerodrom: " + icao);
    List<LetAviona> avioniDolasci;
    try {
      avioniDolasci = oSKlijent.getArrivals(icao, odVremena, doVremena);
      if (avioniDolasci != null) {
        System.out.println("Broj letova: " + avioniDolasci.size());
        for (LetAviona a : avioniDolasci) {
          System.out
              .println("Avion: " + a.getIcao24() + " Odredište: " + a.getEstDepartureAirport());
        }
      }
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }
  }
}
