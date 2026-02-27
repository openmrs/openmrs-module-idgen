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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdentifierSourceServiceTest extends IdgenBaseTest {

	@Autowired
	IdentifierSourceService identifierSourceService;

	@Autowired
    @Qualifier("patientService")
    PatientService patientService;

    @Autowired
    @Qualifier("locationService")
    LocationService locationService;

    @BeforeEach
    public void beforeEachTest() {
		executeDataSet("org/openmrs/module/idgen/include/TestData.xml");
    }
    
	/**
	 * @see {@link IdentifierSourceService#generateIdentifiers(IdentifierSource, Integer, String)}
	 */
	@Test
	public void generateIdentifiers_shouldReturnIdentifiersOfCorrectSize() throws Exception {
		IdentifierSource is = identifierSourceService.getIdentifierSource(1);
		List<String>  sig = identifierSourceService.generateIdentifiers(is, 7, "hello");
		assertEquals("[G-0, H-8, I-5, J-3, K-1, L-9, M-7]", sig.toString());
	}
	
	@Test
	public void generateIdentifiers_shouldReturnLocationPrefixedIdentifiers() {
		Context.getUserContext().setLocationId(13);
		IdentifierSource is = identifierSourceService.getIdentifierSource(8);
		List<String>  sig = identifierSourceService.generateIdentifiers(is, 4, null);
		assertEquals("[LOC_3-000005, LOC_3-000006, LOC_3-000007, LOC_3-000008]", sig.toString());
		// Change location
		Context.getUserContext().setLocationId(11);
		sig = identifierSourceService.generateIdentifiers(is, 4, null);
		assertEquals("[LOC_1-000009, LOC_1-000010, LOC_1-000011, LOC_1-000012]", sig.toString());
	}

	/**
	 * @see {@link IdentifierSourceService#getAllIdentifierSources(boolean)}
	 */
	@Test
	public void getAllIdentifierSources_shouldReturnAllIdentifierSources() throws Exception {
		List<IdentifierSource>  sig = identifierSourceService.getAllIdentifierSources(false);
        assertEquals(8, sig.size());
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 */
	@Test
	public void getIdentifierSource_shouldReturnASavedSequentialIdentifierGenerator() throws Exception {
		SequentialIdentifierGenerator sig = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
		assertEquals("Test Sequential Generator", sig.getName());
		assertNotNull(sig.getBaseCharacterSet());
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 */
	@Test
	public void getIdentifierSource_shouldReturnASavedRestIdentifierGenerator() throws Exception {
		RemoteIdentifierSource rig = (RemoteIdentifierSource)identifierSourceService.getIdentifierSource(2);
		assertEquals("Test Remote Source", rig.getName());
		assertNotNull(rig.getUrl());
	}

	/**
	 * @see {@link IdentifierSourceService#getIdentifierSource(Integer)}
	 * 
	 */
	@Test
	public void getIdentifierSource_shouldReturnASavedIdentifierPool() throws Exception {
		IdentifierPool idpool = (IdentifierPool)identifierSourceService.getIdentifierSource(3);
		assertEquals("Test Identifier Pool", idpool.getName());
		assertEquals(1000, idpool.getBatchSize().intValue());
		assertEquals(5, idpool.getAvailableIdentifiers().size());
		assertEquals(0, idpool.getUsedIdentifiers().size());
	}
	
	/**
	 * @see {@link IdentifierSourceService#retireIdentifierSource(IdentifierSource, String)}
	 * 
	 */
	@Test
	public void retireIdentifierSource_shouldRetireAnIdentifierSourceFromTheSystem() throws Exception {
		IdentifierSource identifierSource = identifierSourceService.getIdentifierSource(2);
		assertFalse(identifierSourceService.getIdentifierSource(2).isRetired());
		identifierSourceService.retireIdentifierSource(identifierSource, "testing");
		assertTrue(identifierSourceService.getIdentifierSource(2).isRetired());
	}

	/**
	 * @see {@link IdentifierSourceService#purgeIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	public void purgeIdentifierSource_shouldDeleteAnIdentifierSourceFromTheSystem() throws Exception {
		IdentifierSource identifierSource = identifierSourceService.getIdentifierSource(2);
		identifierSourceService.purgeIdentifierSource(identifierSource);
		assertNull(identifierSourceService.getIdentifierSource(2));
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	public void saveIdentifierSource_shouldSaveASequentialIdentifierGeneratorForLaterRetrieval() throws Exception {
		
		String name = "Sample Id Gen";
		String baseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		SequentialIdentifierGenerator sig = new SequentialIdentifierGenerator();
		sig.setName(name);
		sig.setBaseCharacterSet(baseChars);
		sig.setFirstIdentifierBase("1");
		sig.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		IdentifierSource source = identifierSourceService.saveIdentifierSource(sig);
		
		assertNotNull(source.getId());
		IdentifierSource s = identifierSourceService.getIdentifierSource(source.getId());
		assertEquals(SequentialIdentifierGenerator.class, s.getClass());
		assertEquals(name, s.getName());
		assertEquals(baseChars, ((SequentialIdentifierGenerator) s).getBaseCharacterSet());
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
	public void saveIdentifierSource_shouldSaveARestIdentifierGeneratorForLaterRetrieval() throws Exception {
		String name = "Sample Id Gen";
		String url = "http://localhost";
		
		RemoteIdentifierSource idgen = new RemoteIdentifierSource();
		idgen.setName(name);
		idgen.setUrl(url);
		idgen.setIdentifierType(Context.getPatientService().getPatientIdentifierType(1));
		IdentifierSource source = identifierSourceService.saveIdentifierSource(idgen);
		
		assertNotNull(source.getId());
		IdentifierSource s = identifierSourceService.getIdentifierSource(source.getId());
		assertEquals(RemoteIdentifierSource.class, s.getClass());
		assertEquals(name, s.getName());
		assertEquals(url, ((RemoteIdentifierSource) s).getUrl());
	}

	/**
	 * @see {@link IdentifierSourceService#saveIdentifierSource(IdentifierSource)}
	 * 
	 */
	@Test
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
		
		assertNotNull(source.getId()); 
		IdentifierPool s = (IdentifierPool)identifierSourceService.getIdentifierSource(source.getId());
		assertEquals(name, s.getName());
		assertEquals(batchSize, s.getBatchSize().intValue());
		assertEquals(4, s.getAvailableIdentifiers().size());
	}

    @Test
    public void getIdentifierSourceByUuid_shouldGetSource() {
        IdentifierSource identifierSource = identifierSourceService.getIdentifierSourceByUuid("0d47284f-9e9b-4a81-a88b-8bb42bc0a903");
        assertEquals(3, identifierSource.getId().intValue());
    }
    
    @Test
    public void getIdentifierSourcesByPatientIdentifierType_shouldGetIdentifierSourcesByPatientIdentifierType() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
        List<IdentifierSource> identifierSource = identifierSourceService.getIdentifierSourcesByType(patientIdentifierType);
        assertEquals(2, identifierSource.size());
    }

    @Test
    public void getAutoGenerationOptionByUuid_shouldGetAutoGenerationOptionByUuid() {
    	AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOptionByUuid("599c5a90-1937-42de-aa7d-79bd9f9acca7");
    	assertEquals(1, autoGenerationOption.getId().intValue());
    }
    
    @Test
    public void getAutoGenerationOptionByPatientIdentifier_shouldGetAutoGenerationOptionByIdentifier() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType);
        assertEquals(1, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldGetAutoGenerationOptionByIdentifier() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        Location location = locationService.getLocation(2);
        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
        assertEquals(3, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnNullIfNoOptionForLocation() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        Location location = locationService.getLocation(4);
        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
        assertNull(autoGenerationOption);
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldFailWhenTryingToFetchOptionByJustPatientIdentifierIfConfiguredByLocation() {
		assertThrows(NonUniqueResultException.class, () -> {
			PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
			identifierSourceService.getAutoGenerationOption(patientIdentifierType);
		});
    }

    @Test
    public void getAutoGenerationOptionByPatientIdentifierAndLocation_shouldReturnOptionsNotConfiguredByLocation() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(1);
        Location location = locationService.getLocation(4);
        AutoGenerationOption autoGenerationOption = identifierSourceService.getAutoGenerationOption(patientIdentifierType, location);
        assertEquals(1, autoGenerationOption.getId().intValue());
    }

    @Test
    public void getAutoGenerationOptionsByPatientIdentifier_shouldReturnAllAutoGenerationOptions() {
        PatientIdentifierType patientIdentifierType = patientService.getPatientIdentifierType(2);
        List<AutoGenerationOption> autoGenerationOptions = identifierSourceService.getAutoGenerationOptions(patientIdentifierType);
        assertEquals(2, autoGenerationOptions.size());
        // poor man's check that the list contains both options
        assertTrue( (autoGenerationOptions.get(0).getId().equals(2) && autoGenerationOptions.get(1).getId().equals(3))
                            || (autoGenerationOptions.get(0).getId().equals(3) && autoGenerationOptions.get(1).getId().equals(2)));
    }

    @Test
    public void getAutoGenerationOptionById_shouldFetchAutoGenerationOptionByPrimaryKey() {
        AutoGenerationOption option = identifierSourceService.getAutoGenerationOption(2);
        assertEquals(2, option.getId().intValue());
    }

	@Test
	public void shouldReturnNullIfNoLogEntriesFound() {
		// Source 6 has no log entries in the test data
		IdentifierSource is = identifierSourceService.getIdentifierSource(6);
		LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
		assertNull(entry);
	}

	@Test
	public void shouldReturnMostRecentLogEntryOrderedByDateGeneratedAndId() {
		// Source 1 has 3 entries. Order should be 2 (2017-10-03), 3 (2017-10-01), 1 (2016-10-03)
		IdentifierSource is = identifierSourceService.getIdentifierSource(1);
		LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
		assertEquals(2, entry.getId().intValue());
		assertEquals("New Visit", entry.getComment());
		assertEquals(is, entry.getSource());
		assertEquals(1, entry.getGeneratedBy().getId().intValue());
		assertEquals("100HH9", entry.getIdentifier());
	}

    @Test
	public void shouldReturnMostRecentLogEntryForSourceWhenGeneratedInBatch() {
	    SequentialIdentifierGenerator is = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
	    identifierSourceService.generateIdentifiers(is, 1000, "hello");
	    LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
	    assertNotNull(entry);
	    assertEquals("hello", entry.getComment());
	    assertEquals(is, entry.getSource());
	    assertEquals(Context.getAuthenticatedUser(), entry.getGeneratedBy());
	    assertDatesEqual(new Date(), entry.getDateGenerated(), "yyyy-MM-dd");

	    // This source has a nextSequenceValue = 6, so last id in a batch of 1000 should be 1005
	    String expected = is.getIdentifierForSeed(1005L);
	    assertEquals(expected, entry.getIdentifier());
    }

	@Test
	public void shouldReturnMostRecentLogEntryForSourceWhenGeneratedInSequence() {
		SequentialIdentifierGenerator is = (SequentialIdentifierGenerator)identifierSourceService.getIdentifierSource(1);
		for (int i=0; i<1000; i++) {
			identifierSourceService.generateIdentifiers(is, 1, "Sequence-" + i);
			LogEntry entry = identifierSourceService.getMostRecentLogEntry(is);
			assertNotNull(entry);
			assertEquals("Sequence-" + i, entry.getComment());
			assertEquals(is, entry.getSource());
			assertEquals(Context.getAuthenticatedUser(), entry.getGeneratedBy());
			assertDatesEqual(new Date(), entry.getDateGenerated(), "yyyy-MM-dd");

			// This source has a nextSequenceValue = 6, so last id in a batch should be 6+i
			long expectedSeed = 6 + i;
			String expected = is.getIdentifierForSeed(expectedSeed);
			assertEquals(expected, entry.getIdentifier());
		}
	}

    protected void assertDatesEqual(Date expected, Date actual, String dateFormat) {
	    SimpleDateFormat df = new SimpleDateFormat(dateFormat);
	    assertEquals(df.format(expected), df.format(actual));
    }
}
