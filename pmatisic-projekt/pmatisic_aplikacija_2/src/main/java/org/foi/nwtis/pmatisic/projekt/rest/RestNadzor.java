package org.foi.nwtis.pmatisic.projekt.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import org.foi.nwtis.Konfiguracija;
import com.google.gson.Gson;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("nadzor")
@RequestScoped
public class RestNadzor {

  @Context
  private ServletContext konfig;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response provjeriStatus() {
    String komanda = "STATUS";
    try {
      String odgovor = spojiSeNaPosluzitelj(komanda);
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(200, odgovor));
      return Response.status(Response.Status.OK).entity(jsonOdgovor).build();
    } catch (Exception e) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400, e.getMessage()));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
  }

  @GET
  @Path("{komanda}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response posaljiKomandu(@PathParam("komanda") String komanda) {
    if (!komanda.equals("KRAJ") && !komanda.equals("INIT") && !komanda.equals("PAUZA")) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(
          new Rezultat(400, "Neispravna komanda. Komanda mora biti unesena velikim slovima."));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
    if (!komanda.equalsIgnoreCase("KRAJ") && !komanda.equalsIgnoreCase("INIT")
        && !komanda.equalsIgnoreCase("PAUZA")) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400, "Neispravna komanda."));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
    try {
      String odgovor = spojiSeNaPosluzitelj(komanda.toUpperCase());
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(200, odgovor));
      return Response.status(Response.Status.OK).entity(jsonOdgovor).build();
    } catch (Exception e) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400, e.getMessage()));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
  }

  @GET
  @Path("INFO/{vrsta}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response posaljiInfoKomandu(@PathParam("vrsta") String vrsta) {
    if (!vrsta.equals("DA") && !vrsta.equals("NE")) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400,
          "Neispravna vrsta za INFO komandu. Mora biti unesena velikim slovima."));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
    if (!vrsta.equalsIgnoreCase("DA") && !vrsta.equalsIgnoreCase("NE")) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400, "Neispravna vrsta za INFO komandu."));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
    try {
      String odgovor = spojiSeNaPosluzitelj("INFO " + vrsta.toUpperCase());
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(200, odgovor));
      return Response.status(Response.Status.OK).entity(jsonOdgovor).build();
    } catch (Exception e) {
      Gson gson = new Gson();
      String jsonOdgovor = gson.toJson(new Rezultat(400, e.getMessage()));
      return Response.status(Response.Status.BAD_REQUEST).entity(jsonOdgovor).build();
    }
  }

  public String spojiSeNaPosluzitelj(String komanda) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    String adresaPosluzitelja = konfiguracija.dajPostavku("adresa.posluzitelja");
    Integer mreznaVrataPosluzitelja =
        Integer.parseInt(konfiguracija.dajPostavku("mreznaVrata.posluzitelja"));
    try (Socket socket = new Socket(adresaPosluzitelja, mreznaVrataPosluzitelja);
        BufferedReader citac = new BufferedReader(
            new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        BufferedWriter pisac = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")))) {

      pisac.write(komanda);
      pisac.flush();
      socket.shutdownOutput();
      String odgovor = citac.readLine();
      socket.shutdownInput();
      if (odgovor.startsWith("OK")) {
        return odgovor;
      } else {
        throw new RuntimeException(odgovor);
      }
    } catch (IOException e) {
      throw new RuntimeException("Pogreška pri komunikaciji s poslužiteljem", e);
    }
  }

  private static class Rezultat {
    @SuppressWarnings("unused")
    private final int status;
    @SuppressWarnings("unused")
    private final String opis;

    public Rezultat(int status, String opis) {
      this.status = status;
      this.opis = opis;
    }
  }

}
