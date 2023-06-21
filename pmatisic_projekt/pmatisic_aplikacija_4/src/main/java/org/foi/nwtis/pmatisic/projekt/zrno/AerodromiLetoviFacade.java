package org.foi.nwtis.pmatisic.projekt.zrno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.foi.nwtis.pmatisic.projekt.entitet.AerodromiLetovi;
import org.foi.nwtis.pmatisic.projekt.entitet.Airports;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Stateless
public class AerodromiLetoviFacade {

  @PersistenceContext(unitName = "nwtis_dz3_pu")
  private EntityManager em;
  private CriteriaBuilder cb;

  @PostConstruct
  private void init() {
    System.out.println("AerodromiLetoviFacade - init");
    cb = em.getCriteriaBuilder();
  }

  public void create(AerodromiLetovi aerodromiLetovi) {
    em.persist(aerodromiLetovi);
  }

  public void edit(AerodromiLetovi aerodromiLetovi) {
    em.merge(aerodromiLetovi);
  }

  public void remove(AerodromiLetovi aerodromiLetovi) {
    em.remove(em.merge(aerodromiLetovi));
  }

  public AerodromiLetovi find(Object icao) {
    return em.find(AerodromiLetovi.class, icao);
  }

  public List<AerodromiLetovi> findAll() {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<AerodromiLetovi> cq = cb.createQuery(AerodromiLetovi.class);
    cq.select(cq.from(AerodromiLetovi.class));
    return em.createQuery(cq).getResultList();
  }

  public List<AerodromiLetovi> findAll(int odBroja, int broj) {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<AerodromiLetovi> cq = cb.createQuery(AerodromiLetovi.class);
    cq.select(cq.from(AerodromiLetovi.class));
    TypedQuery<AerodromiLetovi> q = em.createQuery(cq);
    q.setMaxResults(broj);
    q.setFirstResult(odBroja);
    return q.getResultList();
  }

  public int count() {
    cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> cq = cb.createQuery(Long.class);
    Root<AerodromiLetovi> rt = cq.from(AerodromiLetovi.class);
    cq.select(cb.count(rt));
    Query q = em.createQuery(cq);
    return ((Long) q.getSingleResult()).intValue();
  }

  public List<Airports> dajAerodromePovezaneSAerodromimaLetova() {
    try {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
      Root<Airports> airportsRoot = cq.from(Airports.class);
      Root<AerodromiLetovi> aerodromiLetoviRoot = cq.from(AerodromiLetovi.class);
      Predicate joinCondition = cb.equal(airportsRoot.get("icao"), aerodromiLetoviRoot.get("icao"));
      cq.select(airportsRoot).where(joinCondition);
      TypedQuery<Airports> query = em.createQuery(cq);
      return query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  public boolean dodajAerodromZaLetove(String icao) {
    try {
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Airports> cq = cb.createQuery(Airports.class);
      Root<Airports> airportsRoot = cq.from(Airports.class);
      cq.select(airportsRoot).where(cb.equal(airportsRoot.get("icao"), icao));
      Airports aerodrom = em.createQuery(cq).getSingleResult();
      if (aerodrom != null) {
        AerodromiLetovi noviAerodromLetovi = new AerodromiLetovi();
        noviAerodromLetovi.setIcao(icao);
        noviAerodromLetovi.setAktivan(true);
        noviAerodromLetovi.setAirports(aerodrom);
        em.persist(noviAerodromLetovi);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean pauzirajAerodrom(String icao) {
    try {
      AerodromiLetovi aerodromLetovi = em.find(AerodromiLetovi.class, icao);
      if (aerodromLetovi != null) {
        aerodromLetovi.setAktivan(false);
        em.merge(aerodromLetovi);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public boolean aktivirajAerodrom(String icao) {
    try {
      AerodromiLetovi aerodromLetovi = em.find(AerodromiLetovi.class, icao);
      if (aerodromLetovi != null) {
        aerodromLetovi.setAktivan(true);
        em.merge(aerodromLetovi);
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public Map<String, Boolean> dohvatiStatusePreuzimanja() {
    try {
      List<AerodromiLetovi> aerodromiLetovi = findAll();
      Map<String, Boolean> statusiPreuzimanja = new HashMap<>();
      for (AerodromiLetovi aerodromLet : aerodromiLetovi) {
        statusiPreuzimanja.put(aerodromLet.getIcao(), aerodromLet.isAktivan());
      }
      return statusiPreuzimanja;
    } catch (Exception e) {
      e.printStackTrace();
      return new HashMap<>();
    }
  }

}
