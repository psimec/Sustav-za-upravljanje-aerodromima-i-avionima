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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik;
import org.foi.nwtis.psimec.ejb.eb.Dnevnik_;
import org.foi.nwtis.psimec.ejb.eb.MqttPoruke;
import org.foi.nwtis.psimec.ejb.eb.MqttPoruke_;

/**
 *
 * @author Paskal
 */
@Stateless
public class MqttPorukeFacade extends AbstractFacade<MqttPoruke> {

    @PersistenceContext(unitName = "NWTiS_PROJEKT_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MqttPorukeFacade() {
        super(MqttPoruke.class);
    }

    public List<MqttPoruke> preuzmiKorisnikovePoruke(String korisnik) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<MqttPoruke> svePoruke = cq.from(MqttPoruke.class);
        List<Predicate> uvjeti = new ArrayList<>();
        uvjeti.add(cb.equal(svePoruke.get(MqttPoruke_.korime), korisnik));
        cq.where(uvjeti.toArray(new Predicate[]{}));
        cq.orderBy(cb.asc(svePoruke.get(MqttPoruke_.stored)));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public int obrisiKorisnikovePoruke(String korisnik) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaDelete cd = cb.createCriteriaDelete(MqttPoruke.class);
        Root poruke = cd.from(MqttPoruke.class);
        cd.where(cb.equal(poruke.get("korime"), korisnik));
        return em.createQuery(cd).executeUpdate();
    }

}
