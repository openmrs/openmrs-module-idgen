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

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.APIException;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.IdgenConstants;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.idgen.processor.IdentifierSourceProcessor;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	@Authorized( IdgenConstants.PRIV_MANAGE_IDENTIFIER_SOURCES )
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws APIException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 * @should delete an IdentifierSource from the system
	 */
	@Transactional
	@Authorized( IdgenConstants.PRIV_MANAGE_IDENTIFIER_SOURCES )
	public void purgeIdentifierSource(IdentifierSource identifierSource) throws APIException;
	
	/**
	 * Given a PatientIdentifierType, generates an identifier using the proper IdentifierSource for this server
	 * Returns null if this PatientIdentifierType is not set to be auto-generated
	 */
	@Transactional
	@Authorized(OpenmrsConstants.PRIV_EDIT_PATIENT_IDENTIFIERS)
	public String generateIdentifier(PatientIdentifierType type, String comment);

    /**
     * Given a PatientIdentifierType and Location, generates an identifier using the proper IdentifierSource
     * Returns null if this PatientIdentifierType is not set to be auto-generated
     */
    @Transactional
    @Authorized(OpenmrsConstants.PRIV_EDIT_PATIENT_IDENTIFIERS)
    public String generateIdentifier(PatientIdentifierType type, Location location, String comment);
	
	/**
	 * Generates a Single Identifiers from the given source
	 * @throws APIException
	 */
	@Transactional
	@Authorized( OpenmrsConstants.PRIV_EDIT_PATIENT_IDENTIFIERS )
	public String generateIdentifier(IdentifierSource source, String comment) throws APIException;
	
	/**
	 * Generates a List of Identifiers from the given source in the given quantity
	 * @throws APIException
	 */
	@Transactional
	@Authorized( IdgenConstants.PRIV_GENERATE_BATCH_OF_IDENTIFIERS )
	public List<String> generateIdentifiers(IdentifierSource source, Integer batchSize, String comment) throws APIException;
	
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
	@Authorized( IdgenConstants.PRIV_UPLOAD_BATCH_OF_IDENTIFIERS )
	public void addIdentifiersToPool(IdentifierPool pool, List<String> identifiers) throws APIException;
	
	/**
	 * Adds a batch of Identifiers to the given pool, from the attached source
	 * @throws APIException
	 */
	@Transactional
	@Authorized( IdgenConstants.PRIV_UPLOAD_BATCH_OF_IDENTIFIERS )
	public void addIdentifiersToPool(IdentifierPool pool, Integer batchSize) throws APIException;

    /**
     * @param id of auto generation option
     * @return the AutoGenerationOption

     */
    @Transactional(readOnly = true)
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws APIException;


    /**
	 * @param patient identifier type
     * @param location location
	 * @return the AutoGenerationOption that matches the given PatientIdentifierType and Location
     * @should return options that don't have a configured location
	 */
	@Transactional(readOnly = true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws APIException;

    /**
     * @param patient identifier type
     * @return all AutoGenerationOptions that match the given patient identifier type
     * @throws APIException
     */
    @Transactional(readOnly = true)
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws APIException;

    /**
     * @param patient identifier type
     * @return the AutoGenerationOption that matches the given PatientIdentifierType
     * @throws non-unique exception if more than one auto-generation option for this type
     */
    @Transactional(readOnly = true)
    public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws APIException;

	/**
	 * Persists a AutoGenerationOption, either as a save or update.
	 * @param option
	 * @return the AutoGenerationOption that was passed in
	 */
	@Transactional
	@Authorized( IdgenConstants.PRIV_MANAGE_AUTOGENERATION_OPTIONS )
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws APIException;
	
	/**
	 * Deletes a AutoGenerationOption from the database.
	 * @param option the AutoGenerationOption to purge
	 * @should delete an AutoGenerationOption from the system
	 */
	@Transactional
	@Authorized( IdgenConstants.PRIV_MANAGE_AUTOGENERATION_OPTIONS )
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws APIException;
	
	/**
	 * Retrieves the Log Entries that match the supplied parameters.  All parameters are optional.
	 * The identifier and comment parameters do a "like" match, the date parameters ignore time
	 */
	@Transactional(readOnly=true)
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws APIException;

	/**
	 * Convenience method that checks a pool's level and incrementally adds identifiers in batches of 100
	 * until the min available size is reached.  Only does something if the pool's source is a remote or sequential generator.
	 * @param pool
	 */
	@Transactional
	public void checkAndRefillIdentifierPool(IdentifierPool pool);
	
	/**
	 * Convenience method that returns the set of Patient Identifier Types that match certain AutoGeneration parameters
	 */
	@Transactional(readOnly=true)
	public List<PatientIdentifierType> getPatientIdentifierTypesByAutoGenerationOption(Boolean manualEntryEnabled, Boolean autoGenerationEnabled);

    /**
     * @param uuid
     * @return the identifier source with the given uuid
     */
    @Transactional(readOnly=true)
    IdentifierSource getIdentifierSourceByUuid(String uuid);

    /**
     * Updates sequenceValue of seq directly to the database via SQL, bypassing hibernate's caching
     * @param seq
     * @param sequenceValue
     */
    void saveSequenceValue(SequentialIdentifierGenerator seq, long sequenceValue);

    /**
     * Queries the database directly via SQL, bypassing hibernate's caching
     * @param seq
     * @return the sequence value from seq
     */
    Long getSequenceValue(SequentialIdentifierGenerator seq);

    /**
     * Internal method for generating identifiers; this should never be called directly--exposing it here is a
     * hack, but because evidently the @Transactional annotation will only be picked up by service methods and we need this
     * method to be transactional
     */
    List<String> generateIdentifiersInternal(Integer sourceId, Integer batchSize, String comment);

}
