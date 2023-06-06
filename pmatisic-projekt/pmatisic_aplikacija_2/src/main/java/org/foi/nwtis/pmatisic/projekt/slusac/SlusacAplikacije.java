package org.foi.nwtis.pmatisic.projekt.slusac;

import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.StanjePosluzitelja;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušač aplikacije koji inicijalizira i uništava kontekst servleta.
 *
 * @author Petar Matišić
 * @author Dragutin Kermek
 * @version 1.2.1
 */
@WebListener
public final class SlusacAplikacije implements ServletContextListener {

  private ServletContext context = null;

  /**
   * Metoda koja se poziva pri inicijalizaciji konteksta servleta. Učitava konfiguraciju iz datoteke
   * i sprema je u atribut konteksta.
   *
   * @param sce ServletContextEvent koji sadrži informacije o kontekstu.
   */
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String datoteka = sce.getServletContext().getInitParameter("konfiguracija");
    String putanja = sce.getServletContext().getRealPath("/WEB-INF") + java.io.File.separator;

    try {
      Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + datoteka);
      sce.getServletContext().setAttribute("konfiguracija", konfig);
      System.out
          .println("Aplikacija je uspješno pokrenuta s konfiguracijom: " + putanja + datoteka);

      // Ispisivanje ključeva i vrijednosti konfiguracije kao test
      Properties postavke = konfig.dajSvePostavke();
      for (String kljuc : postavke.stringPropertyNames()) {
        String vrijednost = postavke.getProperty(kljuc);
        System.out.println("Ključ: " + kljuc + ", Vrijednost: " + vrijednost);
      }

      // Provjera statusa poslužitelja nakon učitavanja konfiguracije
      StanjePosluzitelja stanjePosluzitelja = new StanjePosluzitelja(konfig);
      Status status = stanjePosluzitelja.provjeriStatusPosluzitelja();
      if (status == Status.PAUZA) {
        System.err.println("Poslužitelj nije aktivan. Prekidam rad.");
        sce.getServletContext().setAttribute("statusPosluzitelja", "PAUZA");
        // Zatvaranje svih otvorenih veza baze podataka
        // DBConnectionManager.closeAllConnections();
        return;
      }

    } catch (NeispravnaKonfiguracija ex) {
      System.err.println("Pogreška u radu poslužitelja i/ili konfiguracije.");
    }
  }

  /**
   * Metoda koja se poziva pri uništavanju konteksta servleta. Ispisuje poruku o uništavanju
   * konteksta.
   *
   * @param event ServletContextEvent koji sadrži informacije o kontekstu.
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    context = event.getServletContext();
    System.out.println("Obrisan kontekst: " + context.getContextPath());
  }

}
