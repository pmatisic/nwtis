package org.foi.nwtis.pmatisic.projekt.servis;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.iznimka.PogresnaAutentikacija;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.LetoviPolasciFacade;
import org.foi.nwtis.rest.klijenti.OSKlijentBP;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;

@WebService(serviceName = "letovi")
public class WsLetovi {

  @Inject
  private ServletContext konfig;

  @Inject
  AirportFacade airportFacade;

  @Inject
  LetoviPolasciFacade lpFacade;

  @Inject
  KorisniciFacade korisniciFacade;

  @WebMethod
  public List<LetAviona> dajPolaskeInterval(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String danOd, @WebParam String danDo,
      @WebParam Integer odBroja, @WebParam Integer broj) throws Exception {
    if (!korisniciFacade.autenticiraj(korisnik, lozinka)) {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }

    if (odBroja < 1 || broj < 1) {
      odBroja = 1;
      broj = 20;
    }

    int offset = (odBroja - 1) * broj;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    LocalDate datumOd = null;
    LocalDate datumDo = null;

    try {
      datumOd = LocalDate.parse(danOd, formatter);
      datumDo = LocalDate.parse(danDo, formatter);
    } catch (Exception e) {
      throw new Exception("Neispravan format datuma. Molimo koristite format dd.MM.yyyy.");
    }
    if (datumOd.isAfter(datumDo)) {
      throw new Exception("Datum 'danOd' ne može biti nakon datuma 'danDo'.");
    }

    return lpFacade.dohvatiLetovePoIntervalu(icao, datumOd, datumDo, offset, broj);
  }

  @WebMethod
  public List<LetAviona> dajPolaskeNaDan(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String dan, @WebParam Integer odBroja,
      @WebParam Integer broj) throws Exception {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      if (odBroja < 1 || broj < 1) {
        odBroja = 1;
        broj = 20;
      }
      int offset = (odBroja - 1) * broj;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate datum = null;
      try {
        datum = LocalDate.parse(dan, formatter);
      } catch (Exception e) {
        throw new Exception("Neispravan format datuma. Molimo koristite format dd.MM.yyyy.");
      }
      return lpFacade.dohvatiLetoveNaDan(icao, datum, offset, broj);
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
  }

  @WebMethod
  public List<LetAviona> dajPolaskeNaDanOS(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String dan) throws Exception {
    if (korisniciFacade.autenticiraj(korisnik, lozinka)) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      LocalDate datum = null;
      try {
        datum = LocalDate.parse(dan, formatter);
      } catch (Exception e) {
        throw new Exception("Neispravan format datuma. Molimo koristite format dd.MM.yyyy.");
      }
      int odVremena = (int) datum.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
      int doVremena =
          (int) datum.plusDays(1).atStartOfDay().minusSeconds(1).toEpochSecond(ZoneOffset.UTC);
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String korisnikOSN = konfiguracija.dajPostavku("OpenSkyNetwork.korisnik").toString();
      String ldap = konfiguracija.dajPostavku("aai.ldap").toString();
      // String lozinka = konfiguracija.dajPostavku("OpenSkyNetwork.lozinka").toString();
      // OSKlijent osKlijent = new OSKlijent(korisnik, lozinka);
      OSKlijentBP osKlijent = new OSKlijentBP(ldap, korisnikOSN);
      List<LetAviona> avioniPolasci = osKlijent.getDepartures(icao, odVremena, doVremena);
      for (LetAviona let : avioniPolasci) {
        Airports aerodrom = airportFacade.find(let.getEstDepartureAirport());
        lpFacade.dodajLet(let, aerodrom);
      }
      return avioniPolasci;
    } else {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
  }

}
