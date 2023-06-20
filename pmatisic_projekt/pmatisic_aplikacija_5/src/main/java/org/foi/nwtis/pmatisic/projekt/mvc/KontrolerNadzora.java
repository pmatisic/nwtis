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
  public Response posaljiKomandu(String podatak) {
    try {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(podatak, JsonObject.class);
      String komanda = jsonObject.get("komanda").getAsString();
      Response response = null;

      System.out.println("Sending command: " + komanda);

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

      System.out.println("Response status: " + response.getStatus());

      if (response.getStatus() == Response.Status.OK.getStatusCode()) {
        String rezultat = response.readEntity(String.class);

        System.out.println("Response body: " + rezultat);

        return Response.ok(rezultat, MediaType.APPLICATION_JSON).build();
      } else {
        return Response.status(response.getStatus()).build();
      }
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("Pogreška u obradi komande: " + e.getMessage()).build();
    }
  }

}
