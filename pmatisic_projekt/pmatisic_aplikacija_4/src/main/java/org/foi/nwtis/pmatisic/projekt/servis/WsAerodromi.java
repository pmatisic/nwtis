package org.foi.nwtis.pmatisic.projekt.servis;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.iznimka.PogresnaAutentikacija;
import org.foi.nwtis.pmatisic.projekt.podatak.Aerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.websocket.WsInfo;
import org.foi.nwtis.pmatisic.projekt.zrno.AerodromiLetoviFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  @Inject
  AerodromiLetoviFacade alFacade;

  @Inject
  KorisniciFacade korisniciFacade;

  @WebMethod
  public List<Aerodrom> dajAerodromeZaLetove(@WebParam String korisnik, @WebParam String lozinka)
      throws PogresnaAutentikacija {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      List<Airports> airports = alFacade.dajAerodromePovezaneSAerodromimaLetova();
      List<Aerodrom> aerodromi = new ArrayList<>();
      for (Airports a : airports) {
        var koord = a.getCoordinates().split(",");
        var lokacija = new Lokacija(koord[1], koord[0]);
        aerodromi.add(new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija));
      }
      return aerodromi;
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
  }

  @WebMethod
  public boolean dodajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) throws PogresnaAutentikacija {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      if (alFacade.dodajAerodromZaLetove(icao)) {
        WsInfo.sendInfo(alFacade);
        return true;
      }
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
    return false;
  }

  @WebMethod
  public boolean pauzirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) throws PogresnaAutentikacija {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      return alFacade.pauzirajAerodrom(icao);
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
  }

  @WebMethod
  public boolean aktivirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) throws PogresnaAutentikacija {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      return alFacade.aktivirajAerodrom(icao);
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
  }

}
