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

  @WebMethod
  public UdaljenostAerodromDrzavaKlasa dajNajduljiPutDrzave(@WebParam String icao) {
    UdaljenostAerodromDrzavaKlasa najduziPut = admFacade.findMaxDistanceForCountry(icao);
    return najduziPut;
  }

}
