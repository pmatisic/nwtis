package org.foi.nwtis.pmatisic.projekt.servis;

import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Korisnici;
import org.foi.nwtis.pmatisic.projekt.iznimka.PogresnaAutentikacija;
import org.foi.nwtis.pmatisic.projekt.websocket.WsInfo;
import org.foi.nwtis.pmatisic.projekt.zrno.KorisniciFacade;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.core.Context;

@WebService(serviceName = "korisnici")
public class WsKorisnici {

  @Inject
  KorisniciFacade korisniciFacade;

  @Context
  private ServletContext konfig;

  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  @WebMethod
  public List<Korisnici> dajKorisnike(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String traziImeKorisnika, @WebParam String traziPrezimeKorisnika)
      throws PogresnaAutentikacija {
    if (!korisniciFacade.autenticiraj(korisnik, lozinka)) {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
    if ((traziImeKorisnika == null || traziImeKorisnika.isEmpty())
        && (traziPrezimeKorisnika == null || traziPrezimeKorisnika.isEmpty())) {
      return korisniciFacade.findAll();
    }
    return korisniciFacade.findKorisnikeByImeAndPrezime(traziImeKorisnika, traziPrezimeKorisnika);
  }

  @WebMethod
  public Korisnici dajKorisnika(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String traziKorisnika) throws PogresnaAutentikacija {
    if (!korisniciFacade.autenticiraj(korisnik, lozinka)) {
      throw new PogresnaAutentikacija("Pogrešno korisničko ime ili lozinka.");
    }
    return korisniciFacade.findKorisnikByKorisnickoIme(traziKorisnika);
  }

  @WebMethod
  public boolean dodajKorisnika(@WebParam Korisnici korisnik) {
    try {
      korisniciFacade.create(korisnik);
      int ukupnoKorisnika = korisniciFacade.count();
      WsInfo.sendInfo(ukupnoKorisnika);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
