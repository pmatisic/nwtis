package org.foi.nwtis.pmatisic.zadaca_2.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
      @QueryParam("odBroja") Integer odBroja, @QueryParam("broj") Integer broj) {

    if (dan == null || dan.isEmpty()) {
      return Response.status(400).build();
    }

    if (odBroja == null) {
      odBroja = 1;
    }

    if (broj == null) {
      broj = 20;
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

}
