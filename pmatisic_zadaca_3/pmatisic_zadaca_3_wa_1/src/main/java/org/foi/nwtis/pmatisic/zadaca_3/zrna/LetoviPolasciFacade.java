package org.foi.nwtis.pmatisic.zadaca_3.zrna;

import java.time.LocalDate;
import java.time.ZoneOffset;
import org.foi.nwtis.rest.podaci.LetAviona;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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

  public void dodajLet(LetAviona let) {
    em.persist(let);
  }

  public LetAviona zadnjiZapis() {
    CriteriaQuery<LetAviona> cq = cb.createQuery(LetAviona.class);
    Root<LetAviona> root = cq.from(LetAviona.class);
    cq.select(root);
    cq.orderBy(cb.desc(root.get("id"))); // Pretpostavljamo da je "id" primarni ključ LetAviona
    TypedQuery<LetAviona> query = em.createQuery(cq);
    query.setMaxResults(1);
    return query.getSingleResult();
  }

  public LocalDate zadnjiDatumPolaska() {
    CriteriaQuery<LetAviona> cq = cb.createQuery(LetAviona.class);
    Root<LetAviona> root = cq.from(LetAviona.class);
    cq.select(root.get("vrijeme_polaska")); // Pretpostavljamo da je "vrijeme_polaska" stupac za
                                            // vrijeme polaska u bazi podataka
    cq.orderBy(cb.desc(root.get("vrijeme_polaska")));
    TypedQuery<LocalDate> query = em.createQuery(cq);
    query.setMaxResults(1);
    return query.getSingleResult().atZone(ZoneOffset.UTC).toLocalDate();
  }

}
