package org.foi.nwtis.pmatisic.zadaca_3.zrna;

import java.util.List;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.Airports;
import org.foi.nwtis.pmatisic.zadaca_3.jpa.AirportsDistanceMatrix;
import org.foi.nwtis.podaci.Airport;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * @author Petar Matišić (pmatisic@foi.hr)
 * @author Dragutin Kermek
 */
@Stateless
public class AirportFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("AirportFacade- init");
    cb = em.getCriteriaBuilder();
  }

  public void create(Airports airport) {
    em.persist(airport);
  }

  public void edit(Airports airport) {
    em.merge(airport);
  }

  public void remove(Airport Airport) {
    em.remove(em.merge(Airport));
  }

  public Airports find(Object id) {
    return em.find(Airports.class, id);
  }

  public List<Airports> findAll() {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
    cq.select(cq.from(Airports.class));
    return em.createQuery(cq).getResultList();
  }

  public List<Airports> findAll(int odBroja, int broj) {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
    cq.select(cq.from(Airports.class));
    TypedQuery<Airports> q = em.createQuery(cq);
    q.setMaxResults(broj);
    q.setFirstResult(odBroja);
    return q.getResultList();
  }

  public int count() {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Airports> rt = cq.from(Airports.class);
    cq.select(cb.count(rt));
    Query q = em.createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

  public Airports findAirportsByIcao(String icao) {
    CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
    Root<Airports> root = cq.from(Airports.class);
    cq.where(cb.equal(root.get("icao"), icao));
    TypedQuery<Airports> query = em.createQuery(cq);
    return query.getSingleResult();
  }

  public List<AirportsDistanceMatrix> findDistancesFromAirport(String icao) {
    CriteriaQuery<AirportsDistanceMatrix> cq = cb.createQuery(AirportsDistanceMatrix.class);
    Root<AirportsDistanceMatrix> root = cq.from(AirportsDistanceMatrix.class);
    cq.where(cb.equal(root.get("icao_from"), icao));
    TypedQuery<AirportsDistanceMatrix> query = em.createQuery(cq);
    return query.getResultList();
  }

  public List<AirportsDistanceMatrix> findDistancesWithinCountry(String icao) {
    CriteriaQuery<AirportsDistanceMatrix> cq = cb.createQuery(AirportsDistanceMatrix.class);
    Root<AirportsDistanceMatrix> root = cq.from(AirportsDistanceMatrix.class);
    cq.where(cb.and(cb.equal(root.get("icao_from"), icao),
        cb.equal(root.get("country"), findAirportsByIcao(icao).getIsoCountry())));
    TypedQuery<AirportsDistanceMatrix> query = em.createQuery(cq);
    return query.getResultList();
  }

}
