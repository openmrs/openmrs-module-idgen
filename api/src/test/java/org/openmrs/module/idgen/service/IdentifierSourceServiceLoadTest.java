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

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.test.annotation.NotTransactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Can't run this in the same test as the other IdentifierSourceService tests
 */
public class IdentifierSourceServiceLoadTest extends BaseModuleContextSensitiveTest {

    public static final int NUM_THREADS = 500;
    public static final int NUM_PER_THREAD = 10;

    @NotTransactional
    @Test
    public void testThatWeDoNotGenerateTheSameIdentifierTwiceUnderHeavyLoad() throws Exception {
        final IdentifierSourceService service = Context.getService(IdentifierSourceService.class);

        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setName("Generator");
        generator.setIdentifierType(Context.getPatientService().getPatientIdentifierType(2));
        generator.setBaseCharacterSet("1234567890");
        generator.setFirstIdentifierBase("1");
        service.saveIdentifierSource(generator);
        final Integer generatorId = generator.getId();

        AutoGenerationOption opts = new AutoGenerationOption();
        opts.setIdentifierType(Context.getPatientService().getPatientIdentifierType(2));
        opts.setSource(generator);
        opts.setAutomaticGenerationEnabled(true);
        service.saveAutoGenerationOption(opts);

        final List<String> generated = new ArrayList<String>();

        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NUM_THREADS; ++i) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Context.openSession();
                    try {
                        authenticate();
                        IdentifierSource generator = service.getIdentifierSource(generatorId);
                        generated.addAll(service.generateIdentifiers(generator, NUM_PER_THREAD, "thread"));
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

        // make sure we generated the right number of ids
        Assert.assertEquals(NUM_THREADS * NUM_PER_THREAD, generated.size());

        // make sure each id is distinct
        Assert.assertEquals(NUM_THREADS * NUM_PER_THREAD, new HashSet<String>(generated).size());

        // we need to mark this test as @NotTransactional to properly test threading, so clean up after ourselves...
        service.purgeIdentifierSource(generator);
        service.purgeAutoGenerationOption(opts);
    }

}
