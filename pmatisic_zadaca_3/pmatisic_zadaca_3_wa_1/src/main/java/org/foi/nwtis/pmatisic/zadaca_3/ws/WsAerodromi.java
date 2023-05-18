package org.foi.nwtis.pmatisic.zadaca_3.ws;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.AirportFacade;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.UdaljenostKlasa;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  @Inject
  AirportFacade airportFacade;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @WebMethod
  public List<Aerodrom> dajSveAerodrome(@WebParam int odBroja, @WebParam int broj) {
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

    return aerodromi;
  }

  @WebMethod
  public Aerodrom dajAerodrom(@WebParam String icao) {
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

    for (Aerodrom a : aerodromi) {
      if (a.getIcao().compareTo(icao) == 0) {
        return a;
      }
    }
    return null;
  }

  @WebMethod
  public List<UdaljenostKlasa> dajUdaljenostiAerodroma(@WebParam String icaoOd,
      @WebParam String icaoDo) {
    return null;
  }

  @WebMethod
  public Aerodrom dajAerodromJpa(@WebParam String icao) {
    Aerodrom aerodrom = null;
    if (icao == null || icao.trim().length() == 0) {
      return aerodrom;
    }
    Airports a = airportFacade.find(icao);
    var koord = a.getCoordinates().split(",");
    var lokacija = new Lokacija(koord[1], koord[0]);
    aerodrom = new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija);
    return aerodrom;
  }

}
