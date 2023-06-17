package org.foi.nwtis.pmatisic.projekt.zrno;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import org.foi.nwtis.pmatisic.projekt.entitet.LetoviPolasci;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Stateless
public class LetoviPolasciFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("LetoviPolasciFacade - init");
    cb = em.getCriteriaBuilder();
  }

  public List<LetAviona> dohvatiLetovePoIntervalu(String icao, LocalDate datumOd, LocalDate datumDo,
      int odBroja, int broj) {
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    Predicate icaoPred = cb.equal(root.get("airport").get("icao"), icao);
    Predicate datumOdPred =
        cb.greaterThanOrEqualTo(root.get("stored"), Timestamp.valueOf(datumOd.atStartOfDay()));
    Predicate datumDoPred =
        cb.lessThanOrEqualTo(root.get("stored"), Timestamp.valueOf(datumDo.atStartOfDay()));
    cq.where(cb.and(icaoPred, datumOdPred, datumDoPred));
    TypedQuery<LetoviPolasci> query = em.createQuery(cq);
    query.setFirstResult(odBroja);
    query.setMaxResults(broj);
    List<LetoviPolasci> rezultati = query.getResultList();
    List<LetAviona> letovi = new ArrayList<>();
    for (LetoviPolasci lp : rezultati) {
      LetAviona let = new LetAviona();
      let.setIcao24(lp.getIcao24());
      let.setFirstSeen(lp.getFirstSeen());
      let.setEstDepartureAirport(lp.getAirport().getIcao());
      let.setLastSeen(lp.getLastSeen());
      let.setEstArrivalAirport(lp.getEstArrivalAirport());
      let.setCallsign(lp.getCallsign());
      let.setEstDepartureAirportHorizDistance(lp.getEstDepartureAirportHorizDistance());
      let.setEstDepartureAirportVertDistance(lp.getEstDepartureAirportVertDistance());
      let.setEstArrivalAirportHorizDistance(lp.getEstArrivalAirportHorizDistance());
      let.setEstArrivalAirportVertDistance(lp.getEstArrivalAirportVertDistance());
      let.setDepartureAirportCandidatesCount(lp.getDepartureAirportCandidatesCount());
      let.setArrivalAirportCandidatesCount(lp.getArrivalAirportCandidatesCount());
      letovi.add(let);
    }
    return letovi;
  }

  public List<LetAviona> dohvatiLetoveNaDan(String icao, LocalDate datum, int odBroja, int broj) {
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    Predicate icaoPred = cb.equal(root.get("airport").get("icao"), icao);
    Predicate datumOdPred =
        cb.greaterThanOrEqualTo(root.get("stored"), Timestamp.valueOf(datum.atStartOfDay()));
    Predicate datumDoPred = cb.lessThanOrEqualTo(root.get("stored"),
        Timestamp.valueOf(datum.plusDays(1).atStartOfDay()));
    cq.where(cb.and(icaoPred, datumOdPred, datumDoPred));
    TypedQuery<LetoviPolasci> query = em.createQuery(cq);
    query.setFirstResult(odBroja);
    query.setMaxResults(broj);
    List<LetoviPolasci> rezultati = query.getResultList();
    List<LetAviona> letovi = new ArrayList<>();
    for (LetoviPolasci lp : rezultati) {
      LetAviona let = new LetAviona();
      let.setIcao24(lp.getIcao24());
      let.setFirstSeen(lp.getFirstSeen());
      let.setEstDepartureAirport(lp.getAirport().getIcao());
      let.setLastSeen(lp.getLastSeen());
      let.setEstArrivalAirport(lp.getEstArrivalAirport());
      let.setCallsign(lp.getCallsign());
      let.setEstDepartureAirportHorizDistance(lp.getEstDepartureAirportHorizDistance());
      let.setEstDepartureAirportVertDistance(lp.getEstDepartureAirportVertDistance());
      let.setEstArrivalAirportHorizDistance(lp.getEstArrivalAirportHorizDistance());
      let.setEstArrivalAirportVertDistance(lp.getEstArrivalAirportVertDistance());
      let.setDepartureAirportCandidatesCount(lp.getDepartureAirportCandidatesCount());
      let.setArrivalAirportCandidatesCount(lp.getArrivalAirportCandidatesCount());
      letovi.add(let);
    }
    return letovi;
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
      if (!icao24FirstSeenPostoje(let.getIcao24(), let.getFirstSeen())) {
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

  public boolean icao24FirstSeenPostoje(String icao24, int firstSeen) {
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
