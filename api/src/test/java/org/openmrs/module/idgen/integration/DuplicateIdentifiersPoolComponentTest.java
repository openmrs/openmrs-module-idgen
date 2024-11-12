package org.openmrs.module.idgen.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests out the synchronization problem where duplicate identifiers are assigned
 * <p>
 *     Updated for OpenMRS 2.x and Spring 4.x compatibility
 * </p>
 */
public class DuplicateIdentifiersPoolComponentTest extends IdgenBaseTest {

    private static final Log log = LogFactory.getLog(DuplicateIdentifiersPoolComponentTest.class);

    private static final int NUM_THREADS = 25;

    private static final int TIMEOUT_SECONDS = 30;

    private static final String TEST_DATASET = "org/openmrs/module/idgen/include/TestData.xml";

    private static final int IDENTIFIER_SOURCE_ID = 4;

    @Before
    @Transactional
    public void setUp() throws Exception {
        try {
            executeDataSet(TEST_DATASET);
            IdentifierPool identifierPool = (IdentifierPool) getIdentifierSourceService().getIdentifierSource(IDENTIFIER_SOURCE_ID);
            List<String> identifiers = new ArrayList<>();

            for (int i = 1; i <= NUM_THREADS; ++i) {
                identifiers.add(String.valueOf(i));
            }

            getIdentifierSourceService().addIdentifiersToPool(identifierPool, identifiers);
            getIdentifierSourceService().saveIdentifierSource(identifierPool);

            Context.flushSession();
            Context.clearSession();

        } catch (Exception e) {
            log.error("Error in test setup", e);
            throw e;
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void testUnderLoad() throws Exception {
        final List<String> generated = Collections.synchronizedList(new ArrayList<>());
        final CountDownLatch completionLatch = new CountDownLatch(NUM_THREADS);
        final AtomicReference<Exception> testException = getExceptionAtomicReference(generated, completionLatch);

        boolean completed = completionLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (testException.get() != null) {
            fail("Test failed with exception: " + testException.get().getMessage());
        }

        if (!completed) {
            fail("Test did not complete within " + TIMEOUT_SECONDS + " seconds");
        }

        assertThat("Generated list size should match thread count",
                generated.size(), is(NUM_THREADS));
        assertThat("All generated identifiers should be unique",
                new HashSet<>(generated).size(), is(NUM_THREADS));

        log.debug("Generated identifiers: " + generated);
    }

    private AtomicReference<Exception> getExceptionAtomicReference(List<String> generated, CountDownLatch completionLatch) {
        final AtomicReference<Exception> testException = new AtomicReference<>();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < NUM_THREADS; ++i) {
            final int threadNum = i;
            Thread thread = new Thread(() -> {
                try {
                    generateIdentifierInNewSession(generated, threadNum);
                } catch (Exception e) {
                    log.error("Error in thread " + threadNum, e);
                    testException.set(e);
                } finally {
                    completionLatch.countDown();
                }
            }, "IdGenThread-" + i);

            threads.add(thread);
            thread.start();
        }
        return testException;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void generateIdentifierInNewSession(List<String> generated, int threadNum) throws Exception {
        Context.openSession();
        try {
            Context.authenticate("admin", "test");
            IdentifierSource source = getIdentifierSourceService().getIdentifierSource(IDENTIFIER_SOURCE_ID);
            Thread.sleep(50);
            generated.addAll(getIdentifierSourceService().generateIdentifiers(source, 1, "thread-" + threadNum));
            Thread.sleep(50);

        } finally {
            Context.closeSession();
        }
    }

    private IdentifierSourceService getIdentifierSourceService() {
        return Context.getService(IdentifierSourceService.class);
    }
}
