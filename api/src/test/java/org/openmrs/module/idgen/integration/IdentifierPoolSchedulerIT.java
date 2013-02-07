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
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.BaseIdentifierSource;
import org.openmrs.module.idgen.EmptyIdentifierPoolException;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.BaseIdentifierSourceService;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.module.idgen.service.db.HibernateIdentifierSourceDAO;
import org.openmrs.module.idgen.service.db.IdentifierSourceDAO;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.NotTransactional;

import java.util.HashMap;
import java.util.Map;

public class IdentifierPoolSchedulerIT extends BaseModuleContextSensitiveTest {

    BaseIdentifierSourceService service;

    @Autowired
    IdentifierSourceDAO dao;

    @Before
    public void beforeEachTest() {
        service = new BaseIdentifierSourceServiceStub();
        service.setDao(dao);

        SequentialIdentifierGeneratorProcessor processor = new SequentialIdentifierGeneratorProcessor();
        processor.setIdentifierSourceService(service);

        Map<Class<? extends IdentifierSource>, IdentifierSourceProcessor> processors = new HashMap<Class<? extends IdentifierSource>, IdentifierSourceProcessor>();
        processors.put(SequentialIdentifierGenerator.class, processor);
        service.setProcessors(processors);
    }

    @Test(expected = EmptyIdentifierPoolException.class)
    public void shouldNotGetMoreIdentifiersOnDemandIfConfiguredToUseScheduledTask() throws Exception {
        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(2);

        SequentialIdentifierGenerator generator = buildGenerator(identifierType);
        service.saveIdentifierSource(generator);

        IdentifierPool pool = buildPool(identifierType, generator);
        pool.setRefillWithScheduledTask(true);
        service.saveIdentifierSource(pool);

        service.generateIdentifier(pool, "this will fail");
    }

    @Test
    public void shouldGetMoreIdentifiersOnDemandIfNotConfiguredToUseScheduledTask() throws Exception {
        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(2);

        SequentialIdentifierGenerator generator = buildGenerator(identifierType);
        service.saveIdentifierSource(generator);

        IdentifierPool pool = buildPool(identifierType, generator);
        pool.setRefillWithScheduledTask(false);
        service.saveIdentifierSource(pool);

        service.generateIdentifier(pool, "this will work");
        Assert.assertTrue(pool.getAvailableIdentifiers().size() > 0);
        Assert.assertTrue(pool.getUsedIdentifiers().size() == 1);
    }

    @Test
    @Ignore("I don't know of a way to get the timer task to actually commit to the DB in this same transaction")
    public void shouldGetMoreIdentifiersOnScheduleWhenConfigured() throws Exception {
        PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierType(2);

        SequentialIdentifierGenerator generator = buildGenerator(identifierType);
        service.saveIdentifierSource(generator);

        IdentifierPool pool = buildPool(identifierType, generator);
        pool.setRefillWithScheduledTask(true);
        service.saveIdentifierSource(pool);

        Assert.assertEquals(0, pool.getAvailableIdentifiers().size());

        Thread.sleep(12000);

        Assert.assertTrue(pool.getAvailableIdentifiers().size() > 0);
    }

    private IdentifierPool buildPool(PatientIdentifierType identifierType, SequentialIdentifierGenerator generator) {
        IdentifierPool pool = new IdentifierPool();
        pool.setName("Pool");
        pool.setMinPoolSize(1);
        pool.setBatchSize(5);
        pool.setSequential(true);
        pool.setSource(generator);
        pool.setIdentifierType(identifierType);
        return pool;
    }

    private SequentialIdentifierGenerator buildGenerator(PatientIdentifierType identifierType) {
        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setName("Generator");
        generator.setBaseCharacterSet("123456790");
        generator.setFirstIdentifierBase("1");
        generator.setIdentifierType(identifierType);
        return generator;
    }

    private class BaseIdentifierSourceServiceStub extends BaseIdentifierSourceService {

        // we need to override the functionality to get and set sequential values since we are now
        // bypassing Hibernate and going directly to the DB to do this

        private long sequenceValue;

        @Override
        public void saveSequenceValue(SequentialIdentifierGenerator seq, long sequenceValue) {
            this.sequenceValue = sequenceValue;
        }

        @Override
        public Long getSequenceValue(SequentialIdentifierGenerator seq) {
            return sequenceValue;
        }

    }
}
