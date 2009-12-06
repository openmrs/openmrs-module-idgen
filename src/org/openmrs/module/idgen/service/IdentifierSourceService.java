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

import java.util.List;
import java.util.Map;

import org.openmrs.PatientIdentifierType;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for IdentifierSource Service Methods
 */
@Transactional
public interface IdentifierSourceService extends OpenmrsService {
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return all IdentifierSource types that are supported
	 * @should return all supported IdentifierSource types
	 */
	@Transactional(readOnly = true)
	public List<Class<? extends IdentifierSource>> getIdentifierSourceTypes();

	/**
	 * @param id the id to retrieve for the given type
	 * @return the IdentifierSource that matches the given type and id
	 * @should return a saved sequential identifier generator
	 * @should return a saved remote identifier source
	 * @should return a saved identifier pool
	 */
	@Transactional(readOnly = true)
	public IdentifierSource getIdentifierSource(Integer id) throws APIException;
	
	/**
	 * @param includeRetired if true, also returns retired IdentifierSources
	 * @return all IdentifierSources
	 * @should return all identifier sources
	 */
	@Transactional(readOnly = true)
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws APIException;
	
	/**
	 * Returns all IdentifierSources by PatientIdentifierType
	 * @param includeRetired if true, also returns retired IdentifierSources
	 * @return all IdentifierSources by PatientIdentifierType
	 * @throws APIException
	 * @should return all identifier sources by type
	 */
	@Transactional(readOnly = true)
	public Map<PatientIdentifierType, List<IdentifierSource>> getIdentifierSourcesByType(boolean includeRetired) throws APIException;
	
	/**
	 * Persists a IdentifierSource, either as a save or update.
	 * @param identifierSource
	 * @return the IdentifierSource that was passed in
	 * @should save a sequential identifier generator for later retrieval
	 * @should save a rest identifier generator for later retrieval
	 * @should save an identifier pool for later retrieval
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 * @should delete an IdentifierSource from the system
	 */
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) throws APIException;
	
	/**
	 * Generates a List of Identifiers from the given source in the given quantity
	 * @throws APIException
	 */
	@Transactional
	public List<String> generateIdentifiers(IdentifierSource source, Integer batchSize) throws APIException;
	
	/**
	 * Returns an appropriate IdentifierSourceProcessor for the given IdentifierSource
	 * @param source
	 * @return
	 */
	@Transactional(readOnly = true)
	public IdentifierSourceProcessor getProcessor(IdentifierSource source);
	
	/**
	 * Registers a new Processor to handle a particular IdentifierSource
	 * @param type
	 * @param processorToRegister
	 * @throws APIException
	 */
	@Transactional(readOnly = true)
	public void registerProcessor(Class<? extends IdentifierSource> type, IdentifierSourceProcessor processorToRegister) throws APIException;

	/**
	 * Returns available identifiers from a pool 
	 */
	@Transactional(readOnly=true)
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) throws APIException;
	
	/**
	 * Returns Pooled Identifiers for the given source, with the given status options
	 */
	@Transactional(readOnly=true)
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) throws APIException;
	
	/**
	 * Adds a List of Identifiers to the given pool
	 * @throws APIException
	 */
	@Transactional
	public void addIdentifiersToPool(IdentifierPool pool, List<String> identifiers) throws APIException;
	
	/**
	 * Adds a batch of Identifiers to the given pool, from the attached source
	 * @throws APIException
	 */
	@Transactional
	public void addIdentifiersToPool(IdentifierPool pool, Integer batchSize) throws APIException;
}
