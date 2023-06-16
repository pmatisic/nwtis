package org.foi.nwtis.pmatisic.projekt.servis;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.entitet.AirportsDistanceMatrix;
import org.foi.nwtis.pmatisic.projekt.podatak.Aerodrom;
import org.foi.nwtis.pmatisic.projekt.podatak.Lokacija;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodromDrzavaKlasa;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodromKlasa;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostKlasa;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportFacade;
import org.foi.nwtis.pmatisic.projekt.zrno.AirportsDistanceMatrixFacade;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

/**
 * Web servis koji pruža informacije o aerodromima.
 * 
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  @Inject
  AirportFacade airportFacade;

  @Inject
  AirportsDistanceMatrixFacade admFacade;

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dohvaća sve aerodrome s određenim offsetom i limitom.
   *
   * @param odBroja Početna pozicija dohvaćanja.
   * @param broj Broj aerodroma koji treba dohvatiti.
   * @return Lista aerodroma.
   */
  @WebMethod
  public List<Aerodrom> dajSveAerodrome(@WebParam int odBroja, @WebParam int broj) {
    if (odBroja < 1 || broj < 1) {
      odBroja = 1;
      broj = 20;
    }
    int offset = (odBroja - 1) * broj;
    List<Airports> airports = airportFacade.findAll(offset, broj);
    List<Aerodrom> aerodromi = new ArrayList<>();
    for (Airports a : airports) {
      var koord = a.getCoordinates().split(",");
      var lokacija = new Lokacija(koord[1], koord[0]);
      aerodromi.add(new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija));
    }
    return aerodromi;
  }

  /**
   * Dohvaća informacije o aerodromu na temelju ICAO koda.
   *
   * @param icao ICAO kod aerodroma.
   * @return Informacije o aerodromu.
   */
  @WebMethod
  public Aerodrom dajAerodrom(@WebParam String icao) {
    Aerodrom aerodrom = null;
    if (icao == null || icao.trim().length() == 0) {
      return aerodrom;
    }
    Airports a = airportFacade.find(icao);
    if (a != null) {
      var koord = a.getCoordinates().split(",");
      var lokacija = new Lokacija(koord[1], koord[0]);
      aerodrom = new Aerodrom(a.getIcao(), a.getName(), a.getIsoCountry(), lokacija);
    }
    return aerodrom;
  }

  /**
   * Dohvaća udaljenosti između dva aerodroma.
   *
   * @param icaoOd ICAO kod prvog aerodroma.
   * @param icaoDo ICAO kod drugog aerodroma.
   * @return Lista udaljenosti između aerodroma.
   */
  @WebMethod
  public List<UdaljenostKlasa> dajUdaljenostiAerodroma(@WebParam String icaoOd,
      @WebParam String icaoDo) {
    List<AirportsDistanceMatrix> udaljenosti =
        admFacade.findDistancesBetweenAirports(icaoOd, icaoDo);
    List<UdaljenostKlasa> podaci = new ArrayList<>();
    for (AirportsDistanceMatrix udaljenost : udaljenosti) {
      String drzava = udaljenost.getId().getCountry();
      float km = udaljenost.getDistCtry();
      podaci.add(new UdaljenostKlasa(drzava, km));
    }
    return podaci;
  }

  /**
   * Dohvaća sve udaljenosti određenog aerodroma do ostalih aerodroma.
   *
   * @param icao ICAO kod aerodroma.
   * @param odBroja Početna pozicija dohvaćanja.
   * @param broj Broj udaljenosti koje treba dohvatiti.
   * @return Lista udaljenosti do ostalih aerodroma.
   */
  @WebMethod
  public List<UdaljenostAerodromKlasa> dajSveUdaljenostiAerodroma(@WebParam String icao,
      @WebParam int odBroja, @WebParam int broj) {
    if (odBroja < 1 || broj < 1) {
      odBroja = 1;
      broj = 20;
    }
    int offset = (odBroja - 1) * broj;
    List<AirportsDistanceMatrix> udaljenosti =
        admFacade.findAllDistancesBetweenAirports(icao, offset, broj);
    List<UdaljenostAerodromKlasa> podaci = new ArrayList<>();
    for (AirportsDistanceMatrix udaljenost : udaljenosti) {
      String icaoTo = udaljenost.getId().getIcaoTo();
      float km = udaljenost.getDistTot();
      podaci.add(new UdaljenostAerodromKlasa(icaoTo, km));
    }
    return podaci;
  }

  /**
   * Dohvaća najduži put unutar države za aerodrom.
   *
   * @param icao ICAO kod aerodroma.
   * @return Najduži put unutar države.
   */
  @WebMethod
  public UdaljenostAerodromDrzavaKlasa dajNajduljiPutDrzave(@WebParam String icao) {
    UdaljenostAerodromDrzavaKlasa najduziPut = admFacade.findMaxDistanceForCountry(icao);
    return najduziPut;
  }

}
