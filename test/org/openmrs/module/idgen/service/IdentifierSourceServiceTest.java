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
package org.openmrs.module.idgen.service;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.openmrs.test.Verifies;

public class IdentifierSourceServiceTest extends BaseModuleContextSensitiveTest {
	
	private IdentifierSourceService iss;

	@Before
	public void initTestData() throws Exception {
		initializeInMemoryDatabase();
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
		authenticate();
		iss = Context.getService(IdentifierSourceService.class);
	}
	

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 */
	@Test
	@Verifies(value = "should return a saved sequential identifier generator", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedSequentialIdentifierGenerator() throws Exception {
		SequentialIdentifierGenerator sig = (SequentialIdentifierGenerator)iss.getIdentifierSource(1);
		Assert.assertEquals(sig.getName(), "Test Sequential Generator");
		Assert.assertNotNull(sig.getValidCharacters());
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 */
	@Test
	@Verifies(value = "should return a saved remote identifier source", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedRestIdentifierGenerator() throws Exception {
		RemoteIdentifierSource rig = (RemoteIdentifierSource)iss.getIdentifierSource(2);
		Assert.assertEquals(rig.getName(), "Test Remote Source");
		Assert.assertNotNull(rig.getUrl());
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 * 
	 */
	@Test
	@Verifies(value = "should return a saved identifier pool", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedIdentifierPool() throws Exception {
		IdentifierPool idpool = (IdentifierPool)iss.getIdentifierSource(3);
		Assert.assertEquals(idpool.getName(), "Test Identifier Pool");
		Assert.assertEquals(idpool.getBatchSize(), 1000);
		Assert.assertEquals(idpool.getAvailableIdentifiers().size(), 3);
		Assert.assertEquals(idpool.getUsedIdentifiers().size(), 2);
	}

	/**
	 * @see {@link IdentifierSourceService#purgeIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	@Verifies(value = "should delete an IdentifierSource from the system", method = "purgeIdentifierSource(IdentifierSource)")
	public void purgeIdentifierSource_shouldDeleteAnIdentifierSourceFromTheSystem() throws Exception {
		IdentifierSource s = iss.getIdentifierSource(2);
		iss.purgeIdentifierSource(s);
		Assert.assertNull(iss.getIdentifierSource(2));
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	@Verifies(value = "should save a sequential identifier generator for later retrieval", method = "saveIdentifierSource(IdentifierSource)")
	public void saveIdentifierSource_shouldSaveASequentialIdentifierGeneratorForLaterRetrieval() throws Exception {
		
		String name = "Sample Id Gen";
		String validChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		SequentialIdentifierGenerator sig = new SequentialIdentifierGenerator();
		sig.setName(name);
		sig.setValidCharacters(validChars);
		sig.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		IdentifierSource source = iss.saveIdentifierSource(sig);
		
		Assert.assertNotNull(source.getId());
		IdentifierSource s = iss.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getClass(), SequentialIdentifierGenerator.class);
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(((SequentialIdentifierGenerator)s).getValidCharacters(), validChars);
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	@Verifies(value = "should save a rest identifier generator for later retrieval", method = "saveIdentifierSource(IdentifierSource)")
	public void saveIdentifierSource_shouldSaveARestIdentifierGeneratorForLaterRetrieval() throws Exception {
		String name = "Sample Id Gen";
		String url = "http://localhost";
		
		RemoteIdentifierSource idgen = new RemoteIdentifierSource();
		idgen.setName(name);
		idgen.setUrl(url);
		idgen.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		IdentifierSource source = iss.saveIdentifierSource(idgen);
		
		Assert.assertNotNull(source.getId());
		IdentifierSource s = iss.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getClass(), RemoteIdentifierSource.class);
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(((RemoteIdentifierSource)s).getUrl(), url);
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	@Verifies(value = "should save an identifier pool for later retrieval", method = "saveIdentifierSource(IdentifierSource)")
	public void saveIdentifierSource_shouldSaveAnIdentifierPoolForLaterRetrieval() throws Exception {
		String name = "Sample Id Gen";
		int batchSize = 500;
		
		IdentifierPool pool = new IdentifierPool();
		pool.setName(name);
		pool.setBatchSize(batchSize);
		pool.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		
		Set<PooledIdentifier> pooledIdentifiers = new LinkedHashSet<PooledIdentifier>();
		pooledIdentifiers.add(new PooledIdentifier(pool, "ABC00"));
		pooledIdentifiers.add(new PooledIdentifier(pool, "ABC01"));
		pooledIdentifiers.add(new PooledIdentifier(pool, "ABC02"));
		pooledIdentifiers.add(new PooledIdentifier(pool, "ABC03"));
		pool.setIdentifiers(pooledIdentifiers);
		
		IdentifierSource source = iss.saveIdentifierSource(pool);
		
		Assert.assertNotNull(source.getId());
		IdentifierPool s = (IdentifierPool)iss.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(s.getBatchSize(), batchSize);
		Assert.assertEquals(4, s.getAvailableIdentifiers().size());
	}
}
