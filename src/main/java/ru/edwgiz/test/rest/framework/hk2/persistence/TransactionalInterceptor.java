package ru.edwgiz.test.rest.framework.hk2.persistence;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.ServiceLocator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

final class TransactionalInterceptor implements MethodInterceptor {

    private final ServiceLocator locator;

    TransactionalInterceptor(ServiceLocator locator) {
        this.locator = locator;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        final EntityManager entityManager = locator.getService(EntityManager.class);
        final EntityTransaction tx = entityManager.getTransaction();
        boolean success = false;
        try {
            tx.begin();
            final Object result = methodInvocation.proceed();
            success = true;
            return result;
        } finally {
            if (tx.isActive()) {
                if (success && !tx.getRollbackOnly()) {
                    tx.commit();
                } else {
                    try {
                        tx.rollback();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }
}
