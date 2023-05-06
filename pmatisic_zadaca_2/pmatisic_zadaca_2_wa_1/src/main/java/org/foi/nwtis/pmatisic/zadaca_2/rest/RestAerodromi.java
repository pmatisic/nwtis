package org.foi.nwtis.pmatisic.zadaca_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSveAerodrome(@QueryParam("odBroja") String odBroja,
      @QueryParam("broj") String broj) {

    if (jesuLiParametriPrazni(odBroja, broj)) {
      odBroja = "1";
      broj = "20";
    } else {
      if (!jesuLiParametriBroj(odBroja, broj)) {
        return Response.status(400).build();
      }
    }

    List<Aerodrom> aerodromi = new ArrayList<>();
    int odBrojaInt = Integer.parseInt(odBroja);
    int brojInt = Integer.parseInt(broj);
    int offset = (odBrojaInt - 1) * brojInt;
    String upit =
        "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES "
        + "FROM AIRPORTS "
        + "ORDER BY ICAO " 
        + "LIMIT ? "
        + "OFFSET ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setInt(1, brojInt);
      stmt.setInt(2, offset);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("NAME"));
        aerodrom.setDrzava(rs.getString("ISO_COUNTRY"));
        String koordinate[] = rs.getString("COORDINATES").split(",");
        Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1].trim());
        aerodrom.setLokacija(lokacija);
        aerodromi.add(aerodrom);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    if (aerodromi.isEmpty()) {
      return Response.status(404).build();
    }

    Gson gson = new Gson();
    String podaci = gson.toJson(aerodromi);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  private boolean jesuLiParametriBroj(String odBroja, String broj) {
    try {
      int parsedOdBroja = Integer.parseInt(odBroja);
      int parsedBroj = Integer.parseInt(broj);
      if (parsedOdBroja <= 0 || parsedBroj <= 0) {
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  private boolean jesuLiParametriPrazni(String odBroja, String broj) {
    return odBroja == null && broj == null;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajAerodrom(@PathParam("icao") String icao) {

    if (!jesuLiParametriIspravni(icao)) {
      return Response.status(400).build();
    }

    Aerodrom aerodrom = null;
    String upit =
        "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES "
        + "FROM AIRPORTS "
        + "WHERE ICAO = ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        aerodrom = new Aerodrom();
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("NAME"));
        aerodrom.setDrzava(rs.getString("ISO_COUNTRY"));
        String koordinate[] = rs.getString("COORDINATES").split(",");
        Lokacija lokacija = new Lokacija(koordinate[0], koordinate[1].trim());
        aerodrom.setLokacija(lokacija);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    if (aerodrom == null) {
      return Response.status(404).build();
    }
    
    Gson gson = new Gson();
    String podaci = gson.toJson(aerodrom);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  private boolean jesuLiParametriIspravni(String icao) {
    if (icao == null || icao.length() < 2) {
      return false;
    }
    return icao.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-');
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/{icaoDo}")
  public Response dajUdaljenostiAerodoma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {

    if (!jesuLiParametriIcao(icaoFrom, icaoTo)) {
      return Response.status(400).build();
    }

    var udaljenosti = new ArrayList<Udaljenost>();
    String upit = 
        "SELECT ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY "
        + "FROM AIRPORTS_DISTANCE_MATRIX " 
        + "WHERE ICAO_FROM = ? AND ICAO_TO = ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoFrom);
      stmt.setString(2, icaoTo);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        var u = new Udaljenost(drzava, udaljenost);
        udaljenosti.add(u);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    if (udaljenosti.isEmpty()) {
      return Response.status(404).build();
    }

    Gson gson = new Gson();
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  private boolean jesuLiParametriIcao(String icaoFrom, String icaoTo) {
    return jesuLiParametriIspravni(icaoFrom) && jesuLiParametriIspravni(icaoTo);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}/udaljenosti")
  public Response dajUdaljenostiZaAerodome(@PathParam("icao") String icao,
      @QueryParam("odBroja") String odBroja, @QueryParam("broj") String broj) {

    if (!jesuLiParametriIspravni(icao)) {
      return Response.status(400).build();
    }

    if (jesuLiParametriPrazni(odBroja, broj)) {
      odBroja = "1";
      broj = "20";
    } else {
      if (!jesuLiParametriBroj(odBroja, broj)) {
        return Response.status(400).build();
      }
    }

    int odBrojaInt = Integer.parseInt(odBroja);
    int brojInt = Integer.parseInt(broj);
    int offset = (odBrojaInt - 1) * brojInt;
    var udaljenosti = new ArrayList<UdaljenostAerodrom>();
    String upit = 
        "SELECT DISTINCT ICAO_TO, DIST_TOT " 
        + "FROM AIRPORTS_DISTANCE_MATRIX "
        + "WHERE ICAO_FROM = ? " 
        + "ORDER BY DIST_TOT " 
        + "LIMIT ? " 
        + "OFFSET ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      stmt.setInt(2, brojInt);
      stmt.setInt(3, offset);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        String icaoTo = rs.getString("ICAO_TO");
        float udaljenost = rs.getFloat("DIST_TOT");
        var u = new UdaljenostAerodrom(icaoTo, udaljenost);
        udaljenosti.add(u);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    if (udaljenosti.isEmpty()) {
      return Response.status(404).build();
    }
    
    Gson gson = new Gson();
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}/najduljiPutDrzave")
  public Response dajNajduljiPutDrzave(@PathParam("icao") String icao) {

    if (!jesuLiParametriIspravni(icao)) {
      return Response.status(400).build();
    }

    UdaljenostAerodromDrzava najduziPut = null;
    String upit = 
        "SELECT ADM1.ICAO_TO, ADM1.COUNTRY, ADM1.DIST_CTRY AS MAX_DIST_CTRY "
        + "FROM AIRPORTS_DISTANCE_MATRIX ADM1 "
        + "JOIN ("
        + "SELECT ADM2.COUNTRY, MAX(ADM2.DIST_CTRY) AS MAX_DIST "
        + "FROM AIRPORTS_DISTANCE_MATRIX ADM2 "
        + "WHERE ADM2.ICAO_FROM = ? "
        + "GROUP BY ADM2.COUNTRY"
        + ") AS SUBQUERY ON ADM1.COUNTRY = SUBQUERY.COUNTRY AND ADM1.DIST_CTRY = SUBQUERY.MAX_DIST "
        + "WHERE ADM1.ICAO_FROM = ? "
        + "ORDER BY MAX_DIST_CTRY DESC "
        + "LIMIT 1";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      stmt.setString(2, icao);
      ResultSet rs = stmt.executeQuery();

      if (rs.next()) {
        String icaoTo = rs.getString("ICAO_TO");
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("MAX_DIST_CTRY");
        najduziPut = new UdaljenostAerodromDrzava(icaoTo, drzava, udaljenost);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed())
          stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    if (najduziPut == null) {
      return Response.status(404).build();
    }

    Gson gson = new Gson();
    String podaci = gson.toJson(najduziPut);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

}
