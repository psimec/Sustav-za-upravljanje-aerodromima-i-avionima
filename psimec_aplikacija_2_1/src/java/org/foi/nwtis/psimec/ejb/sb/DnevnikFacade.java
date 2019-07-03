/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.psimec.ejb.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik_;

/**
 *
 * @author Paskal
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {

    @PersistenceContext(unitName = "NWTiS_PROJEKT_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }

    public List<Dnevnik> preuzmiKorisnikoveZapise(String korisnik) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Dnevnik> sviZapisi = cq.from(Dnevnik.class);
        List<Predicate> uvjeti = new ArrayList<>();
        uvjeti.add(cb.equal(sviZapisi.get(Dnevnik_.korime), korisnik));
        cq.where(uvjeti.toArray(new Predicate[]{}));
        cq.orderBy(cb.asc(sviZapisi.get(Dnevnik_.stored)));

        return getEntityManager().createQuery(cq).getResultList();
    }

}
