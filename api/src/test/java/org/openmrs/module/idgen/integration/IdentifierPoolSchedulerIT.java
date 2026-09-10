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


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IdentifierPoolSchedulerIT extends IdgenBaseTest {

    @BeforeEach
    public void setUp() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }


    @Test
    public void shouldGetMoreIdentifiersOnDemandAsEmergencyFallbackWhenScheduledTaskPoolIsEmpty() throws Exception {
        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(4); // Configured to refill from scheduler, but scheduler hasn't run yet
        String identifier = getService().generateIdentifier(pool, "emergency fallback should still succeed");
        assertNotNull(identifier);
        assertEquals(1, pool.getUsedIdentifiers().size());
    }

    /**
     * The emergency on-demand fallback must not kick in when the pool already has enough stock
     * to satisfy the request -- otherwise a scheduled-task pool backed by a flaky/unreachable
     * remote source would pay for (and potentially fail because of) a synchronous refill attempt
     * on every request, even though it didn't need to.
     */
    @Test
    public void shouldNotAttemptOnDemandRefillWhenScheduledTaskPoolHasSufficientStock() throws Exception {
        RemoteIdentifierSourceProcessorStub remoteProcessorStub = new RemoteIdentifierSourceProcessorStub();
        remoteProcessorStub.setSimulateConnectivityFailure(true);
        getService().registerProcessor(RemoteIdentifierSource.class, remoteProcessorStub);

        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(9); // Configured to refill from scheduler, has stock, remote is unreachable
        String identifier = getService().generateIdentifier(pool, "should use existing stock without touching remote source");

        assertNotNull(identifier);
        assertEquals(0, remoteProcessorStub.getTimesCalled(), "on-demand refill should not be attempted while existing stock can satisfy the request");
    }

    @Test
    public void shouldGetMoreIdentifiersOnDemandIfNotConfiguredToUseScheduledTask() throws Exception {

        // register a stub RemoteIdentifierSourceProcessor that won't really go to the internet
        RemoteIdentifierSourceProcessorStub remoteProcessorStub = new RemoteIdentifierSourceProcessorStub();
        remoteProcessorStub.setBatchSize(3);
        getService().registerProcessor(RemoteIdentifierSource.class, remoteProcessorStub);

        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(5); // Configured to not refill from scheduler
        getService().generateIdentifier(pool, "this will work");
        assertFalse(pool.getAvailableIdentifiers().isEmpty());
        assertEquals(1, pool.getUsedIdentifiers().size());
    }

    @Test
    @Disabled("I don't know of a way to get the timer task to actually commit to the DB in this same transaction")
    public void shouldGetMoreIdentifiersOnScheduleWhenConfigured() throws Exception {
        IdentifierPool pool = (IdentifierPool)getService().getIdentifierSource(4); // Configured to refill from scheduler
        Thread.sleep(12000);
        assertFalse(pool.getAvailableIdentifiers().isEmpty());
    }

    public IdentifierSourceService getService() {
        return Context.getService(IdentifierSourceService.class);
    }
}
