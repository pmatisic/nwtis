package org.foi.nwtis.pmatisic.projekt.zrno;

import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Dnevnik;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Stateless
public class DnevnikFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("DnevnikFacade - init");
    cb = em.getCriteriaBuilder();
  }

  public void create(Dnevnik dnevnik) {
    em.persist(dnevnik);
  }

  public void edit(Dnevnik dnevnik) {
    em.merge(dnevnik);
  }

  public void remove(Dnevnik dnevnik) {
    em.remove(em.merge(dnevnik));
  }

  public Dnevnik find(Object id) {
    return em.find(Dnevnik.class, id);
  }

  public List<Dnevnik> findAll() {
    CriteriaQuery<Dnevnik> cq = cb.createQuery(Dnevnik.class);
    cq.select(cq.from(Dnevnik.class));
    return em.createQuery(cq).getResultList();
  }

  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Dnevnik> rt = cq.from(Dnevnik.class);
    cq.select(cb.count(rt));
    Query q = em.createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

}
