/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.idgen.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.DaemonToken;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleFactory;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.IdgenModuleActivator;

import java.lang.reflect.Method;

public class IdgenTaskIT extends IdgenBaseTest {

    @Before
    public void mockStartingModule() throws Exception {
        Module idgenModule = new Module("idgen");
        idgenModule.setModuleId("idgen");
        idgenModule.setModuleActivator(new IdgenModuleActivator());

        Method passDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("passDaemonToken", Module.class);
        passDaemonTokenMethod.setAccessible(true);
        passDaemonTokenMethod.invoke(null, idgenModule);

        Method getDaemonTokenMethod = ModuleFactory.class.getDeclaredMethod("getDaemonToken", Module.class);
        getDaemonTokenMethod.setAccessible(true);
        DaemonToken token = (DaemonToken)getDaemonTokenMethod.invoke(null, idgenModule);

        IdgenTask.setDaemonToken(token);
        IdgenTask.setEnabled(true);
    }

    @Test
    public void timerShouldFireAndShouldAuthenticate() throws Exception {
        TestTask.reset();
        Thread.sleep(10000);
        Assert.assertTrue(TestTask.hasRun());
        Assert.assertTrue(TestTask.wasAuthenticated());
    }
}
