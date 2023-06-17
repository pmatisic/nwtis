package org.foi.nwtis.pmatisic.projekt.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class FilterDnevnika implements Filter {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

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
      vrsta = "AP2";
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

    try (Connection con = ds.getConnection()) {
      String upit =
          "INSERT INTO DNEVNIK (vrsta, vrijeme_pristupa, putanja, ip_adresa, korisnik) VALUES (?, ?, ?, ?, ?)";
      try (PreparedStatement s = con.prepareStatement(upit)) {
        s.setString(1, vrsta);
        s.setTimestamp(2, new java.sql.Timestamp(new java.util.Date().getTime()));
        s.setString(3, fullPath);
        s.setString(4, clientIP);
        if (korisnikId == null) {
          s.setNull(5, Types.INTEGER);
        } else {
          s.setInt(5, korisnikId);
        }

        s.executeUpdate();
      }
    } catch (SQLException ex) {
      throw new ServletException("Problem sa upisivanjem u tablicu DNEVNIK", ex);
    }

    chain.doFilter(request, response);
  }

}
