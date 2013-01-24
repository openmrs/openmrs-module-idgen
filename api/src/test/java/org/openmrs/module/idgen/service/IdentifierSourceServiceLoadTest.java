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

    @Test
    @NotTransactional
    public void testThatWeDoNotGenerateTheSameIdentifierTwiceUnderHeavyLoad() throws Exception {
        int NUM_THREADS = 500;
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
                        generated.add(service.generateIdentifier(generator, "thread"));
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

        Assert.assertEquals(NUM_THREADS, generated.size());
        Assert.assertEquals(NUM_THREADS, new HashSet<String>(generated).size());
    }

}
