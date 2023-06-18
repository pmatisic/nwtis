package org.foi.nwtis.pmatisic.projekt.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.podatak.Aerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.podatak.Udaljenost;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodromDrzava;
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

@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSveAerodrome(@QueryParam("odBroja") String odBroja,
      @QueryParam("broj") String broj, @QueryParam("traziNaziv") String traziNaziv,
      @QueryParam("traziDrzavu") String traziDrzavu) {

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

    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES " +
                  "FROM AIRPORTS " +
                  "WHERE (NAME LIKE ? OR ? IS NULL) " +
                  "AND (ISO_COUNTRY = ? OR ? IS NULL) " +
                  "ORDER BY ICAO " + 
                  "LIMIT ? " +
                  "OFFSET ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, traziNaziv != null ? "%" + traziNaziv + "%" : null);
      stmt.setString(2, traziNaziv);
      stmt.setString(3, traziDrzavu);
      stmt.setString(4, traziDrzavu);
      stmt.setInt(5, brojInt);
      stmt.setInt(6, offset);
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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajAerodrom(@PathParam("icao") String icao) {

    if (!jesuLiParametriIspravni(icao)) {
      return Response.status(400).build();
    }

    Aerodrom aerodrom = null;
    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES " + 
                  "FROM AIRPORTS " + 
                  "WHERE ICAO = ?";

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
        Lokacija lokacija = new Lokacija(koordinate[1], koordinate[0].trim());
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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/{icaoDo}")
  public Response dajUdaljenostiZaAerodom(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    if (!jesuLiParametriIcao(icaoOd, icaoDo)) {
      return Response.status(400).build();
    }

    var udaljenosti = new ArrayList<Udaljenost>();
    String upit = "SELECT ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY " + 
                  "FROM AIRPORTS_DISTANCE_MATRIX " + 
                  "WHERE ICAO_FROM = ? AND ICAO_TO = ?";

    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoOd);
      stmt.setString(2, icaoDo);
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

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}/udaljenosti")
  public Response dajUdaljenostiZaSveAerodome(@PathParam("icao") String icao,
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
    String upit = "SELECT DISTINCT ICAO_TO, DIST_TOT " + 
                  "FROM AIRPORTS_DISTANCE_MATRIX " + 
                  "WHERE ICAO_FROM = ? " + 
                  "ORDER BY DIST_TOT " + 
                  "LIMIT ? " + 
                  "OFFSET ?";

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
  @Path("{icaoOd}/izracunaj/{icaoDo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response izracunajUdaljenost(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    if (!jesuLiParametriIcao(icaoOd, icaoDo)) {
      return Response.status(400).build();
    }

    try (Connection con = ds.getConnection()) {
      String upit = "SELECT ICAO, COORDINATES FROM AIRPORTS WHERE ICAO IN (?, ?)";
      PreparedStatement stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoOd);
      stmt.setString(2, icaoDo);
      ResultSet rs = stmt.executeQuery();
      String gpsSirina1 = null, gpsDuzina1 = null, gpsSirina2 = null, gpsDuzina2 = null;

      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String[] koordinate = rs.getString("COORDINATES").split(",");
        if (icao.equals(icaoOd)) {
          gpsSirina1 = koordinate[1];
          gpsDuzina1 = koordinate[0];
        } else {
          gpsSirina2 = koordinate[1];
          gpsDuzina2 = koordinate[0];
        }
      }

      if (gpsSirina1 == null || gpsDuzina1 == null || gpsSirina2 == null || gpsDuzina2 == null) {
        return Response.status(404).entity("Nisu pronađeni svi potrebni aerodromi.").build();
      }

      String komanda = "UDALJENOST" + gpsSirina1 + " " + gpsDuzina1 + gpsSirina2 + " " + gpsDuzina2;
      String odgovor = spojiSeNaPosluzitelj(komanda);

      if (odgovor == null) {
        return Response.status(404).entity("Greška prilikom komunikacije s aplikacijom.").build();
      } else {
        Gson gson = new Gson();
        String podaci = gson.toJson(parseUdaljenost(odgovor));
        return Response.ok().entity(podaci).build();
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return Response.status(500).build();
    }
  }

  @GET
  @Path("{icaoOd}/udaljenost1/{icaoDo}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenost1(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {

    if (!jesuLiParametriIcao(icaoOd, icaoDo)) {
      return Response.status(400).build();
    }

    try (Connection con = ds.getConnection()) {
      String upit = "SELECT ICAO, COORDINATES, ISO_COUNTRY FROM AIRPORTS WHERE ICAO IN (?, ?)";
      PreparedStatement stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoOd);
      stmt.setString(2, icaoDo);
      ResultSet rs = stmt.executeQuery();
      String gpsSirina1 = null, gpsDuzina1 = null, gpsSirina2 = null, gpsDuzina2 = null,
          isoCountry2 = null;

      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String[] koordinate = rs.getString("COORDINATES").split(",");
        if (icao.equals(icaoOd)) {
          gpsSirina1 = koordinate[1];
          gpsDuzina1 = koordinate[0];
        } else {
          gpsSirina2 = koordinate[1];
          gpsDuzina2 = koordinate[0];
          isoCountry2 = rs.getString("ISO_COUNTRY");
        }
      }

      if (gpsSirina1 == null || gpsDuzina1 == null || gpsSirina2 == null || gpsDuzina2 == null) {
        return Response.status(404).entity("Nisu pronađeni svi potrebni aerodromi.").build();
      }

      String komanda = "UDALJENOST" + gpsSirina1 + " " + gpsDuzina1 + gpsSirina2 + " " + gpsDuzina2;
      String odgovor = spojiSeNaPosluzitelj(komanda);
      double udaljenost = parseUdaljenost(odgovor);
      upit = "SELECT ICAO, COORDINATES FROM AIRPORTS WHERE ISO_COUNTRY = ?";
      stmt = con.prepareStatement(upit);
      stmt.setString(1, isoCountry2);
      rs = stmt.executeQuery();
      List<UdaljenostAerodrom> manjiAerodromi = new ArrayList<>();

      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String[] koordinate = rs.getString("COORDINATES").split(",");
        String gpsSirina = koordinate[1];
        String gpsDuzina = koordinate[0];
        komanda = "UDALJENOST" + gpsSirina1 + " " + gpsDuzina1 + gpsSirina + " " + gpsDuzina;
        odgovor = spojiSeNaPosluzitelj(komanda);
        double udaljenostAerodroma = parseUdaljenost(odgovor);
        if (udaljenostAerodroma < udaljenost) {
          manjiAerodromi.add(new UdaljenostAerodrom(icao, (float) udaljenostAerodroma));
        }
      }

      if (manjiAerodromi.isEmpty()) {
        return Response.status(404).build();
      }

      Gson gson = new Gson();
      String podaci = gson.toJson(manjiAerodromi);
      return Response.ok().entity(podaci).build();

    } catch (SQLException e) {
      e.printStackTrace();
      return Response.status(500).build();
    }
  }

  @GET
  @Path("{icaoOd}/udaljenost2")
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajUdaljenost2(@PathParam("icaoOd") String icaoOd,
      @QueryParam("drzava") String drzava, @QueryParam("km") String km) {

    if (!jesuLiParametriIspravni(icaoOd)) {
      return Response.status(400).build();
    }

    if (drzava == null || drzava.isEmpty() || km == null || km.isEmpty()) {
      return Response.status(400).entity("Parametri drzava i km su obavezni.").build();
    }

    try (Connection con = ds.getConnection()) {
      String upit = "SELECT COORDINATES FROM AIRPORTS WHERE ICAO = ?";
      PreparedStatement stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoOd);
      ResultSet rs = stmt.executeQuery();

      if (!rs.next()) {
        return Response.status(404).entity("Aerodrom nije pronađen.").build();
      }

      String[] koordinate1 = rs.getString("COORDINATES").split(",");
      String gpsSirina1 = koordinate1[1];
      String gpsDuzina1 = koordinate1[0];
      upit = "SELECT ICAO, COORDINATES FROM AIRPORTS WHERE ISO_COUNTRY = ?";
      stmt = con.prepareStatement(upit);
      stmt.setString(1, drzava);
      rs = stmt.executeQuery();
      double maxUdaljenost = Double.parseDouble(km);
      List<UdaljenostAerodrom> aerodromi = new ArrayList<>();

      while (rs.next()) {
        String[] koordinate2 = rs.getString("COORDINATES").split(",");
        String gpsSirina2 = koordinate2[1];
        String gpsDuzina2 = koordinate2[0];
        String komanda =
            "UDALJENOST" + gpsSirina1 + " " + gpsDuzina1 + gpsSirina2 + " " + gpsDuzina2;
        String odgovor = spojiSeNaPosluzitelj(komanda);
        double udaljenost = parseUdaljenost(odgovor);

        if (udaljenost < maxUdaljenost) {
          aerodromi.add(new UdaljenostAerodrom(rs.getString("ICAO"), (float) udaljenost));
        }
      }

      if (aerodromi.isEmpty()) {
        return Response.status(404).build();
      }

      Gson gson = new Gson();
      String podaci = gson.toJson(aerodromi);
      return Response.ok().entity(podaci).build();

    } catch (SQLException e) {
      e.printStackTrace();
      return Response.status(500).build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}/najduljiPutDrzave")
  public Response dajNajduljiPutDrzave(@PathParam("icao") String icao) {

    if (!jesuLiParametriIspravni(icao)) {
      return Response.status(400).build();
    }

    UdaljenostAerodromDrzava najduziPut = null;
    String upit = "SELECT ADM1.ICAO_TO, ADM1.COUNTRY, ADM1.DIST_CTRY AS MAX_DIST_CTRY " + 
                  "FROM AIRPORTS_DISTANCE_MATRIX ADM1 " + 
                  "JOIN (" + 
                  "SELECT ADM2.COUNTRY, MAX(ADM2.DIST_CTRY) AS MAX_DIST " + 
                  "FROM AIRPORTS_DISTANCE_MATRIX ADM2 " + 
                  "WHERE ADM2.ICAO_FROM = ? " + 
                  "GROUP BY ADM2.COUNTRY" + 
                  ") AS SUBQUERY ON ADM1.COUNTRY = SUBQUERY.COUNTRY AND ADM1.DIST_CTRY = SUBQUERY.MAX_DIST " + 
                  "WHERE ADM1.ICAO_FROM = ? " + 
                  "ORDER BY MAX_DIST_CTRY DESC " + 
                  "LIMIT 1";

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

  public String spojiSeNaPosluzitelj(String s) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String adresaPosluzitelja = (konfiguracija.dajPostavku("adresa.posluzitelja")).toString();
    Integer mreznaVrataPosluzitelja =
        Integer.parseInt(konfiguracija.dajPostavku("mreznaVrata.posluzitelja"));
    try (var socket = new Socket(adresaPosluzitelja, mreznaVrataPosluzitelja);
        var citac = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        var pisac = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));) {
      String komanda = s;
      pisac.write(komanda);
      pisac.flush();
      socket.shutdownOutput();
      String response = citac.readLine();
      socket.shutdownInput();
      if (response.startsWith("OK ")) {
        return response;
      } else {
        throw new RuntimeException("Neočekivani odgovor od poslužitelja: " + response);
      }
    } catch (IOException e) {
      throw new RuntimeException("Pogreška pri provjeri statusa poslužitelja", e);
    }
  }

  private double parseUdaljenost(String odgovor) {
    if (odgovor != null && odgovor.startsWith("OK ")) {
      return Double.parseDouble(odgovor.substring(3).trim());
    }
    return -1;
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

  private boolean jesuLiParametriIspravni(String icao) {
    if (icao == null || icao.length() < 2) {
      return false;
    }
    return icao.chars().allMatch(c -> Character.isLetterOrDigit(c) || c == '-');
  }

  private boolean jesuLiParametriIcao(String icaoFrom, String icaoTo) {
    return jesuLiParametriIspravni(icaoFrom) && jesuLiParametriIspravni(icaoTo);
  }

}
