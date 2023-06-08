package org.foi.nwtis.pmatisic.projekt.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import org.foi.nwtis.Konfiguracija;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

@Path("nadzor")
@RequestScoped
public class RestNadzor {

  @Context
  private ServletContext konfig;

  @GET
  public Response provjeriStatus() {
    String komanda = "STATUS";
    try {
      String odgovor = spojiSeNaPosluzitelj(komanda);
      return Response.status(Response.Status.OK).entity("{status: 200, opis: \"" + odgovor + "\"}")
          .build();
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("{komanda}")
  public Response posaljiKomandu(@PathParam("komanda") String komanda) {
    if (!komanda.equalsIgnoreCase("KRAJ") && !komanda.equalsIgnoreCase("INIT")
        && !komanda.equalsIgnoreCase("PAUZA")) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Neispravna komanda.").build();
    }
    try {
      String odgovor = spojiSeNaPosluzitelj(komanda.toUpperCase());
      return Response.status(Response.Status.OK).entity("{status: 200, opis: \"" + odgovor + "\"}")
          .build();
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("INFO/{vrsta}")
  public Response posaljiInfoKomandu(@PathParam("vrsta") String vrsta) {
    if (!vrsta.equalsIgnoreCase("DA") && !vrsta.equalsIgnoreCase("NE")) {
      return Response.status(Response.Status.BAD_REQUEST)
          .entity("Neispravna vrsta za INFO komandu.").build();
    }
    try {
      String odgovor = spojiSeNaPosluzitelj("INFO " + vrsta.toUpperCase());
      return Response.status(Response.Status.OK).entity("{status: 200, opis: \"" + odgovor + "\"}")
          .build();
    } catch (Exception e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
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
      if (odgovor.startsWith("OK ")) {
        return odgovor.substring(3);
      } else {
        throw new RuntimeException("Neočekivani odgovor od poslužitelja: " + odgovor);
      }
    } catch (IOException e) {
      throw new RuntimeException("Pogreška pri komunikaciji s poslužiteljem", e);
    }
  }
}
