package org.openmrs.module.idgen;

import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Method;

/**
 * Replaces REQUIRES_NEW with REQUIRED in tests to avoid H2 lock conflicts when a REQUIRES_NEW
 * transaction tries to update a row already locked by an outer REQUIRED test transaction.
 */
public class TestTransactionAttributeSource extends AnnotationTransactionAttributeSource {

    @Override
    public TransactionAttribute getTransactionAttribute(@NonNull Method method, Class<?> targetClass) {
        TransactionAttribute attr = super.getTransactionAttribute(method, targetClass);
        if (attr != null && attr.getPropagationBehavior() == DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW) {
            return new DefaultTransactionAttribute(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
        }
        return attr;
    }
}