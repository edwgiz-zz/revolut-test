package ru.edwgiz.test.rest.framework.hk2.persistence;

import org.glassfish.hk2.api.Factory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static javax.persistence.Persistence.createEntityManagerFactory;

public final class EmFactory implements Factory<EntityManager> {

    private static final EntityManagerFactory EMF = createEntityManagerFactory("TEST");

    @Override
    public EntityManager provide() {
        return EMF.createEntityManager();
    }

    @Override
    public void dispose(EntityManager em) {
        em.close();
    }
}
