package org.foi.nwtis.pmatisic.projekt.filter;

import java.io.IOException;
import java.sql.Timestamp;
import org.foi.nwtis.pmatisic.projekt.entitet.Dnevnik;
import org.foi.nwtis.pmatisic.projekt.entitet.Korisnici;
import org.foi.nwtis.pmatisic.projekt.zrno.DnevnikFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import jakarta.ejb.EJB;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter
public class FilterDnevnika implements Filter {

  @EJB
  private DnevnikFacade dnevnikFacade;

  @EJB
  private KorisniciFacade korisniciFacade;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String requestURI = httpRequest.getRequestURI();
    String queryString = httpRequest.getQueryString();
    String clientIP = request.getRemoteAddr();
    String vrsta = httpRequest.getHeader("X-Application-Type");
    String fullPath = requestURI;

    if (queryString != null) {
      fullPath += "?" + queryString;
    }

    if (vrsta == null) {
      vrsta = "AP4";
    }

    String korisnikStr = httpRequest.getHeader("X-User-ID");
    Integer korisnikId = null;
    if (korisnikStr != null) {
      try {
        korisnikId = Integer.parseInt(korisnikStr);
      } catch (NumberFormatException ex) {
        korisnikId = null;
      }
    }

    Dnevnik dnevnik = new Dnevnik();
    dnevnik.setVrsta(vrsta);
    dnevnik.setVrijemePristupa(new Timestamp(System.currentTimeMillis()));
    dnevnik.setPutanja(fullPath);
    dnevnik.setIpAdresa(clientIP);
    if (korisnikId != null) {
      Korisnici korisnik = korisniciFacade.find(korisnikId);
      if (korisnik != null) {
        dnevnik.setKorisnik(korisnik);
      }
    } else {
      dnevnik.setKorisnik(null);
    }

    dnevnikFacade.create(dnevnik);

    chain.doFilter(request, response);
  }
}
