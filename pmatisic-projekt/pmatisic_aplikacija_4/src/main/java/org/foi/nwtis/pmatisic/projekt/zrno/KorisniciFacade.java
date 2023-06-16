package org.foi.nwtis.pmatisic.projekt.zrno;

import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Korisnici;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Stateless
public class KorisniciFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("KorisniciFacade - init");
    cb = em.getCriteriaBuilder();
  }

  public void create(Korisnici korisnici) {
    em.persist(korisnici);
  }

  public void edit(Korisnici korisnici) {
    em.merge(korisnici);
  }

  public void remove(Korisnici korisnici) {
    em.remove(em.merge(korisnici));
  }

  public Korisnici find(Object id) {
    return em.find(Korisnici.class, id);
  }

  public List<Korisnici> findAll() {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    cq.select(cq.from(Korisnici.class));
    return em.createQuery(cq).getResultList();
  }

  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Korisnici> rt = cq.from(Korisnici.class);
    cq.select(cb.count(rt));
    Query q = em.createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

  public List<Korisnici> findKorisnikeByImeAndPrezime(String ime, String prezime) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> korisnikRoot = cq.from(Korisnici.class);
    Predicate filterIme = null;
    Predicate filterPrezime = null;
    if (ime != null && !ime.isEmpty()) {
      filterIme = cb.like(korisnikRoot.get("ime"), "%" + ime + "%");
    }
    if (prezime != null && !prezime.isEmpty()) {
      filterPrezime = cb.like(korisnikRoot.get("prezime"), "%" + prezime + "%");
    }
    if (filterIme != null && filterPrezime != null) {
      cq.where(cb.and(filterIme, filterPrezime));
    } else if (filterIme != null) {
      cq.where(filterIme);
    } else if (filterPrezime != null) {
      cq.where(filterPrezime);
    }
    return em.createQuery(cq).getResultList();
  }

  public boolean autenticiraj(String korisnickoIme, String lozinka) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> korisnikRoot = cq.from(Korisnici.class);
    Predicate filterKorisnickoIme = cb.equal(korisnikRoot.get("korisnickoIme"), korisnickoIme);
    Predicate filterLozinka = cb.equal(korisnikRoot.get("lozinka"), lozinka);
    cq.where(cb.and(filterKorisnickoIme, filterLozinka));
    List<Korisnici> rezultati = em.createQuery(cq).getResultList();
    return !rezultati.isEmpty();
  }

  public Korisnici findKorisnikByKorisnickoIme(String korisnickoIme) {
    CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
    Root<Korisnici> korisnikRoot = cq.from(Korisnici.class);
    Predicate filterKorisnickoIme = cb.equal(korisnikRoot.get("korisnickoIme"), korisnickoIme);
    cq.where(filterKorisnickoIme);
    List<Korisnici> rezultati = em.createQuery(cq).getResultList();
    if (!rezultati.isEmpty()) {
      return rezultati.get(0);
    } else {
      return null;
    }
  }

}
