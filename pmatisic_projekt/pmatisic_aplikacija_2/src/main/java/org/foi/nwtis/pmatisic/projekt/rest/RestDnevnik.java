package org.foi.nwtis.pmatisic.projekt.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.podatak.Dnevnik;
import com.google.gson.Gson;
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
  javax.sql.DataSource ds;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dohvatiZapise(@QueryParam("vrsta") String vrsta,
      @QueryParam("odBroja") Integer odBroja, @QueryParam("broj") Integer broj) {
    if (odBroja == null)
      odBroja = 1;
    if (broj == null)
      broj = 20;

    int offset = (odBroja - 1) * broj;

    try (Connection con = ds.getConnection()) {
      String query = "SELECT * FROM DNEVNIK WHERE (? IS NULL OR vrsta = ?) LIMIT ? OFFSET ?";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setObject(1, vrsta);
      stmt.setObject(2, vrsta);
      stmt.setInt(3, broj);
      stmt.setInt(4, offset);
      ResultSet rs = stmt.executeQuery();

      if (!rs.next()) {
        return Response.status(404).entity("Zapisi nisu pronađeni.").build();
      }

      List<Dnevnik> zapisi = new ArrayList<>();

      while (rs.next()) {
        String vrstaZapisa = rs.getString("vrsta");
        Timestamp vrijemePristupa = rs.getTimestamp("vrijeme_pristupa");
        String putanja = rs.getString("putanja");
        String ipAdresa = rs.getString("ip_adresa");
        int korisnik = rs.getInt("korisnik");

        zapisi.add(new Dnevnik(vrstaZapisa, vrijemePristupa, putanja, ipAdresa, korisnik));
      }

      if (zapisi.isEmpty()) {
        return Response.status(404).build();
      }

      Gson gson = new Gson();
      String podaci = gson.toJson(zapisi);
      return Response.ok().entity(podaci).build();

    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response dodajZapis(Dnevnik zapis) {
    try (Connection con = ds.getConnection()) {
      String query =
          "INSERT INTO DNEVNIK (vrsta, vrijeme_pristupa, putanja, ip_adresa, korisnik) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement stmt = con.prepareStatement(query);
      stmt.setString(1, zapis.vrsta());
      stmt.setTimestamp(2, zapis.vrijemePristupa());
      stmt.setString(3, zapis.putanja());
      stmt.setString(4, zapis.ipAdresa());
      stmt.setInt(5, zapis.korisnik());

      int brojAzuriranihRedova = stmt.executeUpdate();

      if (brojAzuriranihRedova > 0) {
        return Response.status(Response.Status.CREATED).build();
      } else {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }

    } catch (SQLIntegrityConstraintViolationException ex) {
      return Response.status(Response.Status.CONFLICT)
          .entity("Kršenje ograničenja integriteta: " + ex.getMessage()).build();
    } catch (Exception ex) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
    }
  }

}
