package org.openmrs.module.idgen.integration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;
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
 * Tests out the synchronization problem where duplicate identifiers are assigned
 */
public class DuplicateIdentifiersComponentTest extends BaseModuleContextSensitiveTest {

    public static final int NUM_THREADS = 10;

    @Autowired
    private IdentifierSourceService service;

    @Autowired
    @Qualifier("patientService")
    private PatientService patientService;

    private IdentifierPool identifierPool;

    @Before
    @Transactional
    public void setUp() throws Exception {
        identifierPool = new IdentifierPool();
        identifierPool.setName("Test pool");
        identifierPool.setSequential(false);
        identifierPool.setIdentifierType(patientService.getPatientIdentifierType(2));
        service.saveIdentifierSource(identifierPool);

        List<String> identifiers = new ArrayList<String>();

        for (int i = 1; i <= NUM_THREADS; ++i) {
            identifiers.add(new String("" + i));
        }

        service.addIdentifiersToPool(identifierPool, identifiers);
        Context.flushSession();
        Context.evictFromSession(identifierPool);
    }

    @Test
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
                        generated.addAll(service.generateIdentifiers(identifierPool, 1, "thread"));
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

        assertThat(generated.size(), is(NUM_THREADS));
        assertThat(new HashSet<String>(generated).size(), is(NUM_THREADS));
    }

}

