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
package org.openmrs.module.idgen.service.db;

import org.openmrs.Location;
import org.openmrs.PatientIdentifierType;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.module.idgen.AutoGenerationOption;
import org.openmrs.module.idgen.IdentifierPool;
import org.openmrs.module.idgen.IdentifierSource;
import org.openmrs.module.idgen.LogEntry;
import org.openmrs.module.idgen.PooledIdentifier;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Interface for IdentifierSource Service Methods
 */
@Transactional
public interface IdentifierSourceDAO {
	
	/**
	 * @param id the id to retrieve for the given type
	 * @return the IdentifierSource that matches the given type and id
	 */
	@Transactional(readOnly = true)
	public IdentifierSource getIdentifierSource(Integer id) throws DAOException;
	
	/**
	 * @param includeRetired if true, also returns retired IdentifierSources
	 * @return all IdentifierSources
	 * @should return all identifier sources
	 */
	@Transactional(readOnly = true)
	public List<IdentifierSource> getAllIdentifierSources(boolean includeRetired) throws DAOException;

	/**
	 * Persists a IdentifierSource, either as a save or update.
	 * @param identifierSource
	 * @return the IdentifierSource that was passed in
	 */
	@Transactional
	public IdentifierSource saveIdentifierSource(IdentifierSource identifierSource) throws DAOException;
	
	/**
	 * Deletes a IdentifierSource from the database.
	 * @param identifierSource the IdentifierSource to purge
	 */
	@Transactional
	public void purgeIdentifierSource(IdentifierSource identifierSource) throws DAOException;
	
	/**
	 * Returns available identifiers from a pool 
	 */
	@Transactional(readOnly=true)
	public List<PooledIdentifier> getAvailableIdentifiers(IdentifierPool pool, int quantity) throws DAOException;
	
	/**
	 * Returns Pooled Identifiers for the given source, with the given status options
	 */
	@Transactional(readOnly=true)
	public int getQuantityInPool(IdentifierPool pool, boolean availableOnly, boolean usedOnly) throws DAOException;

    /**
     * @param autoGenerationOptionId
     * @return AutoGenerationOption
     */
    @Transactional(readOnly=true)
    public AutoGenerationOption getAutoGenerationOption(Integer autoGenerationOptionId) throws DAOException;

	/**
	 * @param type
	 * @param location
     * @return the AutoGenerationOption that matches the given PatientIdentifierType and location
     * @should return AutoGenerationOptions that do not have an associated location
	 */
	@Transactional(readOnly=true)
	public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type, Location location) throws DAOException;

    /**
     * @param type
     * @return the AutoGenerationOption that matches the given PatientIdentifierType
     * @throws DAOException
     */
    @Transactional(readOnly = true)
    public List<AutoGenerationOption> getAutoGenerationOptions(PatientIdentifierType type) throws DAOException;

    /**
     * @param type
     * @return the AutoGenerationOption that matches the given PatientIdentifierType
     */
    @Transactional(readOnly=true)
    public AutoGenerationOption getAutoGenerationOption(PatientIdentifierType type) throws DAOException;

    /**
	 * Persists a AutoGenerationOption, either as a save or update.
	 * @param option
	 * @return the AutoGenerationOption that was passed in
	 */
	@Transactional
	public AutoGenerationOption saveAutoGenerationOption(AutoGenerationOption option) throws DAOException;
	
	/**
	 * Deletes a AutoGenerationOption from the database.
	 * @param option the AutoGenerationOption to purge
	 * @should delete an AutoGenerationOption from the system
	 */
	@Transactional
	public void purgeAutoGenerationOption(AutoGenerationOption option) throws DAOException;
	
	/**
	 * Saves a new Log Entry
	 * @throws DAOException
	 */
	public LogEntry saveLogEntry(LogEntry logEntry) throws DAOException;
	
	/**
	 * Retrieves the Log Entries that match the supplied parameters.  All parameters are optional.
	 * @throws DAOException
	 */
	@Transactional(readOnly=true)
	public List<LogEntry> getLogEntries(IdentifierSource source, Date fromDate, Date toDate, 
										String identifier, User generatedBy, String comment) throws DAOException;

    /**
     * @param uuid
     * @return the IdentifierSource with the given uuid
     */
    IdentifierSource getIdentifierSourceByUuid(String uuid);
    
    /**
     * @see IdentifierSourceService#getIdentifierSourcesByType(PatientIdentifierType)
     */
    List<IdentifierSource> getIdentifierSourcesByType(PatientIdentifierType patientIdentifierType);

    /**
     * Updates generator's sequenceValue in the database via SQL, bypassing Hibernate caches
     * @param generator
     * @param sequenceValue
     */
    void saveSequenceValue(SequentialIdentifierGenerator generator, long sequenceValue);

    /**
     * Gets generator's sequenceValue from the database via SQL, bypassing Hibernate caches
     * @param generator
     * @return
     */
    Long getSequenceValue(SequentialIdentifierGenerator generator);

    /**
     * Refresh an identifier source
     * @param source
     */
    void refreshIdentifierSource(IdentifierSource source);
}
