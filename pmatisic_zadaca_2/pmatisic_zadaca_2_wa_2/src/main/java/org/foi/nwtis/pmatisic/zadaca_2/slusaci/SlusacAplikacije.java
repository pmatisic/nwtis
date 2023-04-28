package org.foi.nwtis.pmatisic.zadaca_2.slusaci;

import java.io.File;
import org.foi.nwtis.KonfiguracijaBP;
import org.foi.nwtis.PostavkeBazaPodataka;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SlusacAplikacije implements ServletContextListener {
  private static ServletContextEvent sce;

  public SlusacAplikacije() {}

  @Override
  public void contextInitialized(ServletContextEvent e) {
    ServletContext context = e.getServletContext();
    String nazivDatoteke = context.getInitParameter("konfiguracija");
    String putanja = context.getRealPath("/WEB-INF") + File.separator;
    nazivDatoteke = putanja + nazivDatoteke;
    KonfiguracijaBP konfig = new PostavkeBazaPodataka(nazivDatoteke);
    context.setAttribute("konfig", konfig);
    ServletContextListener.super.contextInitialized(e);
  }

  @Override
  public void contextDestroyed(ServletContextEvent e) {
    ServletContext context = e.getServletContext();
    context.removeAttribute("konfig");
    ServletContextListener.super.contextDestroyed(e);
  }

  public static ServletContext dohvatiServletContext() {
    ServletContext context = sce.getServletContext();
    return context;
  }

}
