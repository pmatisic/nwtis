package org.foi.nwtis.pmatisic.zadaca_2.slusaci;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 *
 * @author Petar Matišić
 * @author Dragutin Kermek
 * @version 1.1
 */
@WebListener
public final class SlusacAplikacije implements ServletContextListener {

  private ServletContext context = null;

  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    System.out.println("Kreiraj kontekst: " + context.getContextPath());
    ucitajKonfiguraciju();
  }

  private void ucitajKonfiguraciju() {
    String path = context.getRealPath("/WEB-INF") + java.io.File.separator;
    String datoteka = context.getInitParameter("konfiguracija");
    try {
      File file = new File(path + datoteka);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();

      NodeList nList = doc.getElementsByTagName("entry");

      for (int i = 0; i < nList.getLength(); i++) {
        Node nNode = nList.item(i);

        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
          Element eElement = (Element) nNode;
          String key = eElement.getAttribute("key");
          String value = eElement.getTextContent();
          context.setAttribute(key, value);
          System.out.println("Ključ: " + key + ", Vrijednost: " + value);
        }
      }
      System.out.println("Ucitana konfiguracija!");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Problem s konfiguracijom!");
    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    context = event.getServletContext();
    System.out.println("Obrisan kontekst: " + context.getContextPath());
  }

  public ServletContext dohvatiServletContext() {
    return context;
  }

}
