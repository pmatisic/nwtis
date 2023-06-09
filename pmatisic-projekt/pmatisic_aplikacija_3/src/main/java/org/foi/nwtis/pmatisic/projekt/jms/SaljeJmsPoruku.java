package org.foi.nwtis.pmatisic.projekt.jms;

import java.io.IOException;
import org.foi.nwtis.pmatisic.projekt.zrno.JmsPosiljatelj;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet koji se koristi za slanje JMS poruka. JMS poruke se šalju putem JmsPosiljatelj zrna.
 *
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@WebServlet(name = "SaljeJmsPoruku", urlPatterns = {"/SaljeJmsPoruku"})
public class SaljeJmsPoruku extends HttpServlet {

  private static final long serialVersionUID = 6677591326517241529L;

  @EJB
  JmsPosiljatelj jmsPosiljatelj;

  /**
   * Metoda koja obrađuje GET zahtjeve. Iz URL-a dohvaća parametar "poruka" i, ako postoji i nije
   * prazan, šalje JMS poruku.
   *
   * @param req HttpServletRequest
   * @param resp HttpServletResponse
   * @throws ServletException Ako dođe do greške tijekom obrade zahtjeva
   * @throws IOException Ako dođe do greške tijekom obrade zahtjeva
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    var poruka = req.getParameter("poruka");

    if (poruka != null && !poruka.isEmpty()) {
      if (jmsPosiljatelj.saljiPoruku(poruka)) {
        System.out.println("Poruka je poslana!");
        return;
      } else {
        System.out.println("Greška kod slanja JMS poruke!");
        return;
      }
    }

    System.out.println("Poruka nema sadržaj");
    return;
  }

}
