package org.openmrs.module.idgen;

import org.junit.jupiter.api.BeforeEach;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;

/**
 * Base class that can be used for an common functionality or modifications to the standard base module context sensitive test
 */
public abstract class IdgenBaseTest extends BaseModuleContextSensitiveTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    public void before() {
        new IdgenModuleActivator().started();

        // disable the "idgenTimerFactory" so that it does not run a RefillIdentifierPoolsTask during tests
        Object idgenTimerFactory = applicationContext.getBean("idgenTimerFactory");
        if (idgenTimerFactory instanceof ExecutorService) {
            ((ExecutorService) idgenTimerFactory).shutdownNow();
        }
    }

}
