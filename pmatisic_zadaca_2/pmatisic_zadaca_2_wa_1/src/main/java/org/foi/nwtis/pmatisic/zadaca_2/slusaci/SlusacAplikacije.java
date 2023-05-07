package org.foi.nwtis.pmatisic.zadaca_2.slusaci;

import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušač aplikacije koji inicijalizira i uništava kontekst servleta.
 *
 * @author Petar Matišić
 * @author Dragutin Kermek
 * @version 1.1
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
    } catch (NeispravnaKonfiguracija ex) {
      System.err.println("Greška prilikom učitavanja konfiguracije: " + putanja + datoteka);
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
