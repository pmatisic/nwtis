package org.foi.nwtis.pmatisic.projekt.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.foi.nwtis.pmatisic.projekt.podatak.Dnevnik;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("dnevnik")
@RequestScoped
public class RestDnevnik {

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dohvatiZapise(@QueryParam("vrsta") String vrsta,
      @QueryParam("odBroja") Integer odBroja, @QueryParam("broj") Integer broj) {

    if (odBroja == null) {
      odBroja = 1;
    }
    if (broj == null) {
      broj = 20;
    }

    int offset = (odBroja - 1) * broj;

    try (Connection con = ds.getConnection()) {
      String query = "SELECT * FROM DNEVNIK WHERE (? IS NULL OR vrsta = ?) LIMIT ? OFFSET ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setObject(1, vrsta);
      stmt.setObject(2, vrsta);
      stmt.setInt(3, broj);
      stmt.setInt(4, offset);

      ResultSet rs = stmt.executeQuery();
      List<Dnevnik> zapisi = new ArrayList<>();
      while (rs.next()) {
        String vrstaZapisa = rs.getString("vrsta");
        Timestamp vrijemePristupa = rs.getTimestamp("vrijeme_pristupa");
        String putanja = rs.getString("putanja");
        int korisnik = rs.getInt("korisnik");

        zapisi.add(new Dnevnik(vrstaZapisa, vrijemePristupa, putanja, korisnik));
      }
      return Response.ok(zapisi).build();

    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response dodajZapis(Dnevnik zapis) {
    try (Connection con = ds.getConnection()) {
      String query =
          "INSERT INTO DNEVNIK (vrsta, vrijeme_pristupa, putanja, korisnik) VALUES (?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, zapis.vrsta());
      stmt.setTimestamp(2, zapis.vrijemePristupa());
      stmt.setString(3, zapis.putanja());
      stmt.setInt(4, zapis.korisnik());

      stmt.executeUpdate();
      return Response.status(Response.Status.CREATED).build();

    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
  }

}
