package org.foi.nwtis.pmatisic.projekt.zrno;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
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

  @PostConstruct
  private void init() {
    System.out.println("LetoviPolasciFacade - init");
  }

  public List<LetAviona> dohvatiLetovePoIntervalu(String icao, LocalDate datumOd, LocalDate datumDo,
      int odBroja, int broj) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    long epochDatumOd = datumOd.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    long epochDatumDo = datumDo.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    Predicate icaoPred = cb.equal(root.get("airport").get("icao"), icao);
    Predicate datumOdPred =
        cb.greaterThanOrEqualTo(root.get("firstSeen").as(Long.class), epochDatumOd);
    Predicate datumDoPred = cb.lessThan(root.get("firstSeen").as(Long.class), epochDatumDo);
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
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<LetoviPolasci> cq = cb.createQuery(LetoviPolasci.class);
    Root<LetoviPolasci> root = cq.from(LetoviPolasci.class);
    long epochDatum = datum.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    long epochSutrasnjiDatum = datum.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();
    Predicate icaoPred = cb.equal(root.get("airport").get("icao"), icao);
    Predicate datumOdPred =
        cb.greaterThanOrEqualTo(root.get("firstSeen").as(Long.class), epochDatum);
    Predicate datumDoPred = cb.lessThan(root.get("firstSeen").as(Long.class), epochSutrasnjiDatum);
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

}
