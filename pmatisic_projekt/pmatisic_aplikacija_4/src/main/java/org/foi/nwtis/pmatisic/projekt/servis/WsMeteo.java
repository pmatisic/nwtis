package org.foi.nwtis.pmatisic.projekt.servis;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@WebService(serviceName = "meteo")
public class WsMeteo {

  @Inject
  private ServletContext konfig;

  @WebMethod
  public MeteoPodaci dajMeteo(@WebParam String icao) {
    Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
    OWMKlijent owmKlijent =
        new OWMKlijent(konfiguracija.dajPostavku("OpenWeatherMap.apikey").toString());
    String url = (konfiguracija.dajPostavku("rest.url")).toString();

    Client client = ClientBuilder.newClient();
    String restUrl = url + icao;
    Response response = client.target(restUrl).request(MediaType.APPLICATION_JSON).get();

    if (response.getStatus() != 200) {
      return null;
    }

    String responseBody = response.readEntity(String.class);
    Gson gson = new Gson();
    Aerodrom aerodrom = gson.fromJson(responseBody, Aerodrom.class);

    String gpsCoordinates =
        aerodrom.getLokacija().getLatitude() + "," + aerodrom.getLokacija().getLongitude();
    String[] latlon = gpsCoordinates.split(",");
    String lat = latlon[1];
    String lon = latlon[0];
    MeteoPodaci meteoPodaci = null;

    try {
      meteoPodaci = owmKlijent.getRealTimeWeather(lon, lat);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }

    return meteoPodaci;
  }

}
