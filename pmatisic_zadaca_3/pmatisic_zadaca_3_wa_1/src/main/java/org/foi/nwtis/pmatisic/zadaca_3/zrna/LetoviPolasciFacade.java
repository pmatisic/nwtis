package org.foi.nwtis.pmatisic.zadaca_3.zrna;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.LetoviPolasci;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.ServletContext;

/**
 * @author Petar Matišić (pmatisic@foi.hr)
 */
@Stateless
public class LetoviPolasciFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("LetoviPolasciFacade- init");
    cb = em.getCriteriaBuilder();
  }

  public void dodajLet(LetAviona let, Airports airport) {
    if (let.getEstArrivalAirport() == null) {
      System.out.println("EstArrivalAirport je null. Let nije dodan.");
      return;
    } else if (let.getEstDepartureAirport() == null) {
      System.out.println("EstDepartureAirport je null. Let nije dodan.");
      return;
    } else if (airport == null) {
      System.out.println("Airport je null. Let nije dodan.");
      return;
    } else {
      if (!icao24Postoji(let.getIcao24(), let.getFirstSeen())) {
        LetoviPolasci noviLet = new LetoviPolasci();
        noviLet.setIcao24(let.getIcao24());
        noviLet.setCallsign(let.getCallsign());
        noviLet.setFirstSeen(let.getFirstSeen());
        noviLet.setLastSeen(let.getLastSeen());
        noviLet.setEstArrivalAirport(let.getEstArrivalAirport());
        noviLet.setArrivalAirportCandidatesCount(let.getArrivalAirportCandidatesCount());
        noviLet.setDepartureAirportCandidatesCount(let.getDepartureAirportCandidatesCount());
        noviLet.setEstArrivalAirportHorizDistance(let.getEstArrivalAirportHorizDistance());
        noviLet.setEstArrivalAirportVertDistance(let.getEstArrivalAirportVertDistance());
        noviLet.setEstDepartureAirportHorizDistance(let.getEstDepartureAirportHorizDistance());
        noviLet.setEstDepartureAirportVertDistance(let.getEstDepartureAirportVertDistance());
        noviLet.setAirport(airport);
        noviLet.setStored(new Timestamp(System.currentTimeMillis()));
        em.persist(noviLet);
      } else {
        System.out.println("Let s istim 'icao24' i 'firstSeen' već postoji. Let nije dodan.");
      }
    }
  }

  public LetoviPolasci zadnjiZapis() {
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    cq.select(root);
    cq.orderBy(cb.desc(root.get("id")));
    TypedQuery<LetoviPolasci> query = em.createQuery(cq);
    query.setMaxResults(1);
    return query.getSingleResult();
  }

  public LocalDate zadnjiDatumPolaska(ServletContext konfig) {
    CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    cq.select(root.get("firstSeen"));
    cq.orderBy(cb.desc(root.get("firstSeen")));
    TypedQuery<Integer> query = em.createQuery(cq);
    query.setMaxResults(1);
    Integer firstSeen = null;
    try {
      firstSeen = query.getSingleResult();
    } catch (NoResultException e) {
      System.out.println("Nema zapisa u bazi podataka!");
    }
    if (firstSeen != null) {
      Instant instant = Instant.ofEpochSecond(firstSeen.longValue());
      return instant.atZone(ZoneOffset.UTC).toLocalDate();
    } else {
      Konfiguracija konfiguracija = (Konfiguracija) konfig.getAttribute("konfiguracija");
      String pocetniDanString = konfiguracija.dajPostavku("preuzimanje.od").toString();
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
      return LocalDate.parse(pocetniDanString, dtf);
    }
  }

  public boolean icao24Postoji(String icao24, int firstSeen) {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    Predicate firstSeenPred = cb.equal(root.get("firstSeen"), firstSeen);
    Predicate icao24Pred = cb.equal(root.get("icao24"), icao24);
    cq.where(cb.and(firstSeenPred, icao24Pred));
    TypedQuery<LetoviPolasci> q = em.createQuery(cq);
    q.setMaxResults(1);
    List<LetoviPolasci> zadnjiLet = q.getResultList();
    return !zadnjiLet.isEmpty();
  }

}
