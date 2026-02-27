package org.openmrs.module.idgen;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Rewires all TransactionInterceptor beans to use TestTransactionAttributeSource.
 * OpenMRS 2.8.x adds &lt;tx:annotation-driven proxy-target-class="true"/&gt; which creates a
 * CGLIB auto-proxy for BaseIdentifierSourceService with its own internal TransactionInterceptor.
 * That interceptor uses an auto-generated AnnotationTransactionAttributeSource (not the named
 * "transactionAttributeSource" bean), so it is not affected by the TestTransactionAttributeSource
 * bean override alone.  This post-processor patches every TransactionInterceptor bean definition
 * to reference the named bean, ensuring REQUIRES_NEW is downgraded to REQUIRED in both proxies.
 */
public class TestTransactionInterceptorConfigurer implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String name : beanFactory.getBeanNamesForType(TransactionInterceptor.class, true, false)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(name);
            bd.getPropertyValues().addPropertyValue("transactionAttributeSource",
                    new RuntimeBeanReference("transactionAttributeSource"));
        }
    }
}