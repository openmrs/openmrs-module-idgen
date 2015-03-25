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
package org.openmrs.module.idgen.integration;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.EmptyIdentifierPoolException;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

public class IdentifierPoolSchedulerIT extends IdgenBaseTest {

    @Before
    public void setUp() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }

    @Test(expected = EmptyIdentifierPoolException.class)
    public void shouldNotGetMoreIdentifiersOnDemandIfConfiguredToUseScheduledTask() throws Exception {
        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(4); // Configured to refill from scheduler
        getService().generateIdentifier(pool, "this will fail");
    }

    @Test
    public void shouldGetMoreIdentifiersOnDemandIfNotConfiguredToUseScheduledTask() throws Exception {

        // register a stub RemoteIdentifierSourceProcessor that won't really go to the internet
        RemoteIdentifierSourceProcessorStub remoteProcessorStub = new RemoteIdentifierSourceProcessorStub();
        remoteProcessorStub.setBatchSize(3);
        getService().registerProcessor(RemoteIdentifierSource.class, remoteProcessorStub);

        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(5); // Configured to not refill from scheduler
        getService().generateIdentifier(pool, "this will work");
        Assert.assertTrue(pool.getAvailableIdentifiers().size() > 0);
        Assert.assertTrue(pool.getUsedIdentifiers().size() == 1);
    }

    @Test
    @Ignore("I don't know of a way to get the timer task to actually commit to the DB in this same transaction")
    public void shouldGetMoreIdentifiersOnScheduleWhenConfigured() throws Exception {
        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(4); // Configured to refill from scheduler
        Thread.sleep(12000);
        Assert.assertTrue(pool.getAvailableIdentifiers().size() > 0);
    }

    public IdentifierSourceService getService() {
        return Context.getService(IdentifierSourceService.class);
    }
}
