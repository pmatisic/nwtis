package org.foi.nwtis.pmatisic.projekt.zrno;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.AirportsDistanceMatrix;
import org.foi.nwtis.pmatisic.projekt.entitet.AirportsDistanceMatrixPK;
import org.foi.nwtis.pmatisic.projekt.podatak.UdaljenostAerodromDrzavaKlasa;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

@Stateless
public class AirportsDistanceMatrixFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("AirportsDistanceMatrixFacade- init");
    cb = em.getCriteriaBuilder();
  }

  public List<AirportsDistanceMatrix> findDistancesBetweenAirports(String icaoFrom, String icaoTo) {
    CriteriaQuery<AirportsDistanceMatrix> cq = cb.createQuery(AirportsDistanceMatrix.class);
    Root<AirportsDistanceMatrix> root = cq.from(AirportsDistanceMatrix.class);
    Join<AirportsDistanceMatrix, AirportsDistanceMatrixPK> join = root.join("id");
    cq.select(root).where(cb.equal(join.get("icaoFrom"), icaoFrom),
        cb.equal(join.get("icaoTo"), icaoTo));
    TypedQuery<AirportsDistanceMatrix> query = em.createQuery(cq);
    return query.getResultList();
  }

  public List<AirportsDistanceMatrix> findAllDistancesBetweenAirports(String icao, int firstResult,
      int maxResults) {
    CriteriaQuery<AirportsDistanceMatrix> cq = cb.createQuery(AirportsDistanceMatrix.class);
    Root<AirportsDistanceMatrix> root = cq.from(AirportsDistanceMatrix.class);
    Join<AirportsDistanceMatrix, AirportsDistanceMatrixPK> join = root.join("id");
    cq.select(root).distinct(true).where(cb.equal(join.get("icaoFrom"), icao));
    TypedQuery<AirportsDistanceMatrix> query = em.createQuery(cq);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    List<AirportsDistanceMatrix> results = query.getResultList();
    return results != null ? results : new ArrayList<>();
  }

  public UdaljenostAerodromDrzavaKlasa findMaxDistanceForCountry(String icao) {
    CriteriaQuery<AirportsDistanceMatrix> cq = cb.createQuery(AirportsDistanceMatrix.class);
    Root<AirportsDistanceMatrix> root = cq.from(AirportsDistanceMatrix.class);
    Join<AirportsDistanceMatrix, AirportsDistanceMatrixPK> joinMaxDist = root.join("id");
    cq.select(root);
    cq.where(cb.equal(joinMaxDist.get("icaoFrom"), icao));
    cq.orderBy(cb.desc(root.get("distCtry")));
    TypedQuery<AirportsDistanceMatrix> queryMaxDist = em.createQuery(cq);
    queryMaxDist.setMaxResults(1);
    List<AirportsDistanceMatrix> results = queryMaxDist.getResultList();
    if (results.isEmpty()) {
      return null;
    }
    AirportsDistanceMatrix maxDistResult = results.get(0);
    String icaoTo = maxDistResult.getId().getIcaoTo();
    String country = maxDistResult.getId().getCountry();
    float maxDistCtry = maxDistResult.getDistCtry();
    return new UdaljenostAerodromDrzavaKlasa(icaoTo, country, maxDistCtry);
  }

}
