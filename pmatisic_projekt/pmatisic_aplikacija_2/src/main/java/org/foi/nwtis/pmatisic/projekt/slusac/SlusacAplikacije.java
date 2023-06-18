package org.foi.nwtis.pmatisic.projekt.slusac;

import java.net.Socket;
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

@WebListener
public final class SlusacAplikacije implements ServletContextListener {

  private ServletContext context = null;
  private Konfiguracija konfig;

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String datoteka = sce.getServletContext().getInitParameter("konfiguracija");
    String putanja = sce.getServletContext().getRealPath("/WEB-INF") + java.io.File.separator;

    try {
      konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(putanja + datoteka);
      sce.getServletContext().setAttribute("konfiguracija", konfig);
      System.out
          .println("Aplikacija je uspješno pokrenuta s konfiguracijom: " + putanja + datoteka);
      Properties postavke = konfig.dajSvePostavke();
      for (String kljuc : postavke.stringPropertyNames()) {
        String vrijednost = postavke.getProperty(kljuc);
        System.out.println("Ključ: " + kljuc + ", Vrijednost: " + vrijednost);
      }
    } catch (NeispravnaKonfiguracija ex) {
      System.err.println("Greška prilikom učitavanja konfiguracije: " + putanja + datoteka);
    }

    String adresaPosluzitelja = konfig.dajPostavku("adresa.posluzitelja");
    Integer mreznaVrataPosluzitelja =
        Integer.parseInt(konfig.dajPostavku("mreznaVrata.posluzitelja"));
    try (Socket socket = new Socket(adresaPosluzitelja, mreznaVrataPosluzitelja)) {
      StanjePosluzitelja stanjePosluzitelja = new StanjePosluzitelja(konfig);
      Status status = stanjePosluzitelja.provjeriStatusPosluzitelja();
      if (status == Status.PAUZA) {
        throw new RuntimeException("Poslužitelj nije aktivan. Prekidam rad.");
      } else {
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    context = event.getServletContext();
    System.out.println("Obrisan kontekst: " + context.getContextPath());
  }

}
