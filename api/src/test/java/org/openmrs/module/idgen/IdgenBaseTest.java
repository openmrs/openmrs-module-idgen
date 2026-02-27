package org.openmrs.module.idgen;

import org.junit.jupiter.api.BeforeEach;
import org.openmrs.test.jupiter.BaseModuleContextSensitiveTest;

/**
 * Base class that can be used for an common functionality or modifications to the standard base module context sensitive test
 */
public abstract class IdgenBaseTest extends BaseModuleContextSensitiveTest {

    @BeforeEach
    public void before() {
        new IdgenModuleActivator().started();
    }

}
