package ru.edwgiz.test.rest.framework.hk2.persistence;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.*;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public final class TransactionalInterceptionService implements InterceptionService {

    private final List<MethodInterceptor> METHOD_INTERCEPTORS;


    @Inject
    public TransactionalInterceptionService(ServiceLocator locator) {
        METHOD_INTERCEPTORS = Collections.singletonList(new TransactionalInterceptor(locator));
    }

    @Override
    public Filter getDescriptorFilter() {
        return new Filter() {
            @Override
            public boolean matches(Descriptor d) {
                if(d.getDescriptorType() == DescriptorType.CLASS) {
                    try {
                        return Class.forName(d.getImplementation()).isAnnotationPresent(Intercepted.class);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
                return false;
            }
        };
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if (method.isAnnotationPresent(Transactional.class)) {
            return METHOD_INTERCEPTORS;
        }
        return null;
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}
