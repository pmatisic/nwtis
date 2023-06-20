package org.foi.nwtis.pmatisic.projekt.mvc;

import java.util.Arrays;
import org.foi.nwtis.Konfiguracija;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Controller
@Path("nadzor")
@RequestScoped
public class KontrolerNadzora {

  @Inject
  private Models model;

  @Context
  private ServletContext konfig;

  private static final String REST_SERVICE_URL =
      "http://200.20.0.4:8080/pmatisic_aplikacija_2/api/nadzor";

  @GET
  @View("pogled_5_3.jsp")
  public void pocetak() {

    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String ime = (konfiguracija.dajPostavku("autor.ime")).toString();
    String prezime = (konfiguracija.dajPostavku("autor.prezime")).toString();
    String predmet = (konfiguracija.dajPostavku("autor.predmet")).toString();
    String godina = (konfiguracija.dajPostavku("aplikacija.godina")).toString();
    String verzija = (konfiguracija.dajPostavku("aplikacija.verzija")).toString();

    try {
      model.put("ime", ime);
      model.put("prezime", prezime);
      model.put("predmet", predmet);
      model.put("godina", godina);
      model.put("verzija", verzija);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response posaljiKomanduIDohvatiOdgovor(String podatak) {
    try {
      Response response = null;
      String komanda = new Gson().fromJson(podatak, JsonObject.class).get("komanda").getAsString();

      System.out.println("Šaljem komandu: " + komanda);

      if ("STATUS".equalsIgnoreCase(komanda)) {
        response = ClientBuilder.newClient().target(REST_SERVICE_URL).request().get();
      } else if (Arrays.asList("KRAJ", "INIT", "PAUZA").contains(komanda.toUpperCase())) {
        response = ClientBuilder.newClient().target(REST_SERVICE_URL).path(komanda).request().get();
      } else if (komanda.toUpperCase().startsWith("INFO")) {
        String[] parts = komanda.split(" ");
        if (parts.length == 2
            && ("DA".equalsIgnoreCase(parts[1]) || "NE".equalsIgnoreCase(parts[1]))) {
          response = ClientBuilder.newClient().target(REST_SERVICE_URL).path("INFO")
              .path(parts[1].toUpperCase()).request().get();
        }
      }

      if (response == null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Neispravna komanda").build();
      }

      if (response.getStatus() == 200) {
        String rezultat = response.readEntity(String.class).toString();
        System.out.println("Response body: " + rezultat);
        return Response.ok().build();
      } else {
        return Response.status(Status.BAD_REQUEST).build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Pogreška u obradi komande: " + e.getMessage()).build();
    }
  }

}
