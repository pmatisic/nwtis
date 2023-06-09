package org.foi.nwtis.pmatisic.projekt.filter;

import java.io.IOException;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Status;
import org.foi.nwtis.pmatisic.projekt.posluzitelj.StanjePosluzitelja;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class FilterStanja implements Filter {

  private Konfiguracija konfiguracija;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    ServletContext context = filterConfig.getServletContext();
    konfiguracija = (Konfiguracija) context.getAttribute("konfiguracija");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    StanjePosluzitelja stanjePosluzitelja = new StanjePosluzitelja(konfiguracija);
    Status status = stanjePosluzitelja.provjeriStatusPosluzitelja();

    String requestPath = httpRequest.getRequestURI();

    if (status == Status.PAUZA && !requestPath.startsWith("/pmatisic_aplikacija_2/api/nadzor")) {
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Poslužitelj je u stanju pauze.");
      return;
    }

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {}
}
