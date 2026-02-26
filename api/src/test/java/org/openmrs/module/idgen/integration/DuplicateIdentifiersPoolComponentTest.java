package org.openmrs.module.idgen.integration;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Tests out the synchronization problem where duplicate identifiers are assigned
 */
public class DuplicateIdentifiersPoolComponentTest extends IdgenBaseTest {

    public static final int NUM_THREADS = 25;

    @Before
    public void setUp() throws Exception {
        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
        IdentifierPool identifierPool = (IdentifierPool) getService().getIdentifierSource(4);
        List<String> identifiers = new ArrayList<String>();
        for (int i = 1; i <= NUM_THREADS; ++i) {
            identifiers.add(new String("" + i));
        }
        getService().addIdentifiersToPool(identifierPool, identifiers);
        getService().saveIdentifierSource(identifierPool);
        Context.flushSession();
    }

    @Test
    @Rollback(false)
    public void testUnderLoad() throws Exception {

        // Commit test data so threads with their own sessions can see it
        TestTransaction.flagForCommit();
        TestTransaction.end();
        TestTransaction.start();

        final List<String> generated = Collections.synchronizedList(new ArrayList<String>());
        final Object authLock = new Object();

        List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NUM_THREADS; ++i) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Context.openSession();
                    try {
                        synchronized (authLock) {
                            Context.authenticate("admin", "test");
                        }
                        IdentifierSource source = getService().getIdentifierSource(4);
                        sleep(100);
                        generated.addAll(getService().generateIdentifiers(source, 1, "thread"));
                        sleep(100);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    finally {
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
            }
            catch (InterruptedException e) {
            }
        }

        assertThat(generated.size(), is(NUM_THREADS));
        assertThat(new HashSet<String>(generated).size(), is(NUM_THREADS));
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        }
        catch (InterruptedException ex) {
        }
    }

    public IdentifierSourceService getService() {
        return Context.getService(IdentifierSourceService.class);
    }
}
