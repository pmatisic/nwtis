package org.foi.nwtis.pmatisic.projekt.zrno;

import java.util.List;
import org.foi.nwtis.pmatisic.projekt.entitet.Korisnik;
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

  public void create(Korisnik korisnik) {
    em.persist(korisnik);
  }

  public void edit(Korisnik korisnik) {
    em.merge(korisnik);
  }

  public void remove(Korisnik korisnik) {
    em.remove(em.merge(korisnik));
  }

  public Korisnik find(Object id) {
    return em.find(Korisnik.class, id);
  }

  public List<Korisnik> findAll() {
    CriteriaQuery<Korisnik> cq = cb.createQuery(Korisnik.class);
    cq.select(cq.from(Korisnik.class));
    return em.createQuery(cq).getResultList();
  }

  public int count() {
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<Korisnik> rt = cq.from(Korisnik.class);
    cq.select(cb.count(rt));
    Query q = em.createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

  public List<Korisnik> findKorisnikeByImeAndPrezime(String ime, String prezime) {
    CriteriaQuery<Korisnik> cq = cb.createQuery(Korisnik.class);
    Root<Korisnik> korisnikRoot = cq.from(Korisnik.class);
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
    CriteriaQuery<Korisnik> cq = cb.createQuery(Korisnik.class);
    Root<Korisnik> korisnikRoot = cq.from(Korisnik.class);
    Predicate filterKorisnickoIme = cb.equal(korisnikRoot.get("korime"), korisnickoIme);
    Predicate filterLozinka = cb.equal(korisnikRoot.get("lozinka"), lozinka);
    cq.where(cb.and(filterKorisnickoIme, filterLozinka));
    List<Korisnik> rezultati = em.createQuery(cq).getResultList();
    return !rezultati.isEmpty();
  }

  public Korisnik findKorisnikByKorisnickoIme(String korisnickoIme) {
    CriteriaQuery<Korisnik> cq = cb.createQuery(Korisnik.class);
    Root<Korisnik> korisnikRoot = cq.from(Korisnik.class);
    Predicate filterKorisnickoIme = cb.equal(korisnikRoot.get("korime"), korisnickoIme);
    cq.where(filterKorisnickoIme);
    List<Korisnik> rezultati = em.createQuery(cq).getResultList();
    if (!rezultati.isEmpty()) {
      return rezultati.get(0);
    } else {
      return null;
    }
  }

}
