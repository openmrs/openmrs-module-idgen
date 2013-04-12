/*
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

package org.openmrs.module.idgen.service;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.NotTransactional;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 *
 */
@Ignore
public class DuplicateIdentifiersFromPoolRegressionTest extends BaseModuleContextSensitiveTest {

    public static final int NUM_THREADS = 10;

    @Autowired
    private IdentifierSourceService service;

    @Autowired @Qualifier("patientService")
    private PatientService patientService;

    private IdentifierPool identifierPool;

//    @Override
//    public Properties getRuntimeProperties() {
//        Properties properties = super.getRuntimeProperties();
//        properties.setProperty("hibernate.show_sql", "true");
//        return properties;
//    }

    @Before
    @Transactional
    public void setUp() throws Exception {
        identifierPool = new IdentifierPool();
        identifierPool.setName("Test pool");
        identifierPool.setSequential(false);
        identifierPool.setIdentifierType(patientService.getPatientIdentifierType(2));
        for (int i = 1; i <= NUM_THREADS; ++i) {
            identifierPool.addIdentifierToPool("" + i);
        }
        service.saveIdentifierSource(identifierPool);
        Context.flushSession();
        Context.evictFromSession(identifierPool);
    }

    @Test
    @NotTransactional
    @DirtiesContext
    public void testUnderLoad() throws Exception {

        final List<String> generated = new ArrayList<String>();

        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NUM_THREADS; ++i) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Context.openSession();
                    try {
                        authenticate();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            // pass
                        }
                        IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
                        IdentifierSource freshFetchOfPool = service.getIdentifierSource(identifierPool.getId());
                        generated.addAll(service.generateIdentifiers(freshFetchOfPool, 1, "thread"));
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            // pass
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        Context.closeSession();
                    }
                }
            });
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // pass
            }
        }

        for (String id : generated) {
            System.out.println(id);
        }

        assertThat(generated.size(), is(NUM_THREADS));
        assertThat(new HashSet<String>(generated).size(), is(NUM_THREADS));
    }

}
