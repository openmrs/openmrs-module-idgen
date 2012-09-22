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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.processor.RemoteIdentifierSourceProcessor;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.test.BaseModuleContextSensitiveTest;


/**
 * Tests setting up a local pool, that pulls from a remote pool, and generates for an identifier type
 */
public class RemoteWithLocalPoolIntegrationTest extends BaseModuleContextSensitiveTest {
	
	@Test
	public void testConfigurePoolFilledFromRemoteSource() throws Exception {

		IdentifierSourceService service = Context.getService(IdentifierSourceService.class);
		
		// register a stub RemoteIdentifierSourceProcessor that won't really go to the internet
		RemoteIdentifierSourceProcessorStub remoteProcessorStub = new RemoteIdentifierSourceProcessorStub();
		service.registerProcessor(RemoteIdentifierSource.class, remoteProcessorStub);
		
		PatientIdentifierType idType = Context.getPatientService().getPatientIdentifierType(2);
		
		// configure a remote source
		RemoteIdentifierSource remoteSource = new RemoteIdentifierSource();
		remoteSource.setName("Remote source to fetch from");
		remoteSource.setUrl("http://urlforremotesource/openmrs/idgen?batchSize={batchSize}");
		remoteSource.setIdentifierType(idType); 
		service.saveIdentifierSource(remoteSource);
		
		// configure a local pool (that is fed by that remote source)
		IdentifierPool pool = new IdentifierPool();
		pool.setName("Local pool to assign from");
		pool.setSource(remoteSource);
		pool.setIdentifierType(idType);
		pool.setMinPoolSize(4);
		pool.setBatchSize(3);
		pool.setSequential(true);
		service.saveIdentifierSource(pool);
		
		// set up auto generation from the local pool (not the remote source)
		AutoGenerationOption autoGen = new AutoGenerationOption();
		autoGen.setIdentifierType(idType);
		autoGen.setSource(pool);
		autoGen.setManualEntryEnabled(false);
		autoGen.setAutomaticGenerationEnabled(true);
		service.saveAutoGenerationOption(autoGen);

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

	
	/**
	 *
	 */
	public class RemoteIdentifierSourceProcessorStub extends RemoteIdentifierSourceProcessor {
		
		Stack<String> identifiers;
		int timesCalled = 0;
		
		public RemoteIdentifierSourceProcessorStub() {
			identifiers = new Stack<String>();
			for (int i = 10; i > 0; --i) {
				identifiers.add("" + i);
			}
		}

		/**
		 * @see org.openmrs.module.idgen.processor.RemoteIdentifierSourceProcessor#getInputStreamFrom(java.lang.String)
		 */
		@Override
		protected InputStream getInputStreamFrom(String url) throws IOException {
			++timesCalled;
		    Matcher matcher = Pattern.compile("batchSize=(\\d+)").matcher(url);
		    matcher.find();
		    Integer batchSize = Integer.valueOf(matcher.group(1));
		    String ret = "";
		    for (int i = 0; i < batchSize; ++i) {
		    	if (i > 0) {
		    		ret += "\n";
		    	}
		    	ret += identifiers.pop();
		    }
		    return new ByteArrayInputStream(ret.getBytes());
		}
		
		/**
		 * @return how many times {@link #getInputStreamFrom(String)} has been called
		 */
		public int getTimesCalled() {
			return timesCalled;
		}
		
	}

}
