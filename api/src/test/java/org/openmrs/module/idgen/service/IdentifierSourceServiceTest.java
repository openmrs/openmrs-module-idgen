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

import junit.framework.Assert;
import org.hibernate.NonUniqueResultException;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.*;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;
import org.openmrs.module.idgen.processor.SequentialIdentifierGeneratorProcessor;
import org.openmrs.module.idgen.service.db.IdentifierSourceDAO;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public class IdentifierSourceServiceTest extends IdgenBaseTest {

	@Autowired
	IdentifierSourceService iss;

    @Autowired
    IdentifierSourceDAO dao;

    @Autowired
    @Qualifier("patientService")
    PatientService patientService;

    @Autowired
    @Qualifier("locationService")
    LocationService locationService;

    @Before
    public void beforeEachTest() throws Exception {

        executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }
	
	/**
	 * @see {@link IdentifierSourceService#generateIdentifiers(IdentifierSource, Integer, String)}
	 */
	@Test
	@Verifies(value = "should return batch of ID of correct size", method = "generateIdentifiers(IdentifierSource, integer, String)")
	public void generateIdentifiers_shouldReturnIdentifiersOfCorrectSize() throws Exception {
		IdentifierSource is = iss.getIdentifierSource(1);
		List<String>  sig = iss.generateIdentifiers(is, 7, "hello");
		Assert.assertEquals(sig.toString(), "[G-0, H-8, I-5, J-3, K-1, L-9, M-7]");
	}

	/**
	 * @see {@link IdentifierSourceService#getAllIdentifierSources(boolean)}
	 */
	@Test
	@Verifies(value = "should return all identifier sources", method = "getAllIdentifierSources(boolean)")
	public void getAllIdentifierSources_shouldReturnAllIdentifierSources() throws Exception {
		List<IdentifierSource>  sig = iss.getAllIdentifierSources(false);
		Assert.assertTrue(sig.size() == 6);
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 */
	@Test
	@Verifies(value = "should return a saved sequential identifier generator", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedSequentialIdentifierGenerator() throws Exception {
		SequentialIdentifierGenerator sig = (SequentialIdentifierGenerator)iss.getIdentifierSource(1);
		Assert.assertEquals(sig.getName(), "Test Sequential Generator");
		Assert.assertNotNull(sig.getBaseCharacterSet());
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
		Assert.assertEquals(5, idpool.getAvailableIdentifiers().size());
		Assert.assertEquals(idpool.getUsedIdentifiers().size(), 0);
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
		String baseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		SequentialIdentifierGenerator sig = new SequentialIdentifierGenerator();
		sig.setName(name);
		sig.setBaseCharacterSet(baseChars);
		sig.setFirstIdentifierBase("1");
		sig.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		IdentifierSource source = iss.saveIdentifierSource(sig);
		
		Assert.assertNotNull(source.getId());
		IdentifierSource s = iss.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getClass(), SequentialIdentifierGenerator.class);
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(((SequentialIdentifierGenerator) s).getBaseCharacterSet(), baseChars);
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
		Assert.assertEquals(((RemoteIdentifierSource) s).getUrl(), url);
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

    @Test
    public void getIdentifierSourceByUuid_shouldGetSource() {
        IdentifierSource identifierSource = iss.getIdentifierSourceByUuid("0d47284f-9e9b-4a81-a88b-8bb42bc0a903");
        Assert.assertEquals(3, identifierSource.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifier_shouldGetAutoGenerationOptionByIdentifier() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
        AutoGenerationOption autoGenerationOption = iss.getAutoGenerationOption(patientIdentifierType);
        Assert.assertEquals(1, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldGetAutoGenerationOptionByIdentifier() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        Location location = locationService.getLocation(2);
        AutoGenerationOption autoGenerationOption = iss.getAutoGenerationOption(patientIdentifierType, location);
        Assert.assertEquals(3, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnNullIfNoOptionForLocation() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        Location location = locationService.getLocation(4);
        AutoGenerationOption autoGenerationOption = iss.getAutoGenerationOption(patientIdentifierType, location);
        Assert.assertNull(autoGenerationOption);
    }

    @Test(expected = NonUniqueResultException.class)
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldFailWhenTryingToFetchOptionByJustPatientIdentifierIfConfiguredByLocation() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        iss.getAutoGenerationOption(patientIdentifierType);
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnOptionsNotConfiguredByLocation() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
        Location location = locationService.getLocation(4);
        AutoGenerationOption autoGenerationOption = iss.getAutoGenerationOption(patientIdentifierType, location);
        Assert.assertEquals(1, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionsByPatientIdentifier_shouldReturnAllAutoGenerationOptions() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        List<AutoGenerationOption> autoGenerationOptions = iss.getAutoGenerationOptions(patientIdentifierType);
        Assert.assertEquals(2, autoGenerationOptions.size());
        // poor man's check that the list contains both options
        Assert.assertTrue( (autoGenerationOptions.get(0).getId().equals(2) && autoGenerationOptions.get(1).getId().equals(3))
                            || (autoGenerationOptions.get(0).getId().equals(3) && autoGenerationOptions.get(1).getId().equals(2)));
    }

    @Test
    public void getAutoGenerationOptionById_shouldFetchAutoGenerationOptionByPrimaryKey() {
        AutoGenerationOption option = iss.getAutoGenerationOption(2);
        Assert.assertEquals(2, option.getId().intValue());
    }
}