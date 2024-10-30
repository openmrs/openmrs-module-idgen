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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.NonUniqueResultException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenBaseTest;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.RemoteIdentifierSource;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.service.db.IdentifierSourceDAO;
import org.openmrs.test.Verifies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class IdentifierSourceServiceTest extends IdgenBaseTest {

	@Autowired
	IdentifierSourceService identifierSourceService;

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
	 * @see IdentifierSourceService#generateIdentifiers(IdentifierSource, Integer, String)
	 */
	@Ignore
	@Verifies(value = "should return batch of ID of correct size", method = "generateIdentifiers(IdentifierSource, integer, String)")
	public void generateIdentifiers_shouldReturnIdentifiersOfCorrectSize() throws Exception {
		IdentifierSource is = identifierSourceService.getIdentifierSource(1);
		List<String>  sig = identifierSourceService.generateIdentifiers(is, 7, "hello");
		Assert.assertEquals(sig.toString(), "[G-0, H-8, I-5, J-3, K-1, L-9, M-7]");
	}

	@Ignore
	public void generateIdentifiers_shouldReturnLocationPrefixedIdentifiers() {
		Context.getUserContext().setLocationId(13);
		IdentifierSource is = identifierSourceService.getIdentifierSource(8);
		List<String>  sig = identifierSourceService.generateIdentifiers(is, 4, null);
		Assert.assertEquals(sig.toString(), "[LOC_3-000005, LOC_3-000006, LOC_3-000007, LOC_3-000008]");
		// Change location
		Context.getUserContext().setLocationId(11);
		sig = identifierSourceService.generateIdentifiers(is, 4, null);
		Assert.assertEquals(sig.toString(), "[LOC_1-000009, LOC_1-000010, LOC_1-000011, LOC_1-000012]");
	}

	/**
	 * @see IdentifierSourceService#getAllIdentifierSources(boolean)
	 */
	@Test
	@Verifies(value = "should return all identifier sources", method = "getAllIdentifierSources(boolean)")
	public void getAllIdentifierSources_shouldReturnAllIdentifierSources() throws Exception {
		List<IdentifierSource>  sig = identifierSourceService.getAllIdentifierSources(false);
		Assert.assertTrue(sig.size() == 8);
	}

	/**
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	@Test
	@Verifies(value = "should return a saved sequential identifier generator", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedSequentialIdentifierGenerator() throws Exception {
		SequentialIdentifierGenerator sig = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
		Assert.assertEquals(sig.getName(), "Test Sequential Generator");
		Assert.assertNotNull(sig.getBaseCharacterSet());
	}

	/**
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 */
	@Test
	@Verifies(value = "should return a saved remote identifier source", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedRestIdentifierGenerator() throws Exception {
		RemoteIdentifierSource rig = (RemoteIdentifierSource)identifierSourceService.getIdentifierSource(2);
		Assert.assertEquals(rig.getName(), "Test Remote Source");
		Assert.assertNotNull(rig.getUrl());
	}

	/**
	 * @see IdentifierSourceService#getIdentifierSource(Integer)
	 *
	 */
	@Test
	@Verifies(value = "should return a saved identifier pool", method = "getIdentifierSource(Integer)")
	public void getIdentifierSource_shouldReturnASavedIdentifierPool() throws Exception {
		IdentifierPool idpool = (IdentifierPool)identifierSourceService.getIdentifierSource(3);
		Assert.assertEquals(idpool.getName(), "Test Identifier Pool");
		Assert.assertEquals(idpool.getBatchSize().intValue(), 1000);
		Assert.assertEquals(5, idpool.getAvailableIdentifiers().size());
		Assert.assertEquals(idpool.getUsedIdentifiers().size(), 0);
	}

	/**
	 * @see IdentifierSourceService#retireIdentifierSource(IdentifierSource, String)
	 *
	 */
	@Test
	@Verifies(value = "should retire an IdentifierSource from the system", method = "retireIdentifierSource(IdentifierSource, String)")
	public void retireIdentifierSource_shouldRetireAnIdentifierSourceFromTheSystem() throws Exception {
		IdentifierSource identifierSource = identifierSourceService.getIdentifierSource(2);
		Assert.assertFalse(identifierSourceService.getIdentifierSource(2).isRetired());
		identifierSourceService.retireIdentifierSource(identifierSource, "testing");
		Assert.assertTrue(identifierSourceService.getIdentifierSource(2).isRetired());
	}

	/**
	 * @see IdentifierSourceService#purgeIdentifierSource(IdentifierSource)
	 *
	 */
	@Test
	@Verifies(value = "should delete an IdentifierSource from the system", method = "purgeIdentifierSource(IdentifierSource)")
	public void purgeIdentifierSource_shouldDeleteAnIdentifierSourceFromTheSystem() throws Exception {
		IdentifierSource identifierSource = identifierSourceService.getIdentifierSource(2);
		identifierSourceService.purgeIdentifierSource(identifierSource);
		Assert.assertNull(identifierSourceService.getIdentifierSource(2));
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
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
		IdentifierSource source = identifierSourceService.saveIdentifierSource(sig);

		Assert.assertNotNull(source.getId());
		IdentifierSource s = identifierSourceService.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getClass(), SequentialIdentifierGenerator.class);
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(((SequentialIdentifierGenerator) s).getBaseCharacterSet(), baseChars);
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
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
		IdentifierSource source = identifierSourceService.saveIdentifierSource(idgen);

		Assert.assertNotNull(source.getId());
		IdentifierSource s = identifierSourceService.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getClass(), RemoteIdentifierSource.class);
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(((RemoteIdentifierSource) s).getUrl(), url);
	}

	/**
	 * @see IdentifierSourceService#saveIdentifierSource(IdentifierSource)
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

		IdentifierSource source = identifierSourceService.saveIdentifierSource(pool);

		Assert.assertNotNull(source.getId());
		IdentifierPool s = (IdentifierPool)identifierSourceService.getIdentifierSource(source.getId());
		Assert.assertEquals(s.getName(), name);
		Assert.assertEquals(s.getBatchSize().intValue(), batchSize);
		Assert.assertEquals(4, s.getAvailableIdentifiers().size());
	}

	@Test
	public void getIdentifierSourceByUuid_shouldGetSource() {
		IdentifierSource identifierSource = identifierSourceService.getIdentifierSourceByUuid("0d47284f-9e9b-4a81-a88b-8bb42bc0a903");
		Assert.assertEquals(3, identifierSource.getId().intValue());
	}

	@Test
	public void getIdentifierSourcesByPatientIdentifierType_shouldGetIdentifierSourcesByPatientIdentifierType() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
		List<IdentifierSource> identifierSource = identifierSourceService.getIdentifierSourcesByType(patientIdentifierType);
		Assert.assertEquals(2, identifierSource.size());
	}

	@Test
	public void getAutoGenerationOptionByUuid_shouldGetAutoGenerationOptionByUuid() {
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOptionByUuid("599c5a90-1937-42de-aa7d-79bd9f9acca7");
		Assert.assertEquals(1, autoGenerationOption.getId().intValue());
	}

	@Test
	public void getAutoGenerationOptionByPatientIdentifier_shouldGetAutoGenerationOptionByIdentifier() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType);
		Assert.assertEquals(1, autoGenerationOption.getId().intValue());
	}

	@Test
	public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldGetAutoGenerationOptionByIdentifier() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
		Location location = locationService.getLocation(2);
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
		Assert.assertEquals(3, autoGenerationOption.getId().intValue());
	}

	@Test
	public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnNullIfNoOptionForLocation() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
		Location location = locationService.getLocation(4);
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
		Assert.assertNull(autoGenerationOption);
	}

	@Test(expected = NonUniqueResultException.class)
	public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldFailWhenTryingToFetchOptionByJustPatientIdentifierIfConfiguredByLocation() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
		identifierSourceService.getAutoGenerationOption(patientIdentifierType);
	}

	@Test
	public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnOptionsNotConfiguredByLocation() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
		Location location = locationService.getLocation(4);
		AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
		Assert.assertEquals(1, autoGenerationOption.getId().intValue());
	}

	@Test
	public void getAutoGenerationOptionsByPatientIdentifier_shouldReturnAllAutoGenerationOptions() {
		PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
		List<AutoGenerationOption> autoGenerationOptions = identifierSourceService.getAutoGenerationOptions(patientIdentifierType);
		Assert.assertEquals(2, autoGenerationOptions.size());
		// poor man's check that the list contains both options
		Assert.assertTrue( (autoGenerationOptions.get(0).getId().equals(2) && autoGenerationOptions.get(1).getId().equals(3))
				|| (autoGenerationOptions.get(0).getId().equals(3) && autoGenerationOptions.get(1).getId().equals(2)));
	}

	@Test
	public void getAutoGenerationOptionById_shouldFetchAutoGenerationOptionByPrimaryKey() {
		AutoGenerationOption option = identifierSourceService.getAutoGenerationOption(2);
		Assert.assertEquals(2, option.getId().intValue());
	}

	@Test
	public void shouldReturnNullIfNoLogEntriesFound() {
		// Source 4 has no log entries in the test data
		IdentifierSource is = identifierSourceService.getIdentifierSource(4);
		LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
		Assert.assertNull(entry);
	}

	@Test
	public void shouldReturnMostRecentLogEntryOrderedByDateGeneratedAndId() {
		// Source 1 has 3 entries.  Order should be 3, 1, 2
		IdentifierSource is = identifierSourceService.getIdentifierSource(1);
		LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
		Assert.assertEquals(2, entry.getId().intValue());
		Assert.assertEquals("New Visit", entry.getComment());
		Assert.assertEquals(is, entry.getSource());
		Assert.assertEquals(1, entry.getGeneratedBy().getId().intValue());
		Assert.assertEquals("100HH9", entry.getIdentifier());
	}

	@Ignore
	public void shouldReturnMostRecentLogEntryForSourceWhenGeneratedInBatch() {
		SequentialIdentifierGenerator is = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
		List<String>  sig = identifierSourceService.generateIdentifiers(is, 1000, "hello");
		LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
		Assert.assertNotNull(entry);
		Assert.assertEquals("hello", entry.getComment());
		Assert.assertEquals(is, entry.getSource());
		Assert.assertEquals(Context.getAuthenticatedUser(), entry.getGeneratedBy());
		assertDatesEqual(new Date(), entry.getDateGenerated(), "yyyy-MM-dd");

		// This source has a nextSequenceValue = 6, so last id in a batch of 1000 should be 1005
		String expected = is.getIdentifierForSeed(1005L);
		Assert.assertEquals(expected, entry.getIdentifier());
	}

	@Ignore
	public void shouldReturnMostRecentLogEntryForSourceWhenGeneratedInSequence() {
		SequentialIdentifierGenerator is = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
		for (int i=0; i<1000; i++) {
			List<String>  sig = identifierSourceService.generateIdentifiers(is, 1, "Sequence-" + i);
			LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
			Assert.assertNotNull(entry);
			Assert.assertEquals("Sequence-" + i, entry.getComment());
			Assert.assertEquals(is, entry.getSource());
			Assert.assertEquals(Context.getAuthenticatedUser(), entry.getGeneratedBy());
			assertDatesEqual(new Date(), entry.getDateGenerated(), "yyyy-MM-dd");

			// This source has a nextSequenceValue = 6, so last id in a batch should be 6+i
			long expectedSeed = 6 + i;
			String expected = is.getIdentifierForSeed(expectedSeed);
			Assert.assertEquals(expected, entry.getIdentifier());
		}
	}

	protected void assertDatesEqual(Date expected, Date actual, String dateFormat) {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		Assert.assertEquals(df.format(expected), df.format(actual));
	}
}
