package org.openmrs.module.idgen;

import org.junit.Before;
import org.openmrs.test.BaseModuleContextSensitiveTest;

/**
 * Base class that can be used for an common functionality or modifications to the standard base module context sensitive test
 */
public abstract class IdgenBaseTest extends BaseModuleContextSensitiveTest {

    @Before
    public void setupActivator() {
        new IdgenModuleActivator().started();
    }
}
