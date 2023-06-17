package org.foi.nwtis.pmatisic.projekt.servis;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.podatak.Aerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.zrno.AerodromiLetoviFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  @Inject
  AerodromiLetoviFacade alFacade;

  @Inject
  KorisniciFacade korisniciFacade;

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @WebMethod
  public List<Aerodrom> dajAerodromeZaLetove(@WebParam String korisnik, @WebParam String lozinka) {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      List<Airports> airports = alFacade.dajAerodromeZaLetove();
      List<Aerodrom> aerodromi = new ArrayList<>();
      for (Airports a : airports) {
        var koord = a.getCoordinates().split(",");
        var lokacija = new Lokacija(koord[1], koord[0]);
        aerodromi.add(new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija));
      }
      return aerodromi;
    }
    return null;
  }

  @WebMethod
  public boolean dodajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      Airports aerodrom = alFacade.dajAerodromPremaIcao(icao);
      if (aerodrom != null) {
        // Možete dodati logiku za postavljanje statusa i slanje obavijesti putem WebSocket-a
        return alFacade.dodajAerodromZaLetove(aerodrom);
      }
    }
    return false;
  }

  @WebMethod
  public boolean pauzirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      Airports aerodrom = alFacade.dajAerodromPremaIcao(icao);
      if (aerodrom != null) {
        // Ovdje postavite logiku za postavljanje statusa aerodroma na pauzu
        // Na primjer, aerodrom.setPauza(true);
        return alFacade.urediAerodromZaLetove(aerodrom);
      }
    }
    return false;
  }

  @WebMethod
  public boolean aktivirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      Airports aerodrom = alFacade.dajAerodromPremaIcao(icao);
      if (aerodrom != null) {
        // Ovdje postavite logiku za postavljanje statusa aerodroma na aktivan
        // Na primjer, aerodrom.setAktivan(true);
        return alFacade.urediAerodromZaLetove(aerodrom);
      }
    }
    return false;
  }

}
