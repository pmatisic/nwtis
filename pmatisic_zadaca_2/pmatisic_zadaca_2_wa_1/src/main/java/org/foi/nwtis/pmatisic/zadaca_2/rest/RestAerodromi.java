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

    List<Aerodrom> aerodromi = new ArrayList<>();

    if (jesuLiParametriPrazni(odBroja, broj)) {
      odBroja = "1";
      broj = "20";
    } else {
      if (false) {
        return Response.status(400).build();
      }
    }

    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS LIMIT ? OFFSET ?";
    String offset =
        String.valueOf(Integer.parseInt(odBroja) * Integer.parseInt(broj) - Integer.parseInt(broj));

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, broj);
      stmt.setString(2, offset);
      ResultSet rs = stmt.executeQuery();

      while (rs.next()) {
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setIcao(rs.getString("ICAO"));
        aerodrom.setNaziv(rs.getString("ISO_COUNTRY"));
        aerodrom.setDrzava(rs.getString("ICAO"));
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

    Gson gson = new Gson();
    String podaci = gson.toJson(aerodromi);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  private boolean jesuLiParametriPrazni(String odBroja, String broj) {
    return odBroja == null && broj == null;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajAerodrom(@PathParam("icao") String icao) {
    List<Aerodrom> aerodromi = new ArrayList<>();
    Aerodrom ad = new Aerodrom("LDZA", "Airport Zagreb", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LDVA", "Airport Varaždin", "HR", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDF", "Airport Frankfurt", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("EDDB", "Airport Berlin", "DE", new Lokacija("0", "0"));
    aerodromi.add(ad);
    ad = new Aerodrom("LOWW", "Airport Vienna", "AT", new Lokacija("0", "0"));
    aerodromi.add(ad);

    Aerodrom aerodrom = null;
    for (Aerodrom a : aerodromi) {
      if (a.getIcao().compareTo(icao) == 0) {
        aerodrom = a;
        break;
      }
    }

    if (aerodrom == null)
      return Response.status(404).build();

    Gson gson = new Gson();
    String podaci = gson.toJson(aerodrom);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/{icaoDo}")
  public Response dajUdaljenostiAerodoma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {

    var udaljenosti = new ArrayList<Udaljenost>();

    String upit = "SELECT ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY FROM "
        + "AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? AND ICAO_TO = ?";

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

    Gson gson = new Gson();
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }
}
