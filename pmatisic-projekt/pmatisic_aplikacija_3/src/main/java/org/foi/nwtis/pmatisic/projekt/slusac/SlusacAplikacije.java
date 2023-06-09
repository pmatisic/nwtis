package org.foi.nwtis.pmatisic.projekt.slusac;

import java.util.Properties;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.pmatisic.projekt.dretva.SakupljacLetovaAviona;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.StanjePosluzitelja;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.JmsPosiljatelj;
import org.foi.nwtis.pmatisic.projekt.zrno.LetoviPolasciFacade;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušač aplikacije koji inicijalizira i uništava kontekst servleta. Pokreće i gasi dretvu
 * programa.
 *
 * @author Petar Matišić
 * @author Dragutin Kermek
 * @version 1.2.2
 */
@WebListener
public final class SlusacAplikacije implements ServletContextListener {

  private SakupljacLetovaAviona sakupljacLetovaAviona;
  private ServletContext context = null;
  private Konfiguracija konfig;
  @EJB
  JmsPosiljatelj jmsPosiljatelj;
  @Inject
  LetoviPolasciFacade lpFacade;
  @Inject
  AirportFacade airportFacade;

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
      konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + datoteka);
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

    // Provjera statusa poslužitelja nakon učitavanja konfiguracije
    StanjePosluzitelja stanjePosluzitelja = new StanjePosluzitelja(konfig);
    Status status = stanjePosluzitelja.provjeriStatusPosluzitelja();
    if (status == Status.PAUZA) {
      throw new RuntimeException("Poslužitelj nije aktivan. Prekidam rad.");
    }

    startThread(sce);
  }

  /**
   * Metoda koja pokreće dretvu za sakupljanje letova aviona.
   * 
   * @param event Događaj konteksta servleta koji pokreće ovu metodu.
   */
  private void startThread(ServletContextEvent event) {
    context = event.getServletContext();
    sakupljacLetovaAviona =
        new SakupljacLetovaAviona(context, lpFacade, airportFacade, jmsPosiljatelj);
    sakupljacLetovaAviona.start();
    System.out.println("Dretva je pokrenuta!");
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
    sakupljacLetovaAviona.interrupt();
    System.out.println("Dretva je ugašena!");
    System.out.println("Obrisan kontekst: " + context.getContextPath());
  }

}
