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
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.service.IdentifierSourceService;

/**
 * Tests setting up a local pool, that pulls from a remote pool, and generates for an identifier type
 */
public class RemoteWithLocalPoolIntegrationTest extends IdgenBaseTest {

	@Before
	public void setUp() throws Exception {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
	}

	@Test
	public void testConfigurePoolFilledFromRemoteSource() throws Exception {

		IdentifierSourceService service = Context.getService(IdentifierSourceService.class);

		// register a stub RemoteIdentifierSourceProcessor that won't really go to the internet
		RemoteIdentifierSourceProcessorStub remoteProcessorStub = new RemoteIdentifierSourceProcessorStub();
		remoteProcessorStub.setBatchSize(3);
		service.registerProcessor(RemoteIdentifierSource.class, remoteProcessorStub);

		IdentifierPool pool = (IdentifierPool)service.getIdentifierSource(5);
		PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierType(4);

		// the first time we request an identifier it should make 2 remote requests with batchSize=3, and then give us one of those
		Assert.assertEquals("1", service.generateIdentifier(idType, "First"));
		Assert.assertEquals("Pool should have 5 available", 5, service.getQuantityInPool(pool, true, false));
		Assert.assertEquals("Pool should have 1 used", 1, service.getQuantityInPool(pool, false, true));
		Assert.assertEquals(2, remoteProcessorStub.getTimesCalled());

		// the next two requests should not make remote requests
		Assert.assertEquals("2", service.generateIdentifier(idType, "Second"));
		Assert.assertEquals("Pool should have 4 available", 4, service.getQuantityInPool(pool, true, false));
		Assert.assertEquals("Pool should have 2 used", 2, service.getQuantityInPool(pool, false, true));
		Assert.assertEquals("3", service.generateIdentifier(idType, "Third"));
		Assert.assertEquals("Pool should have 3 available", 3, service.getQuantityInPool(pool, true, false));
		Assert.assertEquals("Pool should have 3 used", 3, service.getQuantityInPool(pool, false, true));
		Assert.assertEquals(2, remoteProcessorStub.getTimesCalled());

		// since we're below our min pool size, the next request will make a remote request
		Assert.assertEquals("4", service.generateIdentifier(idType, "Fourth"));
		Assert.assertEquals("Pool should have 5 available", 5, service.getQuantityInPool(pool, true, false));
		Assert.assertEquals("Pool should have 4 used", 4, service.getQuantityInPool(pool, false, true));
		Assert.assertEquals(3, remoteProcessorStub.getTimesCalled());
	}
}
