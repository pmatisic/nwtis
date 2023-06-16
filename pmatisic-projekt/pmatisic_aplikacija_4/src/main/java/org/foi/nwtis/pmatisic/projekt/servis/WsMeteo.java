package org.foi.nwtis.pmatisic.projekt.servis;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportFacade;
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

/**
 * Web servis koji pruža meteo podatke za aerodrome i adrese.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@WebService(serviceName = "meteo")
public class WsMeteo {

  @Inject
  AirportFacade airportFacade;

  @Inject
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dohvaća trenutne meteo podatke za aerodrom na temelju ICAO koda.
   *
   * @param icao ICAO kod aerodroma.
   * @return Trenutni meteo podaci za aerodrom.
   */
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

  /**
   * Dohvaća trenutne meteo podatke za adresu.
   *
   * @param adresa Adresa za koju se dohvaćaju meteo podaci.
   * @return Trenutni meteo podaci za adresu.
   */
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

    if (lokacija != null) {
      try {
        meteoPodaci =
            owmKlijent.getRealTimeWeather(lokacija.getLatitude(), lokacija.getLongitude());
      } catch (NwtisRestIznimka e) {
        e.printStackTrace();
      }
    }

    return meteoPodaci;
  }

}
