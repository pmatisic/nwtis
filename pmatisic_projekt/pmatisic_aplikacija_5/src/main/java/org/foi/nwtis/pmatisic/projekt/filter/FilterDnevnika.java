package org.foi.nwtis.pmatisic.projekt.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import org.foi.nwtis.pmatisic.projekt.podatak.Dnevnik;
import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

@WebFilter
public class FilterDnevnika implements Filter {

  private static final String API_URL = "http://200.20.0.4:8080/pmatisic_aplikacija_2/api/dnevnik";
  private static final Gson gson = new Gson();

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
      vrsta = "AP5";
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

    Timestamp vrijemePristupaTimestamp = new Timestamp(System.currentTimeMillis());
    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    String vrijemePristupa = formatter.format(vrijemePristupaTimestamp.toInstant());
    Dnevnik dnevnik = new Dnevnik(vrsta, vrijemePristupa, fullPath, clientIP, korisnikId);
    sendPostRequest(dnevnik);

    chain.doFilter(request, response);
  }

  private void sendPostRequest(Dnevnik dnevnik) throws IOException {
    URL url = new URL(API_URL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("Accept", "application/json");
    connection.setDoOutput(true);

    String jsonInputString = gson.toJson(dnevnik);
    try (OutputStream os = connection.getOutputStream()) {
      byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
      os.write(input, 0, input.length);
    }

    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK
        || responseCode == HttpURLConnection.HTTP_CREATED) {
      System.out.println("Dnevnik poslan uspješno.");
    } else {
      InputStream errorStream = connection.getErrorStream();
      if (errorStream != null) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
          String line;
          StringBuilder responseContent = new StringBuilder();
          while ((line = reader.readLine()) != null) {
            responseContent.append(line);
          }
          System.out.println("Pogreška prilikom slanja dnevnika. HTTP kod: " + responseCode);
          System.out.println("Odgovor servera: " + responseContent.toString());
        }
      } else {
        System.out.println("Pogreška prilikom slanja dnevnika. HTTP kod: " + responseCode);
      }
    }
  }

}
