package org.foi.nwtis.pmatisic.zadaca_3.ws;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.zrna.AirportFacade;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;

@WebService(serviceName = "meteo")
public class WsMeteo {

  @Inject
  AirportFacade airportFacade;

  @Inject
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @WebMethod
  public MeteoPodaci dajMeteo(@WebParam String icao) {
    Airports airport = airportFacade.find(icao);
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    OWMKlijent owmKlijent =
        new OWMKlijent(konfiguracija.dajPostavku("OpenWeatherMap.apikey").toString());

    if (airport == null) {
      return null;
    }

    String gpsCoordinates = airport.getCoordinates();
    String[] latlon = gpsCoordinates.split(",");
    String lat = latlon[0];
    String lon = latlon[1];
    MeteoPodaci meteoPodaci = null;

    try {
      meteoPodaci = owmKlijent.getRealTimeWeather(lon, lat);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    return meteoPodaci;
  }

  @WebMethod
  public MeteoPodaci dajMeteoAdresa(@WebParam String adresa) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    OWMKlijent owmKlijent =
        new OWMKlijent(konfiguracija.dajPostavku("OpenWeatherMap.apikey").toString());
    LIQKlijent liqKlijent =
        new LIQKlijent(konfiguracija.dajPostavku("LocationIQ.apikey").toString());
    Lokacija lokacija = null;
    MeteoPodaci meteoPodaci = null;

    try {
      lokacija = liqKlijent.getGeoLocation(adresa);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    try {
      meteoPodaci = owmKlijent.getRealTimeWeather(lokacija.getLatitude(), lokacija.getLongitude());
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    return meteoPodaci;
  }

}
