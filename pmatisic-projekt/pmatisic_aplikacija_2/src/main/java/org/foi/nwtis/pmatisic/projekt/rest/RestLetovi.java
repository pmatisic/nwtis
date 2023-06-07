package org.foi.nwtis.pmatisic.projekt.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// TODO: Ovu klasu treba maknuti, s obzirom da se letovi tek obrađuju u aplikaciji 4.
/**
 * 
 * Klasa RestLetovi.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 *
 */
@Path("letovi")
@RequestScoped
public class RestLetovi {

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dohvaća sve letove koji polaze iz zračne luke s navedenim ICAO kodom.
   *
   * @param icao ICAO kod zračne luke
   * @param dan datum leta u formatu "dd.MM.yyyy"
   * @param odBrojaStr početni indeks rezultata
   * @param brojStr maksimalan broj rezultata
   * @return Response koji sadrži sve letove u JSON formatu, ili odgovor s odgovarajućim statusom
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajSvePolaske(@PathParam("icao") String icao, @QueryParam("dan") String dan,
      @QueryParam("odBroja") String odBrojaStr, @QueryParam("broj") String brojStr) {

    if (!jesuLiParametriIspravni(icao, dan, odBrojaStr, brojStr)) {
      return Response.status(400).build();
    }

    Integer odBroja = odBrojaStr == null ? 1 : Integer.parseInt(odBrojaStr);
    Integer broj = brojStr == null ? 20 : Integer.parseInt(brojStr);

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate datum = LocalDate.parse(dan, dtf);
    int odVremena = (int) datum.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
    int doVremena = odVremena + 86400;

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String korisnik = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik");
    String lozinka = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka");
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

  /**
   * Provjerava jesu li parametri ispravni za dohvaćanje letova.
   *
   * @param icao ICAO kod zračne luke
   * @param dan datum leta u formatu "dd.MM.yyyy"
   * @param odBrojaStr početni indeks rezultata
   * @param brojStr maksimalan broj rezultata
   * @return true ako su parametri ispravni, inače false
   */
  private boolean jesuLiParametriIspravni(String icao, String dan, String odBrojaStr,
      String brojStr) {
    if (icao == null || icao.length() < 2
        || !icao.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-')) {
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

  /**
   * Dohvaća sve letove koji polaze iz jedne zračne luke i slijeću u drugu zračnu luku.
   *
   * @param icaoOd ICAO kod polazne zračne luke
   * @param icaoDo ICAO kod odredišne zračne luke
   * @param dan datum leta u formatu "dd.MM.yyyy"
   * @return Response koji sadrži sve letove u JSON formatu, ili odgovor s odgovarajućim statusom
   */
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

    String korisnik = (String) konfig.getAttribute("OpenSkyNetwork.korisnik");
    String lozinka = (String) konfig.getAttribute("OpenSkyNetwork.lozinka");
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

  /**
   * Provjerava jesu li parametri ispravni za ICAO kod aerodroma i datum.
   * 
   * @param icao ICAO kod aerodroma koji se provjerava, ne smije biti null niti kraći od 2 znaka.
   * @param dan Datum u formatu "dd.MM.yyyy" koji se provjerava, ne smije biti null niti prazan.
   * @return true ako su oba parametra ispravna, false u suprotnom.
   */
  private boolean jesuLiParametriIspravni(String icao, String dan) {
    if (icao == null || icao.length() < 2
        || !icao.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-')) {
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

  /**
   * Dodaje novi let u bazu podataka.
   *
   * @param let LetAviona objekt koji sadrži informacije o letu
   * @return Response s odgovarajućim statusom koji predstavlja rezultat dodavanja leta
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response dodajLet(LetAviona let) {

    String upit =
        "INSERT INTO LETOVI_POLASCI (icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport, callsign, estDepartureAirportHorizDistance, estDepartureAirportVertDistance, estArrivalAirportHorizDistance, estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount, stored) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = ds.getConnection(); PreparedStatement stmt = con.prepareStatement(upit)) {
      stmt.setString(1, let.getIcao24());
      stmt.setInt(2, let.getFirstSeen());
      stmt.setString(3, let.getEstDepartureAirport());
      stmt.setInt(4, let.getLastSeen());
      stmt.setString(5, let.getEstArrivalAirport());
      stmt.setString(6, let.getCallsign());
      stmt.setInt(7, let.getEstDepartureAirportHorizDistance());
      stmt.setInt(8, let.getEstDepartureAirportVertDistance());
      stmt.setInt(9, let.getEstArrivalAirportHorizDistance());
      stmt.setInt(10, let.getEstArrivalAirportVertDistance());
      stmt.setInt(11, let.getDepartureAirportCandidatesCount());
      stmt.setInt(12, let.getArrivalAirportCandidatesCount());
      stmt.setTimestamp(13, new Timestamp(System.currentTimeMillis()));

      int brojAzuriranihRedova = stmt.executeUpdate();

      if (brojAzuriranihRedova > 0) {
        return Response.status(Response.Status.CREATED).build();
      } else {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }

    } catch (SQLIntegrityConstraintViolationException ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.CONFLICT)
          .entity("Kršenje ograničenja integriteta: " + ex.getMessage()).build();
    } catch (SQLException ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Dohvaća sve spremljene letove iz baze podataka.
   *
   * @return Response koji sadrži sve spremljene letove u JSON formatu, ili odgovor s odgovarajućim
   *         statusom
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("spremljeni")
  public Response dajSpremljeneLetove() {

    List<LetAvionaID> spremljeniLetovi = new ArrayList<>();
    String upit = "SELECT * FROM LETOVI_POLASCI";

    try (Connection con = ds.getConnection(); PreparedStatement stmt = con.prepareStatement(upit)) {
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        LetAvionaID let = new LetAvionaID(rs.getLong("id"), rs.getString("icao24"),
            rs.getInt("firstSeen"), rs.getString("estDepartureAirport"), rs.getInt("lastSeen"),
            rs.getString("estArrivalAirport"), rs.getString("callsign"),
            rs.getInt("estDepartureAirportHorizDistance"),
            rs.getInt("estDepartureAirportVertDistance"),
            rs.getInt("estArrivalAirportHorizDistance"), rs.getInt("estArrivalAirportVertDistance"),
            rs.getInt("departureAirportCandidatesCount"),
            rs.getInt("arrivalAirportCandidatesCount"));
        spremljeniLetovi.add(let);
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    if (!spremljeniLetovi.isEmpty()) {
      Gson gson = new Gson();
      String podaci = gson.toJson(spremljeniLetovi);
      return Response.ok().entity(podaci).build();
    } else {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  /**
   * Briše let iz baze podataka prema navedenom ID-u.
   *
   * @param id ID leta koji treba izbrisati
   * @return Response s odgovarajućim statusom koji predstavlja rezultat brisanja leta
   */
  @DELETE
  @Path("{id}")
  public Response obrisiLet(@PathParam("id") int id) {

    if (id <= 0) {
      return Response.status(Response.Status.BAD_REQUEST).build();
    }

    String upit = "DELETE FROM LETOVI_POLASCI WHERE id = ?";

    try (Connection con = ds.getConnection(); PreparedStatement stmt = con.prepareStatement(upit)) {
      stmt.setInt(1, id);
      int brojAzuriranihRedova = stmt.executeUpdate();
      if (brojAzuriranihRedova > 0) {
        return Response.status(Response.Status.OK).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
