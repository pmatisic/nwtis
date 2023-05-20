package org.foi.nwtis.pmatisic.zadaca_3.ws;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.AirportFacade;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

@WebService(serviceName = "meteo")
public class WsMeteo {

  @Inject
  AirportFacade airportFacade;

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @WebMethod
  public List<Aerodrom> dajSveAerodrome(@WebParam int odBroja, @WebParam int broj) {
    List<Airports> airports = airportFacade.findAll(odBroja, broj);
    List<Aerodrom> aerodromi = new ArrayList<>();
    for (Airports a : airports) {
      var koord = a.getCoordinates().split(",");
      var lokacija = new Lokacija(koord[1], koord[0]);
      aerodromi.add(new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija));
    }
    return aerodromi;
  }



}
