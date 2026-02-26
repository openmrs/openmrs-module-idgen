package org.openmrs.module.idgen;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.lang.reflect.Method;

/**
 * Test-only transaction attribute source that downgrades REQUIRES_NEW to REQUIRED.
 * This is necessary because H2 1.4.200's MVStore engine doesn't support reading
 * uncommitted data from suspended transactions, which causes REQUIRES_NEW to fail
 * in tests where data is loaded within a test transaction.
 */
public class TestTransactionAttributeSource extends AnnotationTransactionAttributeSource {

    @Override
    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        TransactionAttribute attr = super.getTransactionAttribute(method, targetClass);
        if (attr != null && attr.getPropagationBehavior() == TransactionDefinition.PROPAGATION_REQUIRES_NEW) {
            DefaultTransactionAttribute modified = new DefaultTransactionAttribute(attr);
            modified.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            return modified;
        }
        return attr;
    }
}
