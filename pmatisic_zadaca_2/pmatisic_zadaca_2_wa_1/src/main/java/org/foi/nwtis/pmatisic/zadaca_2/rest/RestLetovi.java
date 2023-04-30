package org.foi.nwtis.pmatisic.zadaca_2.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("letovi")
@RequestScoped
public class RestLetovi {

  @Context
  private ServletContext context;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajSvePolaske(@PathParam("icao") String icao, @QueryParam("dan") String dan,
      @QueryParam("odBroja") String odBrojaStr, @QueryParam("broj") String brojStr) {

    Integer odBroja = null;
    Integer broj = null;

    if (!jesuLiParametriIspravni(icao, dan, odBrojaStr, brojStr)) {
      return Response.status(400).build();
    }

    odBroja = odBrojaStr == null ? 1 : Integer.parseInt(odBrojaStr);
    broj = brojStr == null ? 20 : Integer.parseInt(brojStr);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate datum = LocalDate.parse(dan, dtf);
    int odVremena = (int) datum.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    int doVremena = odVremena + 86400;

    String korisnik = (String) context.getAttribute("OpenSkyNetwork.korisnik");
    String lozinka = (String) context.getAttribute("OpenSkyNetwork.lozinka");
    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

    List<LetAviona> avioniPolasci;
    try {
      avioniPolasci = oSKlijent.getDepartures(icao, odVremena, doVremena);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
      return Response.status(500).build();
    }

    if (avioniPolasci != null) {
      int startIndex = (odBroja - 1) * broj;
      int endIndex = Math.min(startIndex + broj, avioniPolasci.size());
      List<LetAviona> avioniPolasciStraniceni = avioniPolasci.subList(startIndex, endIndex);

      Gson gson = new Gson();
      String podaci = gson.toJson(avioniPolasciStraniceni);
      return Response.ok().entity(podaci).build();
    } else {
      return Response.status(404).build();
    }
  }

  private boolean jesuLiParametriIspravni(String icao, String dan, String odBrojaStr,
      String brojStr) {
    if (icao == null || icao.isEmpty() || !icao.matches("^[A-Z]{4}$")) {
      return false;
    }
    if (dan == null || dan.isEmpty()) {
      return false;
    }
    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate.parse(dan, dtf);
    } catch (DateTimeParseException e) {
      return false;
    }
    if (odBrojaStr != null) {
      try {
        int odBroja = Integer.parseInt(odBrojaStr);
        if (odBroja <= 0) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
    if (brojStr != null) {
      try {
        int broj = Integer.parseInt(brojStr);
        if (broj <= 0) {
          return false;
        }
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return true;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/{icaoDo}")
  public Response dajSvePolaskeAerodroma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo, @QueryParam("dan") String dan) {
    if (!jesuLiParametriIspravni(icaoOd, dan) || !jesuLiParametriIspravni(icaoDo, dan)) {
      return Response.status(400).build();
    }

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate datum = LocalDate.parse(dan, dtf);
    int odVremena = (int) datum.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    int doVremena = odVremena + 86400;

    String korisnik = (String) context.getAttribute("OpenSkyNetwork.korisnik");
    String lozinka = (String) context.getAttribute("OpenSkyNetwork.lozinka");
    OSKlijent oSKlijent = new OSKlijent(korisnik, lozinka);

    List<LetAviona> avioniPolasci;
    try {
      avioniPolasci = oSKlijent.getDepartures(icaoOd, odVremena, doVremena);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
      return Response.status(500).build();
    }

    if (avioniPolasci != null) {
      List<LetAviona> filtriraniAvioniPolasci = avioniPolasci.stream()
          .filter(let -> icaoDo.equals(let.getEstArrivalAirport())).collect(Collectors.toList());

      Gson gson = new Gson();
      String podaci = gson.toJson(filtriraniAvioniPolasci);
      return Response.ok().entity(podaci).build();
    } else {
      return Response.status(404).build();
    }
  }

  private boolean jesuLiParametriIspravni(String icao, String dan) {
    if (icao == null || icao.isEmpty() || !icao.matches("^[A-Z]{4}$")) {
      return false;
    }
    if (dan == null || dan.isEmpty()) {
      return false;
    }
    try {
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate.parse(dan, dtf);
    } catch (DateTimeParseException e) {
      return false;
    }
    return true;
  }

}
